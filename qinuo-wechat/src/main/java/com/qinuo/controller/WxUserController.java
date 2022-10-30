package com.qinuo.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.qinuo.entity.WxUserEntity;
import com.qinuo.service.WxUserService;
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
import com.qinuo.common.utils.poi.ExcelUtil;
import com.qinuo.common.core.page.TableDataInfo;

/**
 * 微信用户Controller
 * 
 * @author qinuo
 * @date 2022-10-23
 */
@RestController
@RequestMapping("/wechat/wechat")
public class WxUserController extends BaseController
{
    @Autowired
    private WxUserService wxUserService;

    /**
     * 查询微信用户列表
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:list')")
    @GetMapping("/list")
    public TableDataInfo list(WxUserEntity wxUser)
    {
        startPage();
        List<WxUserEntity> list = wxUserService.selectWxUserList(wxUser);
        return getDataTable(list);
    }

    /**
     * 导出微信用户列表
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:export')")
    @Log(title = "微信用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WxUserEntity wxUser)
    {
        List<WxUserEntity> list = wxUserService.selectWxUserList(wxUser);
        ExcelUtil<WxUserEntity> util = new ExcelUtil<WxUserEntity>(WxUserEntity.class);
        util.exportExcel(response, list, "微信用户数据");
    }

    /**
     * 获取微信用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(wxUserService.selectWxUserById(id));
    }

    /**
     * 新增微信用户
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:add')")
    @Log(title = "微信用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WxUserEntity wxUser)
    {
        return toAjax(wxUserService.insertWxUser(wxUser));
    }

    /**
     * 修改微信用户
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:edit')")
    @Log(title = "微信用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WxUserEntity wxUser)
    {
        return toAjax(wxUserService.updateWxUser(wxUser));
    }

    /**
     * 删除微信用户
     */
    @PreAuthorize("@ss.hasPermi('wechat:wechat:remove')")
    @Log(title = "微信用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wxUserService.deleteWxUserByIds(ids));
    }
}
