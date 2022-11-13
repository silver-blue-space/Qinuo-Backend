package com.qinuo.listener;

import cn.hutool.core.util.StrUtil;
import com.qinuo.config.RedisConfigProperties;
import com.qinuo.constant.CommonConstants;
import com.qinuo.constant.MallConstants;
import com.qinuo.domain.QnOrder;
import com.qinuo.enums.OrderInfoEnum;
import com.qinuo.service.IQnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * redis过期监听
 * 1、自动取消订单
 * 2、自动收货
 */
@Component
public class RedisKeyExpirationListener implements MessageListener {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private RedisConfigProperties redisConfigProperties;

	@Autowired
	private IQnOrderService iQnOrderService;

	public RedisKeyExpirationListener(RedisTemplate<String, String> redisTemplate,
                                      RedisConfigProperties redisConfigProperties,
									  IQnOrderService orderInfoService){
		this.redisTemplate = redisTemplate;
		this.redisConfigProperties = redisConfigProperties;
		this.iQnOrderService = orderInfoService;
	}
	@Override
	public void onMessage(Message message, byte[] bytes) {
		RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
		String channel = String.valueOf(serializer.deserialize(message.getChannel()));
		String body = String.valueOf(serializer.deserialize(message.getBody()));
		//key过期监听
		if(StrUtil.format("__keyevent@{}__:expired", redisConfigProperties.getDatabase()).equals(channel)){
			//订单自动取消
			if(body.contains(MallConstants.REDIS_ORDER_KEY_IS_PAY_0)) {
				body = body.replace(MallConstants.REDIS_ORDER_KEY_IS_PAY_0, "");
				String[] str = body.split(":");
				String wxOrderId = str[1];
				QnOrder orderInfo =   iQnOrderService.selectQnOrderById(Long.valueOf(wxOrderId));
				if(orderInfo != null && CommonConstants.NO.equals(orderInfo.getIsPay())){//只有待支付的订单能取消
					iQnOrderService.orderCancel(orderInfo);
				}
			}
			//订单自动收货
			if(body.contains(MallConstants.REDIS_ORDER_KEY_STATUS_2)) {
				body = body.replace(MallConstants.REDIS_ORDER_KEY_STATUS_2, "");
				String[] str = body.split(":");
				String orderId = str[1];
				QnOrder orderInfo =   iQnOrderService.selectQnOrderById(Long.valueOf(orderId));
				if(orderInfo != null && OrderInfoEnum.STATUS_2.getValue().equals(orderInfo.getStatus())){//只有待收货的订单能收货
					iQnOrderService.orderReceive(orderInfo);
				}
			}
		}
	}
}
