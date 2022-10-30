package com.qinuo.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.qinuo.domain.QnCourse;
import com.qinuo.entity.QnCourseEntity;

import java.util.List;

/**
 * QnCourseDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface QnCourseDao extends IBaseDao<QnCourseEntity> {

    /**
     * 查询总件数
     * @param qnCourse
     * @return
     */
    int countQnDoctor(QnCourse qnCourse);

    /**
     * 分页查询门诊总件数
     * @param qnCourse
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<QnCourseEntity> selectQnCourseList(QnCourse qnCourse, Integer pageNum, Integer pageSize);
}
