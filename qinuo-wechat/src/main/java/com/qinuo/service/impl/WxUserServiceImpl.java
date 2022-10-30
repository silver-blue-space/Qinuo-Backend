package com.qinuo.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.qinuo.config.WxMaConfiguration;
import com.qinuo.constant.ConfigConstant;
import com.qinuo.constant.WxMaConstants;
import com.qinuo.dao.intf.WxUserDao;
import com.qinuo.entity.ThirdSession;
import com.qinuo.entity.WxOpenDataDTO;
import com.qinuo.entity.WxUserEntity;
import com.qinuo.service.WxUserService;
import com.qinuo.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.api.WxMpUserTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 微信用户
 *
 * @author www.joolun.com
 * @date 2019-03-25 15:39:39
 */
@Slf4j
@Service
@AllArgsConstructor
public class WxUserServiceImpl  implements WxUserService {

	private final WxMpService wxService;
	private final RedisTemplate redisTemplate;

	@Autowired
	private WxUserDao wxUserDao;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateRemark(WxUserEntity entity) throws WxErrorException {
		String id = entity.getId();
		String remark = entity.getRemark();
		String openId = entity.getOpenId();
		entity = new WxUserEntity();
		entity.setId(id);
		entity.setRemark(remark);
		wxUserDao.updateById(entity);
		WxMpUserService wxMpUserService = wxService.getUserService();
		wxMpUserService.userUpdateRemark(openId,remark);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void tagging(String taggingType,Long tagId,String[] openIds) throws WxErrorException {
		WxMpUserTagService wxMpUserTagService = wxService.getUserTagService();
		WxUserEntity wxUser;
		if("tagging".equals(taggingType)){
			for(String openId : openIds){
				wxUser = wxUserDao.selectByOpenId(openId);
				String tagidList = wxUser.getTagidList();
                List<String> list = new ArrayList<>();
				if(!StringUtils.isEmpty(tagidList) && !",".equals(tagidList)){
                    list = Arrays.asList(tagidList.split(","));
                }
				if(!list.contains(""+tagId) && Objects.nonNull(tagId)){
					list.add(""+tagId);
					wxUser.setTagidList(String.join(",",list));
                    wxUserDao.updateById(wxUser);
				}
			}
			wxMpUserTagService.batchTagging(tagId,openIds);
		}
		if("unTagging".equals(taggingType)){
			for(String openId : openIds){
                wxUser = wxUserDao.selectByOpenId(openId);
                String tagidList = wxUser.getTagidList();
                List<String> list = new ArrayList<>();
                if(!StringUtils.isEmpty(tagidList) && !",".equals(tagidList)){
                    list = Arrays.asList(tagidList.split(","));
                }
				if(list.contains(""+tagId) && Objects.nonNull(tagId)){
					list.remove(tagId);
                    wxUser.setTagidList(String.join(",",list));
                    wxUserDao.updateById(wxUser);
				}
			}
			wxMpUserTagService.batchUntagging(tagId,openIds);
		}
	}



	@Override
	public WxUserEntity getByOpenId(String openId){
		return  wxUserDao.selectByOpenId(openId);
	}

	/***
	 * 小程序用户登录
	 * @param appId
	 * @param jsCode
	 * @return
	 * @throws WxErrorException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public WxUserEntity loginMa(String appId, String jsCode) throws WxErrorException {
		WxMaJscode2SessionResult jscode2session = WxMaConfiguration.getMaService(appId).jsCode2SessionInfo(jsCode);
        WxUserEntity wxUser = this.getByOpenId(jscode2session.getOpenid());
		if(wxUser==null) {
			//新增微信用户
			wxUser = new WxUserEntity();
			wxUser.setAppType(ConfigConstant.WX_APP_TYPE_1);
			wxUser.setOpenId(jscode2session.getOpenid());
			wxUser.setSessionKey(jscode2session.getSessionKey());
			wxUser.setUnionId(jscode2session.getUnionid());
			try{
                wxUserDao.save(wxUser);
			}catch (DuplicateKeyException e){
				if(e.getMessage().contains("uk_appid_openid")){
					wxUser = this.getByOpenId(wxUser.getOpenId());
				}
			}
		}else {
			//更新SessionKey
			wxUser.setAppType(ConfigConstant.WX_APP_TYPE_1);
			wxUser.setOpenId(jscode2session.getOpenid());
			wxUser.setSessionKey(jscode2session.getSessionKey());
			wxUser.setUnionId(jscode2session.getUnionid());
            wxUserDao.updateById(wxUser);
		}

		String thirdSessionKey = UUID.randomUUID().toString();
		ThirdSession thirdSession = new ThirdSession();
		thirdSession.setAppId(appId);
		thirdSession.setSessionKey(wxUser.getSessionKey());
		thirdSession.setWxUserId(wxUser.getId());
		thirdSession.setOpenId(wxUser.getOpenId());
		//将3rd_session和用户信息存入redis，并设置过期时间
		String key = WxMaConstants.THIRD_SESSION_BEGIN + ":" + thirdSessionKey;
		redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(thirdSession) , WxMaConstants.TIME_OUT_SESSION, TimeUnit.HOURS);
		wxUser.setSessionKey(thirdSessionKey);
		return wxUser;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WxUserEntity saveOrUptateWxUser(WxOpenDataDTO wxOpenDataDTO) {
		WxMaUserService wxMaUserService = WxMaConfiguration.getMaService(wxOpenDataDTO.getAppId()).getUserService();
		WxMaUserInfo wxMaUserInfo = wxMaUserService.getUserInfo(wxOpenDataDTO.getSessionKey(), wxOpenDataDTO.getEncryptedData(), wxOpenDataDTO.getIv());
		WxUserEntity wxUser = new WxUserEntity();
		BeanUtil.copyProperties(wxMaUserInfo,wxUser);
		wxUser.setId(wxOpenDataDTO.getUserId());
		wxUser.setSex(wxMaUserInfo.getGender());
		wxUser.setHeadimgUrl(wxMaUserInfo.getAvatarUrl());
        wxUserDao.updateById(wxUser);
		wxUser = wxUserDao.selectById(wxUser.getId());
		return wxUser;
	}

	@Override
	public List<WxUserEntity> selectWxUserList(WxUserEntity wxUser) {
		return wxUserDao.selectByEntity(wxUser);
	}

	@Override
	public WxUserEntity selectWxUserById(String id) {
		return wxUserDao.selectById(id);
	}

	@Override
	public int insertWxUser(WxUserEntity wxUser) {
		return wxUserDao.save(wxUser);
	}

	@Override
	public int updateWxUser(WxUserEntity wxUser) {
		return wxUserDao.updateById(wxUser) ? 1 : 0;
	}

	@Override
	public int deleteWxUserByIds(String[] ids) {
		return wxUserDao.deleteByIds(Arrays.asList(ids));
	}
}
