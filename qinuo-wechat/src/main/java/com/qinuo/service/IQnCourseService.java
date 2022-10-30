package com.qinuo.service;

import java.util.List;
import com.qinuo.domain.QnCourse;

/**
 * 门诊科目Service接口
 * 
 * @author qinuo
 * @date 2022-10-30
 */
public interface IQnCourseService 
{
    /**
     * 查询门诊科目
     * 
     * @param id 门诊科目主键
     * @return 门诊科目
     */
    public QnCourse selectQnCourseById(Long id);

    /**
     * 分页查询门诊科目列表
     * 
     * @param qnCourse 门诊科目
     * @return 门诊科目集合
     */
    public List<QnCourse> selectQnCourseList(QnCourse qnCourse);

    /**
     * 新增门诊科目
     * 
     * @param qnCourse 门诊科目
     * @return 结果
     */
    public int insertQnCourse(QnCourse qnCourse);

    /**
     * 修改门诊科目
     * 
     * @param qnCourse 门诊科目
     * @return 结果
     */
    public int updateQnCourse(QnCourse qnCourse);

    /**
     * 批量删除门诊科目
     * 
     * @param ids 需要删除的门诊科目主键集合
     * @return 结果
     */
    public int deleteQnCourseByIds(Long[] ids);

    /**
     * 删除门诊科目信息
     * 
     * @param id 门诊科目主键
     * @return 结果
     */
    public int deleteQnCourseById(Long id);

    /**
     * 查询总件数
     * @param qnCourse
     * @return
     */
    int countQnDoctor(QnCourse qnCourse);
}
