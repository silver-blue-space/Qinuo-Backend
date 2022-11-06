package com.qinuo.service;


import com.qinuo.domain.WxOpenDataDTO;
import com.qinuo.entity.WxUserEntity;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.List;

/**
 * 微信用户
 *
 * @author www.joolun.com
 * @date 2019-03-25 15:39:39
 */
public interface WxUserService {

	/**
	 * 修改用户备注
	 * @param entity
	 * @return
	 */
	boolean updateRemark(WxUserEntity entity) throws WxErrorException;

	/**
	 * 认识标签
	 * @param taggingType
	 * @param tagId
	 * @param openIds
	 * @throws WxErrorException
	 */
	void tagging(String taggingType, Long tagId, String[] openIds) throws WxErrorException;

	/**
	 * 根据openId获取用户
	 * @param openId
	 * @return
	 */
	WxUserEntity getByOpenId(String openId);

	/**
	 * 小程序登录
	 * @param appId
	 * @param jsCode
	 * @return
	 */
	WxUserEntity loginMa(String appId, String jsCode) throws WxErrorException;

	/**
	 * 新增、更新微信用户
	 * @param wxOpenDataDTO
	 * @return
	 */
	WxUserEntity saveOrUptateWxUser(WxOpenDataDTO wxOpenDataDTO);

	/**
	 * 小程序用户列表查询
	 * @param wxUser
	 * @return
	 */
	List<WxUserEntity> selectWxUserList(WxUserEntity wxUser);

	/**
	 * 微信用户详细信息
	 * @param id
	 * @return
	 */
	WxUserEntity selectWxUserById(String id);

	/***
	 * 添加微信用户
	 * @param wxUser
	 * @return
	 */
	int insertWxUser(WxUserEntity wxUser);

	/**
	 * 更新微信用户
	 * @param wxUser
	 * @return
	 */
	int updateWxUser(WxUserEntity wxUser);

	/**
	 * 删除微信用户
	 * @param ids
	 * @return
	 */
	int deleteWxUserByIds(String[] ids);
}
