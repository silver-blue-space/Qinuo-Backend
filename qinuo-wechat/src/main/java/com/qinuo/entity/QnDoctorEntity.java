package com.qinuo.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * QnDoctorEntity: 数据映射实体定义
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
    table = "qn_doctor",
    schema = "qinuo"
)
public class QnDoctorEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId(
      value = "id",
      desc = "id"
  )
  private Long id;

  @TableField(
      value = "enable_state",
      desc = "停启用状态，1启用 2停用"
  )
  private String enableState;

  @TableField(
      value = "expertise",
      desc = "擅长信息"
  )
  private String expertise;

  @TableField(
      value = "introduce",
      desc = "医生介绍"
  )
  private String introduce;

  @TableField(
      value = "show_flg",
      desc = "首页展示0不展示 1展示"
  )
  private String showFlg;

  @TableField(
      value = "show_order",
      desc = "首页展示顺序"
  )
  private String showOrder;

  @TableField(
      value = "sys_user_id",
      desc = "系统用户id"
  )
  private Long sysUserId;

  @TableField(
      value = "title",
      desc = "职称"
  )
  private String title;

  @Override
  public final Class entityClass() {
    return QnDoctorEntity.class;
  }
}
