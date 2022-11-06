package com.qinuo.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.GmtCreate;
import cn.org.atool.fluent.mybatis.annotation.GmtModified;
import cn.org.atool.fluent.mybatis.annotation.LogicDelete;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * QnOrderEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@AllArgsConstructor
@NoArgsConstructor
@FluentMybatis(
    table = "qn_order",
    schema = "qinuo"
)
public class QnOrderEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId(
      value = "id",
      desc = "id"
  )
  private Long id;

  @TableField(
      value = "closing_time",
      desc = "成交时间"
  )
  private Date closingTime;

  @TableField(
      value = "create_by",
      desc = "创建者"
  )
  private String createBy;

  @TableField(
      value = "delivery_time",
      desc = "发货时间"
  )
  private Date deliveryTime;

  @TableField(
      value = "freight_price",
      desc = "运费金额"
  )
  private BigDecimal freightPrice;

  @TableField(
      value = "is_pay",
      desc = "是否支付0、未支付 1、已支付"
  )
  private String isPay;

  @TableField(
      value = "order_no",
      desc = "订单单号"
  )
  private String orderNo;

  @TableField(
      value = "payment_price",
      desc = "支付金额（销售金额+运费金额）"
  )
  private BigDecimal paymentPrice;

  @TableField(
      value = "payment_time",
      desc = "付款时间"
  )
  private Date paymentTime;

  @TableField(
      value = "payment_way",
      desc = "支付方式1、货到付款；2、在线支付"
  )
  private String paymentWay;

  @TableField(
      value = "receiver_time",
      desc = "收货时间"
  )
  private Date receiverTime;

  @TableField(
      value = "remark",
      desc = "备注"
  )
  private String remark;

  @TableField(
      value = "sales_price",
      desc = "销售金额"
  )
  private BigDecimal salesPrice;

  @TableField(
      value = "scheduling_id",
      desc = "订单名"
  )
  private Long schedulingId;

  @TableField(
      value = "status",
      desc = "订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭"
  )
  private String status;

  @TableField(
      value = "transaction_id",
      desc = "支付交易ID"
  )
  private String transactionId;

  @TableField(
      value = "update_by",
      desc = "更新者"
  )
  private String updateBy;

  @TableField(
      value = "user_message",
      desc = "买家留言"
  )
  private String userMessage;

  @TableField(
      value = "wx_user_id",
      desc = "微信id"
  )
  private String wxUserId;

  @TableField(
      value = "create_time",
      insert = "now()",
      desc = "创建时间"
  )
  @GmtCreate
  private Date createTime;

  @TableField(
      value = "update_time",
      insert = "now()",
      update = "now()",
      desc = "更新时间"
  )
  @GmtModified
  private Date updateTime;

  @TableField(
      value = "is_deleted",
      insert = "0",
      desc = "删除字段"
  )
  @LogicDelete
  private Boolean isDeleted;

  @Override
  public final Class entityClass() {
    return QnOrderEntity.class;
  }
}
