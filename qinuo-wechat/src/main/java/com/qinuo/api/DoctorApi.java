package com.qinuo.api;


import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Maps;
import com.qinuo.common.annotation.Anonymous;
import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.common.core.domain.entity.SysUser;
import com.qinuo.common.utils.StringUtils;
import com.qinuo.coverter.QnDoctorConverter;
import com.qinuo.domain.QnCourse;
import com.qinuo.domain.QnDoctor;
import com.qinuo.domain.QnScheduling;
import com.qinuo.service.IQnCourseService;
import com.qinuo.service.IQnDoctorService;
import com.qinuo.service.IQnSchedulingService;
import com.qinuo.system.mapper.SysUserMapper;
import com.qinuo.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Anonymous
@RestController
@RequestMapping("/weixin/api/ma/doctor")
@Api(value = "DoctorApi", tags = "医生信息管理Api")
public class DoctorApi {

    @Autowired
    private IQnCourseService qnCourseService;
    @Autowired
    private IQnDoctorService qnDoctorService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private IQnSchedulingService qnSchedulingService;


    @ApiOperation("获取门诊科目列表")
    @GetMapping("/course")
    public AjaxResult course(){
        List<QnCourse> list = qnCourseService.selectQnCourseList(new QnCourse().setEnableState("0"));
        return AjaxResult.success(list);
    }

    @ApiOperation("获取医生列表")
    @GetMapping("/list")
    public AjaxResult list(){

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
        List<SysUser> userList = userMapper.selectUserList(user);
        if(CollectionUtils.isEmpty(userList)){
            AjaxResult.success(Lists.newArrayList());
        }
        Map<Long,SysUser> listUser = userList.stream().filter(e -> sysUserIds.contains(e.getUserId())).collect(Collectors.toMap(SysUser::getUserId,e -> e));

        return AjaxResult.success(list.stream().map(e -> QnDoctorConverter.INSTANCE.toQnDoctorDTO(e,listUser)).collect(Collectors.toList()));
    }


    @ApiOperation("获取门诊医生排班日历")
    @GetMapping("/scheduling")
    @ApiImplicitParams({@ApiImplicitParam(name = "startDate", value = "开始时间", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "endDate", value = "结束时间", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "courseId", value = "门诊科目ID", dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "doctorId", value = "医生ID", dataType = "String", paramType = "path", dataTypeClass = String.class)})
    public AjaxResult scheduling(String startDate,String endDate,String courseId,String doctorId){

        QnScheduling qnScheduling =  new QnScheduling();
        if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)){
            Map<String, Object> params = Maps.newHashMap();
            params.put("beginTime",startDate);
            params.put("endTime",endDate);
            qnScheduling.setParams(params);
        }else {
            AjaxResult.error("门诊排班开始~结束时间不能为空！");
        }

        if(StringUtils.isEmpty(courseId) && NumberUtil.isLong(courseId)){
            qnScheduling.setCourseId(NumberUtil.parseLong(courseId));
        }
        if(StringUtils.isEmpty(doctorId) && NumberUtil.isLong(doctorId)){
            qnScheduling.setUserId(NumberUtil.parseLong(doctorId));
        }

        List<QnScheduling> list =  qnSchedulingService.selectQnSchedulingList(qnScheduling);
        Map<String,Long> scheduleCount = list.stream().collect(Collectors.groupingBy(QnScheduling::getSchedulDate, Collectors.counting()));
        //TODO：去除已经预约件数.
        return AjaxResult.success(list).put("scheduleCount",scheduleCount);
    }
}
