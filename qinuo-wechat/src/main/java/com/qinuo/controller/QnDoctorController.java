package com.qinuo.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.qinuo.common.core.domain.entity.SysUser;
import com.qinuo.domain.QnDoctor;
import com.qinuo.service.IQnDoctorService;
import com.qinuo.system.service.ISysUserService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
 * 医生管理Controller
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@RestController
@RequestMapping("/doctor/doctor")
public class QnDoctorController extends BaseController
{
    @Autowired
    private IQnDoctorService qnDoctorService;
    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询医生管理列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:list')")
    @GetMapping("/list")
    public TableDataInfo list(QnDoctor qnDoctor)
    {
        int total =  qnDoctorService.countQnDoctor(qnDoctor);
        if(total <= 0){
            return getDataTable(Lists.newArrayList());
        }
        startPage();
        List<QnDoctor> list = qnDoctorService.selectQnDoctorList(qnDoctor);
        return  getDataTable(list,total);
    }

    /**
     * 医生(从用户选择后的)下拉列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:list')")
    @GetMapping("/selections")
    public AjaxResult list()
    {
        QnDoctor qnDoctor = new QnDoctor();
        qnDoctor.setEnableState("0");//有效
        List<QnDoctor> list = qnDoctorService.selectQnDoctorList(qnDoctor);
        if(CollectionUtils.isEmpty(list)){
            AjaxResult.success(Lists.newArrayList());
        }
        List<Long> sysUserIds = list.stream().map(QnDoctor::getSysUserId).collect(Collectors.toList());
        SysUser user =  new SysUser();
        user.setDelFlag("0");
        user.setStatus("0");
        List<SysUser> userList = userService.selectUserList(user);
        if(CollectionUtils.isEmpty(userList)){
            AjaxResult.success(Lists.newArrayList());
        }
        List<SysUser> rtn = userList.stream().filter(e -> sysUserIds.contains(e.getUserId())).collect(Collectors.toList());
        return AjaxResult.success(rtn);

    }
    /**
     * 导出医生管理列表
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:export')")
    @Log(title = "医生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QnDoctor qnDoctor)
    {
        List<QnDoctor> list = qnDoctorService.selectQnDoctorList(qnDoctor);
        ExcelUtil<QnDoctor> util = new ExcelUtil<QnDoctor>(QnDoctor.class);
        util.exportExcel(response, (List<QnDoctor>) list, "医生管理数据");
    }

    /**
     * 获取医生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(qnDoctorService.selectQnDoctorById(id));
    }

    /**
     * 新增医生管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:add')")
    @Log(title = "医生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QnDoctor qnDoctor)
    {
        return toAjax(qnDoctorService.insertQnDoctor(qnDoctor));
    }

    /**
     * 修改医生管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:edit')")
    @Log(title = "医生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QnDoctor qnDoctor)
    {
        return toAjax(qnDoctorService.updateQnDoctor(qnDoctor));
    }

    /**
     * 删除医生管理
     */
    @PreAuthorize("@ss.hasPermi('doctor:doctor:remove')")
    @Log(title = "医生管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(qnDoctorService.deleteQnDoctorByIds(ids));
    }
}
