package com.qinuo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 下单参数
 *
 * @author JL
 * @date 2019-08-13 10:18:34
 */
@Data
@ApiModel(description = "下单参数",value = "PlaceOrderDTO")
public class PlaceOrderDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 买家留言
	 */
	@ApiModelProperty(value = "买家留言")
	private String userMessage;

	/**
	 * 门诊ID
	 */
	@ApiModelProperty(value = "门诊ID",required = true)
	private Long  schedulingId;
}
