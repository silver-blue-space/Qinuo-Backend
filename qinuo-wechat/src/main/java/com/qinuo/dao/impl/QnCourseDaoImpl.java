package com.qinuo.dao.impl;

import cn.org.atool.fluent.mybatis.If;
import com.qinuo.dao.base.QnCourseBaseDao;
import com.qinuo.dao.intf.QnCourseDao;
import com.qinuo.domain.QnCourse;
import com.qinuo.entity.QnCourseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QnCourseDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class QnCourseDaoImpl extends QnCourseBaseDao implements QnCourseDao {
    @Override
    public int countQnDoctor(QnCourse param) {
        return this.query()
                .where
                .enableState().eq(param.getEnableState(), If::notBlank)
                .and.name().like(param.getName(),If::notBlank)
                .end()
                .execute(this::count);
    }

    @Override
    public List<QnCourseEntity> selectQnCourseList(QnCourse param, Integer pageNum, Integer pageSize) {
        return this.query()
                .where
                .enableState().eq(param.getEnableState(), If::notBlank)
                .and.name().like(param.getName(),If::notBlank)
                .end()
                .limit(pageNum,pageSize)
                .execute(this::listEntity);
    }
}
