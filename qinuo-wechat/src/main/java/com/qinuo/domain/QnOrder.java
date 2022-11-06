package com.qinuo.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qinuo.common.annotation.Excel;
import com.qinuo.common.core.domain.BaseEntity;

/**
 * 订单管理对象 qn_order
 * 
 * @author qinuo
 * @date 2022-11-06
 */
@Data
@Accessors(chain = true)
public class QnOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 微信id */
    @Excel(name = "微信id")
    private String wxUserId;

    /** 门诊ID */
    @Excel(name = "门诊ID")
    private Long schedulingId;

    /** 订单单号 */
    @Excel(name = "订单单号")
    private String orderNo;

    /** 支付方式1、货到付款；2、在线支付 */
    @Excel(name = "支付方式1、货到付款；2、在线支付")
    private String paymentWay;

    /** 是否支付0、未支付 1、已支付 */
    @Excel(name = "是否支付0、未支付 1、已支付")
    private String isPay;

    /** 订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭 */
    @Excel(name = "订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭")
    private String status;

    /** 运费金额 */
    private BigDecimal freightPrice;

    /** 销售金额 */
    private BigDecimal salesPrice;

    /** 支付金额（销售金额+运费金额） */
    private BigDecimal paymentPrice;

    /** 付款时间 */
    private Date paymentTime;

    /** 发货时间 */
    private Date deliveryTime;

    /** 收货时间 */
    private Date receiverTime;

    /** 成交时间 */
    private Date closingTime;

    /** 买家留言 */
    private String userMessage;

    /** 支付交易ID */
    private String transactionId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wxUserId", getWxUserId())
            .append("schedulingId", getSchedulingId())
            .append("orderNo", getOrderNo())
            .append("paymentWay", getPaymentWay())
            .append("isPay", getIsPay())
            .append("status", getStatus())
            .append("freightPrice", getFreightPrice())
            .append("salesPrice", getSalesPrice())
            .append("paymentPrice", getPaymentPrice())
            .append("paymentTime", getPaymentTime())
            .append("deliveryTime", getDeliveryTime())
            .append("receiverTime", getReceiverTime())
            .append("closingTime", getClosingTime())
            .append("userMessage", getUserMessage())
            .append("transactionId", getTransactionId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
