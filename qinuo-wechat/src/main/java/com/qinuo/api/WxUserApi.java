/**
 * Copyright (C) 2018-2019
 * All rights reserved, Designed By www.joolun.com
 */
package com.qinuo.api;

import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.domain.LoginMaDTO;
import com.qinuo.domain.WxOpenDataDTO;
import com.qinuo.entity.WxUserEntity;
import com.qinuo.service.WxUserService;
import com.qinuo.utils.ThirdSessionHolder;
import com.qinuo.utils.WxMaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *  小程序用户
 *  基于 封装SDK https://gitee.com/binary/weixin-java-tools.git
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/weixin/api/ma/wxuser")
@Api(value = "WxUserApi", tags = "小程序用户管理API")
public class WxUserApi {

	@Autowired
	private final WxUserService wxUserService;
	/**
	 * 小程序用户登录
	 * @param request
	 * @param loginMaDTO
	 * @return
	 */
	@ApiOperation("小程序用户登录")
	@PostMapping("/login")
	@ApiImplicitParam(name = "loginMaDTO", required = true, dataTypeClass = LoginMaDTO.class,dataType = "LoginMaDTO" )
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
	@ApiOperation("获取用户信息")
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
	@ApiOperation("保存用户信息")
	@PostMapping
	@ApiImplicitParam(name = "wxOpenDataDTO", required = true, dataTypeClass = WxOpenDataDTO.class,dataType = "WxOpenDataDTO" )
	public AjaxResult saveOrUptateWxUser(@RequestBody WxOpenDataDTO wxOpenDataDTO){
		wxOpenDataDTO.setAppId(ThirdSessionHolder.getThirdSession().getAppId());
		wxOpenDataDTO.setUserId(ThirdSessionHolder.getThirdSession().getWxUserId());
		wxOpenDataDTO.setSessionKey(ThirdSessionHolder.getThirdSession().getSessionKey());
		WxUserEntity wxUser = wxUserService.saveOrUptateWxUser(wxOpenDataDTO);
		return AjaxResult.success(wxUser);
	}
}
