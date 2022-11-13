package com.qinuo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 保存微信用户信息
 * http://binary.ac.cn/weixin-java-miniapp-javadoc/cn/binarywang/wx/miniapp/api/impl/WxMaUserServiceImpl.html
 */
@Data
@ApiModel(value = "WxOpenDataDTO",description = "保存微信用户信息")
public class WxOpenDataDTO {

	/**
	 * 小程序AppId(无需传参)
	 */
	@ApiModelProperty(value = "小程序AppId",hidden = true)
	private String appId;
	/**
	 * 奇诺用户ID(无需传参)
	 */
	@ApiModelProperty(value = "用户ID",hidden = true)
	private String userId;
	/**
	 * 会话密钥(无需传参)
	 */
	@ApiModelProperty(value = "会话密钥",hidden = true)
	private String sessionKey;

	/**
	 * 消息密文
	 */
	@ApiModelProperty(value = "消息密文")
	private String encryptedData;
	/**
	 * 加密算法的初始向量
	 */
	@ApiModelProperty(value = "加密算法的初始向量")
	private String iv;

	@ApiModelProperty(value = "校验用户信息字符串",hidden = true)
	private String rawData;
	@ApiModelProperty(value = "原始数据字符串",hidden = true)
	private String signature;
	@ApiModelProperty(value = "错误信息",hidden = true)
	private String errMsg;

}
