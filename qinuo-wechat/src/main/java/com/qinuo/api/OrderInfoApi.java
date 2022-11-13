package com.qinuo.api;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageHelper;
import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.common.annotation.Anonymous;
import com.qinuo.common.core.page.PageDomain;
import com.qinuo.common.core.page.TableSupport;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.StringUtils;
import com.qinuo.common.utils.sql.SqlUtil;
import com.qinuo.config.WxMaProperties;
import com.qinuo.config.WxPayConfiguration;
import com.qinuo.constant.CommonConstants;
import com.qinuo.constant.MallConstants;
import com.qinuo.constant.MyReturnCode;
import com.qinuo.domain.PlaceOrderDTO;
import com.qinuo.domain.QnCourse;
import com.qinuo.domain.QnOrder;
import com.qinuo.entity.WxUserEntity;
import com.qinuo.enums.OrderInfoEnum;
import com.qinuo.service.IQnCourseService;
import com.qinuo.service.IQnOrderService;
import com.qinuo.utils.LocalDateTimeUtils;
import com.qinuo.utils.ThirdSessionHolder;
import com.qinuo.utils.WxMaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 小程序订单
 *  {@link com.github.binarywang.wxpay}
 */
@Slf4j
@Anonymous
@RestController
@AllArgsConstructor
@RequestMapping("/weixin/api/ma/orderinfo")
@Api(value = "OrderInfoApi", tags = "小程序订单API")
public class OrderInfoApi {

	@Autowired
	private IQnOrderService iQnOrderService;
	@Autowired
	private IQnCourseService qnCourseService;
	@Autowired
	private WxMaProperties wxMaProperties;


	/**
	* 分页查询
	*  @param pageNum 页号
	 * @param pageSize 件数
	* @param orderInfo 门诊订单
	* @return
	*/
	@ApiOperation(value = "分页查询")
    @GetMapping("/page")
	@ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页号", required = false, dataType = "Integer", paramType = "query", dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "pageSize", value = "件数", required = false, dataType = "Integer", paramType = "query", dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "qnOrder", required = true, dataTypeClass = QnOrder.class,dataType = "QnOrder" )})
	public AjaxResult getOrderInfoPage(Integer pageNum, Integer pageSize, QnOrder orderInfo) {

		orderInfo.setWxUserId(ThirdSessionHolder.getWxUserId());
		int total =  iQnOrderService.countQnOrder(orderInfo);
		if(total <= 0){
			return  AjaxResult.success(Lists.newArrayList());
		}
		if(pageNum == null || pageNum <= 0){
			pageNum = 1;
		}
		if(pageSize == null || pageSize <= 0){
			pageSize = 10;
		}
		PageDomain pageDomain = TableSupport.buildPageRequest();
		String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
		Boolean reasonable = pageDomain.getReasonable();
		PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
        return AjaxResult.success(iQnOrderService.selectQnOrderList(orderInfo));
    }

