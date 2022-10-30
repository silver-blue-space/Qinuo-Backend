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
import com.qinuo.domain.QnCourse;
import com.qinuo.service.IQnCourseService;
import com.qinuo.common.utils.poi.ExcelUtil;
import com.qinuo.common.core.page.TableDataInfo;

/**
 * 门诊科目Controller
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@RestController
@RequestMapping("/doctor/course")
public class QnCourseController extends BaseController
{
    @Autowired
    private IQnCourseService qnCourseService;

    /**
     * 查询门诊科目列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(QnCourse qnCourse)
    {
        int total =  qnCourseService.countQnDoctor(qnCourse);
        if(total <= 0){
            return getDataTable(Lists.newArrayList());
        }
        startPage();
        List<QnCourse> list = qnCourseService.selectQnCourseList(qnCourse);
        return  getDataTable(list,total);
    }

    /**
     * 导出门诊科目列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:export')")
    @Log(title = "门诊科目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QnCourse qnCourse)
    {
        List<QnCourse> list = qnCourseService.selectQnCourseList(qnCourse);
        ExcelUtil<QnCourse> util = new ExcelUtil<QnCourse>(QnCourse.class);
        util.exportExcel(response, list, "门诊科目数据");
    }

    /**
     * 获取门诊科目详细信息
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(qnCourseService.selectQnCourseById(id));
    }

    /**
     * 新增门诊科目
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:add')")
    @Log(title = "门诊科目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QnCourse qnCourse)
    {
        qnCourse.setCreateBy(getUsername());
        return toAjax(qnCourseService.insertQnCourse(qnCourse));
    }

    /**
     * 修改门诊科目
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:edit')")
    @Log(title = "门诊科目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QnCourse qnCourse)
    {
        qnCourse.setUpdateBy(getUsername());
        return toAjax(qnCourseService.updateQnCourse(qnCourse));
    }

    /**
     * 删除门诊科目
     */
    @PreAuthorize("@ss.hasPermi('doctor:course:remove')")
    @Log(title = "门诊科目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(qnCourseService.deleteQnCourseByIds(ids));
    }
}
