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
 * QnCourseEntity: 数据映射实体定义
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
    table = "qn_course",
    schema = "qinuo"
)
public class QnCourseEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId(
      value = "id",
      desc = "id"
  )
  private Long id;

  @TableField(
      value = "background_color",
      desc = "背景颜色"
  )
  private String backgroundColor;

  @TableField(
      value = "duration",
      desc = "门诊时长，单位分钟"
  )
  private Integer duration;

  @TableField(
      value = "enable_state",
      desc = "停启用状态，1启用 2停用"
  )
  private Integer enableState;

  @TableField(
      value = "name",
      desc = "门诊名称"
  )
  private String name;

  @Override
  public final Class entityClass() {
    return QnCourseEntity.class;
  }
}
