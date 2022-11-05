/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.joolun.com
 */
package com.qinuo.api;

import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.entity.LoginMaDTO;
import com.qinuo.entity.WxOpenDataDTO;
import com.qinuo.entity.WxUserEntity;
import com.qinuo.service.WxUserService;
import com.qinuo.utils.ThirdSessionHolder;
import com.qinuo.utils.WxMaUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *  小程序用户
 *
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/weixin/api/ma/wxuser")
public class WxUserApi {

	@Autowired
	private final WxUserService wxUserService;
	/**
	 * 小程序用户登录
	 * @param request
	 * @param loginMaDTO
	 * @return
	 */
	@PostMapping("/login")
	public AjaxResult login(HttpServletRequest request, @RequestBody LoginMaDTO loginMaDTO){
		try {
			WxUserEntity wxUser = wxUserService.loginMa(WxMaUtil.getAppId(request),loginMaDTO.getJsCode());
			return AjaxResult.success(wxUser);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(e.getMessage());
		}
	}

	/**
	 * 获取用户信息
	 * @param
	 * @return
	 */
	@GetMapping
	public AjaxResult get(){
		String id = ThirdSessionHolder.getThirdSession().getWxUserId();
		return AjaxResult.success(wxUserService.selectWxUserById(id));
	}

	/**
	 * 保存用户信息
	 * @param wxOpenDataDTO
	 * @return
	 */
	@PostMapping
	public AjaxResult saveOrUptateWxUser(@RequestBody WxOpenDataDTO wxOpenDataDTO){
		wxOpenDataDTO.setAppId(ThirdSessionHolder.getThirdSession().getAppId());
		wxOpenDataDTO.setUserId(ThirdSessionHolder.getThirdSession().getWxUserId());
		wxOpenDataDTO.setSessionKey(ThirdSessionHolder.getThirdSession().getSessionKey());
		WxUserEntity wxUser = wxUserService.saveOrUptateWxUser(wxOpenDataDTO);
		return AjaxResult.success(wxUser);
	}
}
