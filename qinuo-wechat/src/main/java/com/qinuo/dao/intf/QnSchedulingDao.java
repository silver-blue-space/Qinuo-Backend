package com.qinuo.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnSchedulingEntity;

import java.util.List;

/**
 * QnSchedulingDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface QnSchedulingDao extends IBaseDao<QnSchedulingEntity> {

    /**
     *  分页查询排班管理列表
     * @param qnScheduling
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<QnSchedulingEntity> selectQnSchedulingList(QnScheduling qnScheduling, Integer pageNum, Integer pageSize);

    /**
     * 查询排班管理件数
     * @param param
     * @return
     */
    int countQnSchedulingList(QnScheduling param);


    /**
     *  查询全部排班管理列表
     * @param qnScheduling
     * @return
     */
    List<QnSchedulingEntity> selectQnSchedulingList(QnScheduling qnScheduling);
}