    /**
    * 通过id查询门诊订单详情
    * @param id
    * @return R
    */
	@ApiOperation(value = "通过id查询门诊订单")
    @GetMapping("/{id}")
	@ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    public AjaxResult getById(HttpServletRequest request, @PathVariable("id") String id){
		QnOrder orderInfo = new QnOrder();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("订单ID错误",orderInfo);
		}
		String wxUserId =  ThirdSessionHolder.getWxUserId();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("用户未登录",orderInfo);
		}
		orderInfo.setWxUserId(wxUserId);
		orderInfo.setId(Long.valueOf(id));
		List<QnOrder> orders = iQnOrderService.selectQnOrderList(orderInfo);
		if(CollectionUtils.isEmpty(orders)){
			return AjaxResult.error("查询异常",orderInfo);
		}
		QnOrder order = orders.get(0);
		if(order == null || order.getIsDeleted()){
			return AjaxResult.error(MyReturnCode.ERR_70005.getCode(), MyReturnCode.ERR_70005.getMsg());
		}
		//订单状态过期时间
		iQnOrderService.checkOutTime(order);
		return AjaxResult.success(order);
    }

    /**
    * 新增门诊订单
    * @param placeOrderDTO 门诊订单
    * @return R
    */
	@ApiOperation(value = "新增门诊订单")
    @PostMapping
	@ApiImplicitParam(name = "placeOrderDTO", required = true, dataTypeClass = PlaceOrderDTO.class,dataType = "PlaceOrderDTO",paramType = "body" )
    public AjaxResult save(@RequestBody PlaceOrderDTO placeOrderDTO){
		QnOrder orderInfo = new QnOrder();
		//用户id
		String wxUserId =  ThirdSessionHolder.getWxUserId();
		if(StringUtils.isNotEmpty(wxUserId)){
			return AjaxResult.error("用户未登录",orderInfo);
		}
		orderInfo.setWxUserId(wxUserId);
		//支付方式
		orderInfo.setPaymentWay(MallConstants.PAYMENT_WAY_2);
		if(StringUtils.isNotEmpty(placeOrderDTO.getUserMessage())){
			//买家留言
			orderInfo.setUserMessage(placeOrderDTO.getUserMessage());
		}
		//创建订单
		orderInfo = iQnOrderService.createOrder(orderInfo);
		if(orderInfo == null || Objects.isNull(orderInfo.getId())){
			return AjaxResult.error(MyReturnCode.ERR_70003.getCode(), MyReturnCode.ERR_70003.getMsg());
		}
		return AjaxResult.success(orderInfo);
    }

    /**
    * 通过id删除门诊订单
    * @param id
    * @return R
    */
	@ApiOperation(value = "通过id删除门诊订单")
    @DeleteMapping("/{id}")
	@ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
	public AjaxResult removeById(@PathVariable String id){
		QnOrder orderInfo = new QnOrder();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("订单ID错误",orderInfo);
		}
		String wxUserId =  ThirdSessionHolder.getWxUserId();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("用户未登录",orderInfo);
		}
		orderInfo.setWxUserId(wxUserId);
		orderInfo.setId(Long.valueOf(id));
		List<QnOrder> orders = iQnOrderService.selectQnOrderList(orderInfo);
		if(CollectionUtils.isEmpty(orders)){
			return AjaxResult.error("查询异常",orderInfo);
		}
		QnOrder order = orders.get(0);
		if(order == null || order.getIsDeleted()){
			return AjaxResult.error(MyReturnCode.ERR_70005.getCode(), MyReturnCode.ERR_70005.getMsg());
		}
		// !已经取消 || 已支付  ===>不支持删除
		if(!OrderInfoEnum.STATUS_5.getValue().equals(orderInfo.getStatus()) || CommonConstants.YES.equals(orderInfo.getIsPay())){
			return AjaxResult.error(MyReturnCode.ERR_70001.getCode(), MyReturnCode.ERR_70001.getMsg());
		}
		//逻辑删除
		return AjaxResult.success(iQnOrderService.logicDeleteById(Long.valueOf(id)));
    }

	/**
	 * 取消门诊订单
	 * @param id 门诊订单
	 * @return R
	 */
	@ApiOperation(value = "取消门诊订单")
	@PutMapping("/cancel/{id}")
	@ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
	public AjaxResult orderCancel(@PathVariable String id){
		QnOrder orderInfo = new QnOrder();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("订单ID错误",orderInfo);
		}
		String wxUserId =  ThirdSessionHolder.getWxUserId();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("用户未登录",orderInfo);
		}
		orderInfo.setWxUserId(wxUserId);
		orderInfo.setId(Long.valueOf(id));
		List<QnOrder> orders = iQnOrderService.selectQnOrderList(orderInfo);
		if(CollectionUtils.isEmpty(orders)){
			return AjaxResult.error("查询异常",orderInfo);
		}
		QnOrder order = orders.get(0);
		if(order == null || order.getIsDeleted()){
			return AjaxResult.error(MyReturnCode.ERR_70005.getCode(), MyReturnCode.ERR_70005.getMsg());
		}
		if(!CommonConstants.NO.equals(orderInfo.getIsPay())){//只有未支付订单能取消
			return AjaxResult.error(MyReturnCode.ERR_70001.getCode(), MyReturnCode.ERR_70001.getMsg());
		}
		//取消订单
		iQnOrderService.orderCancel(order);
		return AjaxResult.success();
	}

	/**
	 * 门诊订单确认诊断完成
	 * @param id 门诊订单ID
	 * @return R
	 */
	@ApiOperation(value = "门诊订单确认诊断完成")
	@PutMapping("/receive/{id}")
	@ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
	public AjaxResult orderReceive(@PathVariable String id){
		QnOrder orderInfo = new QnOrder();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("订单ID错误",orderInfo);
		}
		String wxUserId =  ThirdSessionHolder.getWxUserId();
		if(StringUtils.isNotEmpty(id)){
			return AjaxResult.error("用户未登录",orderInfo);
		}
		orderInfo.setWxUserId(wxUserId);
		orderInfo.setId(Long.valueOf(id));
		List<QnOrder> orders = iQnOrderService.selectQnOrderList(orderInfo);
		if(CollectionUtils.isEmpty(orders)){
			return AjaxResult.error("查询异常",orderInfo);
		}
		QnOrder order = orders.get(0);
		if(order == null || order.getIsDeleted()){
			return AjaxResult.error(MyReturnCode.ERR_70005.getCode(), MyReturnCode.ERR_70005.getMsg());
		}
		if(!OrderInfoEnum.STATUS_2.getValue().equals(orderInfo.getStatus())){//只有待收货订单能诊断完成
			return AjaxResult.error(MyReturnCode.ERR_70001.getCode(), MyReturnCode.ERR_70001.getMsg());
		}
		iQnOrderService.orderReceive(orderInfo);
		return AjaxResult.success();
	}

	/**
	 * 调用统一下单接口，并组装生成支付所需参数对象.
	 *
	 * @param orderInfo 统一下单请求参数
	 * @return 返回 {@link com.github.binarywang.wxpay.bean.order}包下的类对象
	 */
	@ApiOperation(value = "调用统一下单支付接口")
	@PostMapping("/unifiedOrder")
	@ApiImplicitParam(name = "qnOrder", required = true, dataTypeClass = QnOrder.class,dataType = "QnOrder" )
	public AjaxResult unifiedOrder(HttpServletRequest request, @RequestBody QnOrder orderInfo) throws WxPayException {
		//检验用户session登录
		WxUserEntity wxUser = new WxUserEntity();
		wxUser.setId(ThirdSessionHolder.getThirdSession().getWxUserId());
		wxUser.setSessionKey(ThirdSessionHolder.getThirdSession().getSessionKey());
		wxUser.setOpenId(ThirdSessionHolder.getThirdSession().getOpenId());
		orderInfo = iQnOrderService.selectQnOrderById(orderInfo.getId());
		if(orderInfo == null){
			return AjaxResult.error(MyReturnCode.ERR_70005.getCode(), MyReturnCode.ERR_70005.getMsg());
		}
		if(!CommonConstants.NO.equals(orderInfo.getIsPay())){//只有未支付的详单能发起支付
			return AjaxResult.error(MyReturnCode.ERR_70004.getCode(), MyReturnCode.ERR_70004.getMsg());
		}
		if(orderInfo.getPaymentPrice().compareTo(BigDecimal.ZERO)==0){//0元购买不调支付
			orderInfo.setPaymentTime(DateUtils.getNowDate());
			iQnOrderService.notifyOrder(orderInfo);
			return AjaxResult.success();
		}
		// 预约科目信息
		QnCourse course =  qnCourseService.selectQnCourseById(orderInfo.getCourseId());

		String appId = WxMaUtil.getAppId(request);
		WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
		wxPayUnifiedOrderRequest.setAppid(appId);
		String body = course.getName();//门诊名称
		body = body.length() > 40 ? body.substring(0,39) : body;
		wxPayUnifiedOrderRequest.setBody(body);
		wxPayUnifiedOrderRequest.setOutTradeNo(orderInfo.getOrderNo());
		wxPayUnifiedOrderRequest.setTotalFee(orderInfo.getPaymentPrice().multiply(new BigDecimal(100)).intValue());
		wxPayUnifiedOrderRequest.setTradeType("JSAPI");
		wxPayUnifiedOrderRequest.setNotifyUrl(wxMaProperties.getNotifyHost()+"/weixin/api/ma/orderinfo/notify-order");
		wxPayUnifiedOrderRequest.setSpbillCreateIp(wxMaProperties.getSpbillCreateIp());
		wxPayUnifiedOrderRequest.setOpenid(wxUser.getOpenId());
		WxPayService wxPayService = WxPayConfiguration.getPayService();
		return AjaxResult.success(JSONUtil.parse(wxPayService.createOrder(wxPayUnifiedOrderRequest)));
	}

	/**
	 * 支付回调
	 * @param xmlData
	 * @return
	 * @throws WxPayException
	 */
	@ApiOperation(value = "支付回调")
	@PostMapping("/notify-order")
	@ApiImplicitParam(name = "xmlData", value = "支付回调XML", required = true, dataType = "String", paramType = "body", dataTypeClass = String.class)
	public String notifyOrder(@RequestBody String xmlData) throws WxPayException {
		log.info("支付回调:"+xmlData);
		WxPayService wxPayService = WxPayConfiguration.getPayService();
		WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);

		List<QnOrder> orders = iQnOrderService.selectQnOrderList(new QnOrder()
				.setOrderNo(notifyResult.getOutTradeNo())
				.setWxUserId(ThirdSessionHolder.getThirdSession().getWxUserId()));
		if(!CollectionUtils.isEmpty(orders) &&  Objects.nonNull(orders.get(0))){
			QnOrder orderInfo = orders.get(0);
			if(orderInfo.getPaymentPrice().multiply(new BigDecimal(100)).intValue() == notifyResult.getTotalFee()){
				String timeEnd = notifyResult.getTimeEnd();
				LocalDateTime paymentTime = LocalDateTimeUtils.parse(timeEnd);
				orderInfo.setPaymentTime(DateUtils.toDate(paymentTime));
				orderInfo.setTransactionId(notifyResult.getTransactionId());
				//订单支付处理
				iQnOrderService.notifyOrder(orderInfo);
				return WxPayNotifyResponse.success("成功");
			}else{
				return WxPayNotifyResponse.fail("付款金额与订单金额不等");
			}
		}else{
			return WxPayNotifyResponse.fail("无此订单");
		}
	}
}
