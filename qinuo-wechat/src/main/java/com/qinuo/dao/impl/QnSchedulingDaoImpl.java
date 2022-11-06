package com.qinuo.dao.impl;

import cn.org.atool.fluent.mybatis.If;
import cn.org.atool.fluent.mybatis.base.crud.BaseQuery;
import cn.org.atool.fluent.mybatis.base.crud.IQuery;
import cn.org.atool.fluent.mybatis.base.model.SqlOp;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.StringUtils;
import com.qinuo.dao.base.QnSchedulingBaseDao;
import com.qinuo.dao.intf.QnSchedulingDao;
import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnSchedulingEntity;
import com.qinuo.wrapper.QnSchedulingQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;

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
        return mapper.listEntity(getQnSchedulingQuery(param).limit(pageNum,pageSize));
    }

    /**
     * 查询总件数
     * @param param
     * @return
     */
    @Override
    public int countQnSchedulingList(QnScheduling param) {
        return mapper.count(getQnSchedulingQuery(param));
    }

    /**
     * 查询全部排班管理列表
     * @param param
     * @return
     */
    @Override
    public List<QnSchedulingEntity> selectQnSchedulingList(QnScheduling param) {
        return mapper.listEntity(getQnSchedulingQuery(param));
    }

    /**
     * QnScheduling 构造查询Query
     * 设置 开始时间~结束时间Where 条件
     * @param param 查询条件
     */
    private QnSchedulingQuery getQnSchedulingQuery(QnScheduling param) {
        QnSchedulingQuery schedulingQuery = this.query()
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
                .end();
        if(Objects.nonNull(param.getParams()) &&
                param.getParams().containsKey("beginTime") &&
                param.getParams().containsKey("endTime") &&
                Objects.nonNull(param.getParams().get("beginTime")) &&
                Objects.nonNull(param.getParams().get("endTime"))){
            schedulingQuery.where().apply("schedul_date", SqlOp.BETWEEN, param.getParams().get("beginTime"),param.getParams().get("endTime"));
        }
        return schedulingQuery;
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
