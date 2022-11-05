package com.qinuo.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qinuo.common.annotation.Excel;
import com.qinuo.common.core.domain.BaseEntity;

/**
 * 排班管理对象 qn_scheduling
 * 
 * @author qinuo
 * @date 2022-11-02
 */
@Data
@Accessors(chain = true)
public class QnScheduling extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 门诊科目ID */
    @Excel(name = "门诊科目ID")
    private Long courseId;

    /** 值班日期 */
    @Excel(name = "值班日期", width = 30)
    private String schedulDate;

    /** 门诊开始时间 */
    @Excel(name = "门诊开始时间", width = 30)
    private String attendTime;

    /** 门诊结束时间 */
    @Excel(name = "门诊结束时间", width = 30)
    private String finishTime;

    /** 门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊 */
    @Excel(name = "门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊")
    private String clinicType;

    /** 状态， */
    @Excel(name = "状态，")
    private String status;

    /** 可预约患者数 */
    @Excel(name = "可预约患者数")
    private Integer ticketCount;

    /** 删除字段 */
    private Boolean isDeleted;

    /** 门诊背景色 */
    private String backgroundColor;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("courseId", getCourseId())
            .append("schedulDate", getSchedulDate())
            .append("attendTime", getAttendTime())
            .append("finishTime", getFinishTime())
            .append("clinicType", getClinicType())
            .append("status", getStatus())
            .append("ticketCount", getTicketCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
