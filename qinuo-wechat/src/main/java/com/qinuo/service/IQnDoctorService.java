package com.qinuo.service;

import com.qinuo.common.core.page.TableDataInfo;
import com.qinuo.domain.QnDoctor;

import java.util.List;

/**
 * 医生管理Service接口
 * 
 * @author qinuo
 * @date 2022-10-30
 */
public interface IQnDoctorService 
{
    /**
     * 查询医生管理
     * 
     * @param id 医生管理主键
     * @return 医生管理
     */
    public QnDoctor selectQnDoctorById(Long id);

    /**
     * 查询医生管理列表
     * 
     * @param qnDoctor 医生管理
     * @return 医生管理集合
     */
    List<QnDoctor> selectQnDoctorList(QnDoctor qnDoctor);


    /**
     * 查询总件数
     * @param qnDoctor
     * @return
     */
    int countQnDoctor(QnDoctor qnDoctor);
    /**
     * 新增医生管理
     * 
     * @param qnDoctor 医生管理
     * @return 结果
     */
    public int insertQnDoctor(QnDoctor qnDoctor);

    /**
     * 修改医生管理
     * 
     * @param qnDoctor 医生管理
     * @return 结果
     */
    public int updateQnDoctor(QnDoctor qnDoctor);

    /**
     * 批量删除医生管理
     * 
     * @param ids 需要删除的医生管理主键集合
     * @return 结果
     */
    public int deleteQnDoctorByIds(Long[] ids);

    /**
     * 删除医生管理信息
     * 
     * @param id 医生管理主键
     * @return 结果
     */
    public int deleteQnDoctorById(Long id);
}
