package com.qinuo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.atool.fluent.mybatis.If;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.SecurityUtils;
import com.qinuo.constant.CommonConstants;
import com.qinuo.constant.MallConstants;
import com.qinuo.coverter.QnOrderServiceConverter;
import com.qinuo.coverter.QnSchedulingConverter;
import com.qinuo.dao.intf.QnSchedulingDao;
import com.qinuo.domain.QnScheduling;
import com.qinuo.entity.QnOrderEntity;
import com.qinuo.entity.QnSchedulingEntity;
import com.qinuo.enums.OrderInfoEnum;
import com.qinuo.mapper.QnOrderMapper;
import com.qinuo.service.IQnSchedulingService;
import com.qinuo.wrapper.QnOrderQuery;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.qinuo.domain.QnOrder;
import com.qinuo.service.IQnOrderService;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IQnSchedulingService iQnSchedulingService;

    @Autowired
    private QnSchedulingDao qnSchedulingDao;


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
        QnOrderQuery query = this.qnOrderMapper
                .query()
                .where.wxUserId().eq(qnOrder.getWxUserId(), If::notBlank)
                .and.schedulingId().eq(qnOrder.getSchedulingId(),If::notNull)
                .and.orderNo().eq(qnOrder.getOrderNo(),If::notBlank)
                .and.paymentWay().eq(qnOrder.getPaymentWay(),If::notBlank)
                .and.isPay().eq(qnOrder.getIsPay(),If::notBlank)
                .and.status().eq(qnOrder.getStatus(), If::notBlank)
                .and.salesPrice().eq(qnOrder.getSalesPrice(),If::notNull)
                .and.transactionId().eq(qnOrder.getTransactionId(), If::notBlank)
                .and.isDeleted().isFalse()
                .end();
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
     * 检查订单过期时间
     * @param orderInfo
     */
    @Override
    public void checkOutTime(QnOrder orderInfo) {
        String keyRedis = null;
        //获取自动取消倒计时
        if(CommonConstants.NO.equals(orderInfo.getIsPay())){
            keyRedis = String.valueOf(StrUtil.format("{}:{}", MallConstants.REDIS_ORDER_KEY_IS_PAY_0,orderInfo.getId()));
        }
        //获取自动收货倒计时
        if(OrderInfoEnum.STATUS_2.getValue().equals(orderInfo.getStatus())){
            keyRedis = String.valueOf(StrUtil.format("{}:{}",MallConstants.REDIS_ORDER_KEY_STATUS_2,orderInfo.getId()));
        }
        if(keyRedis != null){
            Long outTime = redisTemplate.getExpire(keyRedis);
            if(outTime != null && outTime > 0){
                orderInfo.setOutTime(outTime);
            }
        }
    }

    /**
     * 小程序创建订单
     * @param orderInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QnOrder createOrder(QnOrder orderInfo) {
        Date createTime = DateUtils.getNowDate();
        orderInfo.setIsPay(CommonConstants.NO);
        orderInfo.setOrderNo(IdUtil.getSnowflake(0,0).nextIdStr());
        orderInfo.setSalesPrice(BigDecimal.ZERO);
        orderInfo.setPaymentPrice(BigDecimal.ZERO);
        orderInfo.setFreightPrice(BigDecimal.ZERO);
        orderInfo.setCreateTime(createTime);

        /**
         * 查询预约的门诊信息
         */
        QnScheduling  goodsSpu  = iQnSchedulingService.selectQnSchedulingById(orderInfo.getSchedulingId());
        //门诊被删除 || 门诊约满、停诊(sys_scheduling_status  可约 0) || 超过最大预约数
        if(Objects.isNull(goodsSpu) ||  goodsSpu.getIsDeleted() || !"0".equals(goodsSpu.getStatus()) || goodsSpu.getTicketCount() <= 0){
            return null;
        }

        /**
         * 订单创建
         */
        QnOrderEntity orderEntity = QnOrderServiceConverter.INSTANCE.toQnOrderEntity(orderInfo);
        //门诊科目
        orderEntity.setCourseId(goodsSpu.getCourseId());
        //门诊时间
        orderEntity.setSchedulDate(DateUtils.parseDate(goodsSpu.getSchedulDate()));
        //门诊ID
        orderEntity.setSchedulingId(orderInfo.getSchedulingId());
        Long id = qnOrderMapper.save(orderEntity);

        /**
         * 更新库存
         */
        goodsSpu.setTicketCount(goodsSpu.getTicketCount() - 1);
        goodsSpu.setUpdateTime(createTime);
        QnSchedulingEntity updateEntity =  QnSchedulingConverter.INSTANCE.toQnSchedulingEntity(goodsSpu);
        qnSchedulingDao.updateById(updateEntity);

        /**
         * 设置订单超时未支付自动取消
         */
        long orderTimeOut = MallConstants.ORDER_TIME_OUT_0;
        //加入redis，30分钟自动取消
        String keyRedis = String.valueOf(StrUtil.format("{}:{}",MallConstants.REDIS_ORDER_KEY_IS_PAY_0, orderInfo.getId()));
        redisTemplate.opsForValue().set(keyRedis, orderInfo.getOrderNo() , orderTimeOut, TimeUnit.MINUTES);//设置过期时间

        return orderInfo.setId(id);
    }

    /**
     * 小程序删除订单
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int logicDeleteById(Long id){
        return qnOrderMapper.logicDeleteById(id);
    }

    /**
     * 取消订单
     * @param orderInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orderCancel(QnOrder orderInfo) {
        Date createTime = DateUtils.getNowDate();
        if(CommonConstants.NO.equals(orderInfo.getIsPay()) && !OrderInfoEnum.STATUS_5.getValue().equals(orderInfo.getStatus())){//校验
            orderInfo.setStatus(OrderInfoEnum.STATUS_5.getValue());
            //回滚库存
            QnScheduling  goodsSpu  = iQnSchedulingService.selectQnSchedulingById(orderInfo.getSchedulingId());
            /**
             * 更新回滚库存
             */
            goodsSpu.setTicketCount(goodsSpu.getTicketCount() + 1);
            goodsSpu.setUpdateTime(createTime);
            QnSchedulingEntity updateEntity =  QnSchedulingConverter.INSTANCE.toQnSchedulingEntity(goodsSpu);
            qnSchedulingDao.updateById(updateEntity);

            /**
             * 更新订单状态
             */
            QnOrderEntity entity = QnOrderServiceConverter.INSTANCE.toQnOrderEntity(orderInfo);
            entity.setUpdateTime(DateUtils.getNowDate());
            int cnt = qnOrderMapper.updateById(entity);
            if(cnt > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 门诊完成
     * @param orderInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderReceive(QnOrder orderInfo) {
        orderInfo.setStatus(OrderInfoEnum.STATUS_3.getValue());
        orderInfo.setReceiverTime(DateUtils.getNowDate());
        QnOrderEntity entity = QnOrderServiceConverter.INSTANCE.toQnOrderEntity(orderInfo);
        entity.setUpdateTime(DateUtils.getNowDate());
        int cnt = qnOrderMapper.updateById(entity);
        if(cnt > 0){
            return true;
        }
        return false;
    }

    /**
     * 订单支付处理
     * @param orderInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyOrder(QnOrder orderInfo) {
        if(CommonConstants.NO.equals(orderInfo.getIsPay())){//只有未支付订单能操作
            orderInfo.setIsPay(CommonConstants.YES);
            orderInfo.setStatus(OrderInfoEnum.STATUS_1.getValue());
            QnScheduling  goodsSpu  = iQnSchedulingService.selectQnSchedulingById(orderInfo.getSchedulingId());

            /**
             * 门诊排班表  更新销量
             */
            goodsSpu.setSaleNum(goodsSpu.getSaleNum()+ 1);
            QnSchedulingEntity updateEntity =  QnSchedulingConverter.INSTANCE.toQnSchedulingEntity(goodsSpu);
            qnSchedulingDao.updateById(updateEntity);
            /**
             * 订单表更新支付状态
             */
            QnOrderEntity entity = QnOrderServiceConverter.INSTANCE.toQnOrderEntity(orderInfo);
            qnOrderMapper.updateById(entity);//更新订单
        }
    }

    /**
     * 获取当前登录用户名
     * @return
     */
    private String getUsername() {
        return SecurityUtils.getLoginUser().getUsername();
    }

}
