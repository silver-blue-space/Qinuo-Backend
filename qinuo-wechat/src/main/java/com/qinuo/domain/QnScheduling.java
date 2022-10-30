package com.qinuo.domain;

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
 * @date 2022-10-29
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
    private Long accountId;

    /** 医院科室ID */
    @Excel(name = "医院科室ID")
    private Long deptId;

    /** 值班日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "值班日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date schedulDate;

    /** 上午/下午/夜晚 */
    @Excel(name = "上午/下午/夜晚")
    private String ampm;

    /** 时段 */
    @Excel(name = "时段")
    private String period;

    /** 门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊 */
    @Excel(name = "门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊")
    private String clinicType;

    /** 状态， */
    @Excel(name = "状态，")
    private String status;

    /** 可预约患者数 */
    @Excel(name = "可预约患者数")
    private Long ticketCount;

    /** 删除字段 */
    private Boolean isDeleted;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("accountId", getAccountId())
            .append("deptId", getDeptId())
            .append("schedulDate", getSchedulDate())
            .append("ampm", getAmpm())
            .append("period", getPeriod())
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
