package com.qinuo.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课批量新增参数 qn_course
 *
 * @author qinuo
 * @date 2022-11-02
 */
@Data
@Accessors(chain = true)
public class QnSchedulingBatchSave {

    /**
     * 门诊科目id
     */
    @NotNull
    private Long courseId;

    /**
     * 可预约患者数
     */
    @NotNull
    private Integer ticketCount;

    /**
     * 用户ID(医生)
     */
    @NotNull
    private Long userId;

    /**
     * 门诊开始时间
     */
    @NotNull
    private LocalTime attendTime;

    /**
     * 门诊结束时间
     */
    @NotNull
    private LocalTime finishTime;

    /**
     * 排班开始日期
     */
    @NotNull
    private LocalDate startDate;

    /**
     * 排班结束日期
     */
    @NotNull
    private LocalDate endDate;

    /**
     * 周几上班列表
     */
    @NotEmpty
    private List<Integer> weekList;
}
