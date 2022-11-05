package com.qinuo.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.qinuo.common.core.page.PageDomain;
import com.qinuo.common.core.page.TableSupport;
import com.qinuo.domain.QnSchedulingBatchSave;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.qinuo.domain.QnScheduling;
import com.qinuo.service.IQnSchedulingService;
import com.qinuo.common.utils.poi.ExcelUtil;
import com.qinuo.common.core.page.TableDataInfo;

/**
 * 排班管理Controller
 * 
 * @author qinuo
 * @date 2022-10-29
 */
@RestController
@RequestMapping("/doctor/scheduling")
public class QnSchedulingController extends BaseController
{
    @Autowired
    private IQnSchedulingService qnSchedulingService;

    /**
     * 分页
     * 查询排班管理列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:list')")
    @GetMapping("/list")
    public TableDataInfo list(QnScheduling qnScheduling)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        return qnSchedulingService.selectQnSchedulingList(qnScheduling,pageNum,pageSize);
    }
    /**
     * 排班日历
     * 查询排班管理列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:list')")
    @GetMapping("/calendar/list")
    public AjaxResult listSchedulingCalendarList(QnScheduling query) {

        List<QnScheduling> list =  qnSchedulingService.selectQnSchedulingList(query);
        Map<String,Long> scheduleCount = list.stream().collect(Collectors.groupingBy(QnScheduling::getSchedulDate, Collectors.counting()));
        return AjaxResult.success(list).put("scheduleCount",scheduleCount);
    }

    /**
     * 导出排班管理列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:export')")
    @Log(title = "排班管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QnScheduling qnScheduling)
    {
        List<QnScheduling> list = qnSchedulingService.selectQnSchedulingList(qnScheduling);
        ExcelUtil<QnScheduling> util = new ExcelUtil<QnScheduling>(QnScheduling.class);
        util.exportExcel(response, list, "排班管理数据");
    }

    /**
     * 获取排班管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(qnSchedulingService.selectQnSchedulingById(id));
    }

    /**
     * 新增排班管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:add')")
    @Log(title = "排班管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QnScheduling qnScheduling)
    {
        return toAjax(qnSchedulingService.insertQnScheduling(qnScheduling));
    }

    @PreAuthorize("@ss.hasPermi('doctor:scheduling:add')")
    @Log(title = "批量新增管理", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult saveCourseScheduling(@Validated @RequestBody QnSchedulingBatchSave saveDTO) {

        return qnSchedulingService.batchSaveCourseScheduling(saveDTO);
    }

    /**
     * 修改排班管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:edit')")
    @Log(title = "排班管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QnScheduling qnScheduling)
    {
        return toAjax(qnSchedulingService.updateQnScheduling(qnScheduling));
    }

    /**
     * 删除排班管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:scheduling:remove')")
    @Log(title = "排班管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(qnSchedulingService.deleteQnSchedulingByIds(ids));
    }
}
