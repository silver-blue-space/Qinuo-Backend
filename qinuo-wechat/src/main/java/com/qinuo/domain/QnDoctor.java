package com.qinuo.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.qinuo.common.annotation.Excel;
import com.qinuo.common.core.domain.BaseEntity;

/**
 * 医生管理对象 qn_doctor
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@Data
@Accessors(chain = true)
public class QnDoctor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 系统用户id */
    @Excel(name = "系统用户id")
    private Long sysUserId;

    /** 职称 */
    @Excel(name = "职称")
    private String title;

    /** 医生介绍 */
    @Excel(name = "医生介绍")
    private String introduce;

    /** 擅长信息 */
    @Excel(name = "擅长信息")
    private String expertise;

    /** 首页展示0不展示 1展示 */
    @Excel(name = "首页展示0不展示 1展示")
    private String showFlg;

    /** 首页展示顺序 */
    @Excel(name = "首页展示顺序")
    private String showOrder;

    /** 停启用状态，1启用 2停用 */
    @Excel(name = "停启用状态，1启用 2停用")
    private String enableState;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sysUserId", getSysUserId())
            .append("title", getTitle())
            .append("introduce", getIntroduce())
            .append("expertise", getExpertise())
            .append("showFlg", getShowFlg())
            .append("showOrder", getShowOrder())
            .append("enableState", getEnableState())
            .toString();
    }
}
