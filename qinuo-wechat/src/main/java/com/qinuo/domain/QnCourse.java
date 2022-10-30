package com.qinuo.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qinuo.common.annotation.Excel;
import com.qinuo.common.core.domain.BaseEntity;

/**
 * 门诊科目对象 qn_course
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@Data
@Accessors(chain = true)
public class QnCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 门诊名称 */
    @Excel(name = "门诊名称")
    private String name;

    /** 停启状态 */
    @Excel(name = "停启状态")
    private String enableState;

    /** 门诊时长，单位分钟 */
    @Excel(name = "门诊时长，单位分钟")
    private Integer duration;

    /** 背景颜色 */
    @Excel(name = "背景颜色")
    private String backgroundColor;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("enableState", getEnableState())
            .append("duration", getDuration())
            .append("backgroundColor", getBackgroundColor())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .toString();
    }
}
