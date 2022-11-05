package com.qinuo.dao.impl;

import cn.org.atool.fluent.mybatis.If;
import com.qinuo.dao.base.QnSchedulingBaseDao;
import com.qinuo.dao.intf.QnSchedulingDao;
import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnSchedulingEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * QnSchedulingDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class QnSchedulingDaoImpl extends QnSchedulingBaseDao implements QnSchedulingDao {

    /**
     *  分页查询排班管理列表
     * @param param
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<QnSchedulingEntity> selectQnSchedulingList(QnScheduling param, Integer pageNum, Integer pageSize) {
        return this.query()
                .where.isDeleted().isFalse()
                .and.userId().eq(param.getUserId(), If::notNull)
                .and.courseId().eq(param.getCourseId(), If::notNull)
                .and.schedulDate().eq(param.getSchedulDate(), If::notNull)
                .and.clinicType().eq(param.getClinicType(),If::notBlank)
                .and.attendTime().eq(param.getAttendTime(),If::notNull)
                .and.finishTime().eq(param.getFinishTime(),If::notNull)
                .and.status().eq(param.getStatus(),If::notBlank)
                .and.ticketCount().eq(param.getTicketCount(),If::notNull)
                .end()
                .limit(pageNum,pageSize)
                .execute(this::listEntity);
    }

    /**
     * 查询总件数
     * @param param
     * @return
     */
    @Override
    public int countQnSchedulingList(QnScheduling param) {
        return this.query()
                .where.isDeleted().isFalse()
                .and.userId().eq(param.getUserId(), If::notNull)
                .and.courseId().eq(param.getCourseId(), If::notNull)
                .and.schedulDate().eq(param.getSchedulDate(), If::notNull)
                .and.clinicType().eq(param.getClinicType(),If::notBlank)
                .and.attendTime().eq(param.getAttendTime(),If::notNull)
                .and.finishTime().eq(param.getFinishTime(),If::notNull)
                .and.status().eq(param.getStatus(),If::notBlank)
                .and.ticketCount().eq(param.getTicketCount(),If::notNull)
                .end()
                .execute(this::count);
    }

    /**
     * 查询全部排班管理列表
     * @param param
     * @return
     */
    @Override
    public List<QnSchedulingEntity> selectQnSchedulingList(QnScheduling param) {
        return this.query()
                .where.isDeleted().isFalse()
                .and.id().eq(param.getId(), If::notNull)
                .and.userId().eq(param.getUserId(), If::notNull)
                .and.courseId().eq(param.getCourseId(), If::notNull)
                .and.schedulDate().eq(param.getSchedulDate(), If::notNull)
                .and.clinicType().eq(param.getClinicType(),If::notBlank)
                .and.attendTime().eq(param.getAttendTime(),If::notNull)
                .and.finishTime().eq(param.getFinishTime(),If::notNull)
                .and.status().eq(param.getStatus(),If::notBlank)
                .and.ticketCount().eq(param.getTicketCount(),If::notNull)
                .end()
                .execute(this::listEntity);
    }

    @Override
    public List<QnSchedulingEntity> selectCourseSchedulingList(Long userId, List<LocalDate> dateList) {
        return this.query()
                .where.isDeleted().isFalse()
                .and.userId().eq(userId)
                .and.schedulDate().in(dateList)
                .end()
                .execute(this::listEntity);
    }
}
