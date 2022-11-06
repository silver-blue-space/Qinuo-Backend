package com.qinuo.service;

import java.util.List;
import com.qinuo.domain.QnOrder;

/**
 * 订单管理Service接口
 * 
 * @author qinuo
 * @date 2022-11-06
 */
public interface IQnOrderService 
{
    /**
     * 查询订单管理
     * 
     * @param id 订单管理主键
     * @return 订单管理
     */
    public QnOrder selectQnOrderById(Long id);

    /**
     * 查询订单管理列表
     * 
     * @param qnOrder 订单管理
     * @return 订单管理集合
     */
    public List<QnOrder> selectQnOrderList(QnOrder qnOrder);

    /**
     * 查询订单总件数
     * @param qnOrder
     * @return
     */
    int countQnOrder(QnOrder qnOrder);

    /**
     * 新增订单管理
     * 
     * @param qnOrder 订单管理
     * @return 结果
     */
    public int insertQnOrder(QnOrder qnOrder);

    /**
     * 修改订单管理
     * 
     * @param qnOrder 订单管理
     * @return 结果
     */
    public int updateQnOrder(QnOrder qnOrder);

    /**
     * 批量删除订单管理
     * 
     * @param ids 需要删除的订单管理主键集合
     * @return 结果
     */
    public int deleteQnOrderByIds(Long[] ids);

    /**
     * 删除订单管理信息
     * 
     * @param id 订单管理主键
     * @return 结果
     */
    public int deleteQnOrderById(Long id);
}
