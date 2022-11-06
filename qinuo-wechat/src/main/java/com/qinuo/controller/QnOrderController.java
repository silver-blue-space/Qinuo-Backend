package com.qinuo.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.Lists;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qinuo.common.annotation.Log;
import com.qinuo.common.core.controller.BaseController;
import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.common.enums.BusinessType;
import com.qinuo.domain.QnOrder;
import com.qinuo.service.IQnOrderService;
import com.qinuo.common.utils.poi.ExcelUtil;
import com.qinuo.common.core.page.TableDataInfo;

/**
 * 订单管理Controller
 * 
 * @author qinuo
 * @date 2022-11-06
 */
@RestController
@RequestMapping("/wechat/order")
public class QnOrderController extends BaseController
{
    @Autowired
    private IQnOrderService qnOrderService;

    /**
     * 查询订单管理列表
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(QnOrder qnOrder)
    {
        int total =  qnOrderService.countQnOrder(qnOrder);
        if(total <= 0){
            return getDataTable(Lists.newArrayList());
        }
        startPage();
        List<QnOrder> list = qnOrderService.selectQnOrderList(qnOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单管理列表
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:export')")
    @Log(title = "订单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QnOrder qnOrder)
    {
        List<QnOrder> list = qnOrderService.selectQnOrderList(qnOrder);
        ExcelUtil<QnOrder> util = new ExcelUtil<QnOrder>(QnOrder.class);
        util.exportExcel(response, list, "订单管理数据");
    }

    /**
     * 获取订单管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(qnOrderService.selectQnOrderById(id));
    }

    /**
     * 新增订单管理
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:add')")
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QnOrder qnOrder)
    {
        return toAjax(qnOrderService.insertQnOrder(qnOrder));
    }

    /**
     * 修改订单管理
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:edit')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QnOrder qnOrder)
    {
        return toAjax(qnOrderService.updateQnOrder(qnOrder));
    }

    /**
     * 删除订单管理
     */
    @PreAuthorize("@ss.hasPermi('wechat:order:remove')")
    @Log(title = "订单管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(qnOrderService.deleteQnOrderByIds(ids));
    }
}
