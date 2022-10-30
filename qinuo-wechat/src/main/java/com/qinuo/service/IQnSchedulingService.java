package com.qinuo.service;

import java.util.List;

import com.qinuo.common.core.page.TableDataInfo;
import com.qinuo.domain.QnScheduling;
import org.springframework.stereotype.Service;

/**
 * 排班管理Service接口
 * 
 * @author qinuo
 * @date 2022-10-29
 */
public interface IQnSchedulingService
{
    /**
     * 查询排班管理
     * 
     * @param id 排班管理主键
     * @return 排班管理
     */
    public QnScheduling selectQnSchedulingById(Long id);

    /**
     * 分页查询排班管理列表
     * 
     * @param qnScheduling 排班管理
     * @param pageNum 当前记录起始页
     * @param pageSize 每页件数
     * @return 排班管理集合
     */
    TableDataInfo selectQnSchedulingList(QnScheduling qnScheduling, Integer pageNum, Integer pageSize);

    /**
     * 查询排班管理列表
     *
     * @param qnScheduling 排班管理
     * @return 排班管理集合
     */
    List<QnScheduling> selectQnSchedulingList(QnScheduling qnScheduling);

    /**
     * 新增排班管理
     * 
     * @param qnScheduling 排班管理
     * @return 结果
     */
    public int insertQnScheduling(QnScheduling qnScheduling);

    /**
     * 修改排班管理
     * 
     * @param qnScheduling 排班管理
     * @return 结果
     */
    public int updateQnScheduling(QnScheduling qnScheduling);

    /**
     * 批量删除排班管理
     * 
     * @param ids 需要删除的排班管理主键集合
     * @return 结果
     */
    public int deleteQnSchedulingByIds(Long[] ids);

    /**
     * 删除排班管理信息
     * 
     * @param id 排班管理主键
     * @return 结果
     */
    public int deleteQnSchedulingById(Long id);
}
