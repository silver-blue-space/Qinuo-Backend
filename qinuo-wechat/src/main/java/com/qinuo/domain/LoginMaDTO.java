package com.qinuo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "LoginMaDTO",description = "微信临时登录凭证")
public class LoginMaDTO {
	/**
	 * 微信临时登录凭证Code
	 */
	@ApiModelProperty("微信临时登录凭证Code")
	private String jsCode;
}
