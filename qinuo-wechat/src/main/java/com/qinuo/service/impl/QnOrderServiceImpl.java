package com.qinuo.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.SecurityUtils;
import com.qinuo.coverter.QnOrderServiceConverter;
import com.qinuo.entity.QnOrderEntity;
import com.qinuo.mapper.QnOrderMapper;
import com.qinuo.wrapper.QnOrderQuery;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinuo.domain.QnOrder;
import com.qinuo.service.IQnOrderService;
import org.springframework.util.CollectionUtils;

/**
 * 订单管理Service业务层处理
 * 
 * @author qinuo
 * @date 2022-11-06
 */
@Service
public class QnOrderServiceImpl implements IQnOrderService 
{
    @Autowired
    private QnOrderMapper qnOrderMapper;

    /**
     * 查询订单管理
     * 
     * @param id 订单管理主键
     * @return 订单管理
     */
    @Override
    public QnOrder selectQnOrderById(Long id)
    {
        QnOrderEntity entity =  qnOrderMapper.findById(id);
        return QnOrderServiceConverter.INSTANCE.toQnOrder(entity);
    }

    /**
     * 查询订单管理列表
     * 
     * @param qnOrder 订单管理
     * @return 订单管理
     */
    @Override
    public List<QnOrder> selectQnOrderList(QnOrder qnOrder)
    {

        List<QnOrderEntity> entities =  qnOrderMapper.listEntity(getQnOrderQuery(qnOrder));
        if(CollectionUtils.isEmpty(entities)){
            return Lists.newArrayList();
        }

        return entities.stream().map(QnOrderServiceConverter.INSTANCE::toQnOrder).collect(Collectors.toList());
    }

    /**
     * 查询订单管理列表总件数
     * @param qnOrder
     * @return
     */
    @Override
    public int countQnOrder(QnOrder qnOrder) {
        return qnOrderMapper.count(getQnOrderQuery(qnOrder));
    }

    /**
     * QnOrderQuery 构造查询
     * @param qnOrder
     * @return
     */
    private QnOrderQuery getQnOrderQuery(QnOrder qnOrder){
        QnOrderQuery query = this.qnOrderMapper.query();
        return query;
    }

    /**
     * 新增订单管理
     * 
     * @param qnOrder 订单管理
     * @return 结果
     */
    @Override
    public int insertQnOrder(QnOrder qnOrder)
    {
        qnOrder.setCreateTime(DateUtils.getNowDate());
        qnOrder.setCreateBy(getUsername());
        return qnOrderMapper.save(QnOrderServiceConverter.INSTANCE.toQnOrderEntity(qnOrder));
    }

    /**
     * 修改订单管理
     * 
     * @param qnOrder 订单管理
     * @return 结果
     */
    @Override
    public int updateQnOrder(QnOrder qnOrder)
    {
        qnOrder.setUpdateTime(DateUtils.getNowDate());
        qnOrder.setUpdateBy(getUsername());

        return qnOrderMapper.updateById(QnOrderServiceConverter.INSTANCE.toQnOrderEntity(qnOrder));
    }

    /**
     * 批量删除订单管理
     * 
     * @param ids 需要删除的订单管理主键
     * @return 结果
     */
    @Override
    public int deleteQnOrderByIds(Long[] ids)
    {
        return qnOrderMapper.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除订单管理信息
     * 
     * @param id 订单管理主键
     * @return 结果
     */
    @Override
    public int deleteQnOrderById(Long id)
    {
        return qnOrderMapper.deleteById(id);
    }
    /**
     * 获取当前登录用户名
     * @return
     */
    private String getUsername() {
        return SecurityUtils.getLoginUser().getUsername();
    }

}
