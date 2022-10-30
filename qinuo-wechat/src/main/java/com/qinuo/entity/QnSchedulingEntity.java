package com.qinuo.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.LogicDelete;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * QnSchedulingEntity: 数据映射实体定义
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
    table = "qn_scheduling",
    schema = "qinuo"
)
public class QnSchedulingEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId(
      value = "id",
      desc = "ID"
  )
  private Long id;

  @TableField(
      value = "account_id",
      desc = "用户ID"
  )
  private Long accountId;

  @TableField(
      value = "ampm",
      desc = "上午/下午/夜晚"
  )
  private String ampm;

  @TableField(
      value = "clinic_type",
      desc = "门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊"
  )
  private String clinicType;

  @TableField(
      value = "create_by",
      desc = "创建者"
  )
  private String createBy;

  @TableField(
      value = "create_time",
      desc = "创建时间"
  )
  private Date createTime;

  @TableField(
      value = "dept_id",
      desc = "医院科室ID"
  )
  private Long deptId;

  @TableField(
      value = "period",
      desc = "时段"
  )
  private String period;

  @TableField(
      value = "remark",
      desc = "备注"
  )
  private String remark;

  @TableField(
      value = "schedul_date",
      desc = "值班日期"
  )
  private Date schedulDate;

  @TableField(
      value = "status",
      desc = "状态，"
  )
  private String status;

  @TableField(
      value = "ticket_count",
      desc = "可预约患者数"
  )
  private Integer ticketCount;

  @TableField(
      value = "update_by",
      desc = "更新者"
  )
  private String updateBy;

  @TableField(
      value = "update_time",
      desc = "更新时间"
  )
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
    return QnSchedulingEntity.class;
  }
}
