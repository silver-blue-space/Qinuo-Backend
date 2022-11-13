package com.qinuo.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.qinuo.common.constant.HttpStatus;
import com.qinuo.common.core.domain.AjaxResult;
import com.qinuo.common.core.domain.entity.SysUser;
import com.qinuo.common.core.domain.model.LoginUser;
import com.qinuo.common.core.page.TableDataInfo;
import com.qinuo.common.exception.ServiceException;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.SecurityUtils;
import com.qinuo.coverter.QnSchedulingConverter;
import com.qinuo.dao.intf.QnCourseDao;
import com.qinuo.dao.intf.QnSchedulingDao;
import com.qinuo.domain.QnSchedulingBatchSave;
import com.qinuo.entity.QnCourseEntity;
import com.qinuo.entity.QnSchedulingEntity;
import com.qinuo.system.service.ISysUserService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinuo.domain.QnScheduling;
import com.qinuo.service.IQnSchedulingService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 排班管理Service业务层处理
 * 
 * @author qinuo
 * @date 2022-10-29
 */
@Service
public class QnSchedulingServiceImpl implements IQnSchedulingService 
{
    @Autowired
    private QnSchedulingDao qnSchedulingDao;
    @Autowired
    private QnCourseDao qnCourseDao;
    @Autowired
    private ISysUserService userService;

    /**
     * 查询排班管理
     * 
     * @param id 排班管理主键
     * @return 排班管理
     */
    @Override
    public QnScheduling selectQnSchedulingById(Long id)
    {
        QnSchedulingEntity entity =  qnSchedulingDao.selectById(id);
        return QnSchedulingConverter.INSTANCE.toQnScheduling(entity,Maps.newHashMap());
    }

    /**
     * 查询排班管理列表
     * 
     * @param qnScheduling 排班管理
     * @param pageNum 当前记录起始页
     * @param pageSize 每页件数
     * @return 排班管理
     */
    @Override
    public TableDataInfo selectQnSchedulingList(QnScheduling qnScheduling, Integer pageNum, Integer pageSize)
    {

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(Lists.newArrayList());
        rspData.setTotal(0);

        int total =  qnSchedulingDao.countQnSchedulingList(qnScheduling);
        if(total > 0){
            rspData.setTotal(total);
            List<QnSchedulingEntity> entities =  qnSchedulingDao.selectQnSchedulingList(qnScheduling, pageNum, pageSize);
            List<QnScheduling> list  = entities.stream().map(e -> QnSchedulingConverter.INSTANCE.toQnScheduling(e, Maps.newHashMap())).collect(Collectors.toList());
            rspData.setRows(list);
        }
        return rspData;
    }

    /**
     * 查询全部排班管理列表
     *
     * @param qnScheduling 排班管理
     * @return 排班管理
     */
    @Override
    public List<QnScheduling> selectQnSchedulingList(QnScheduling qnScheduling)
    {
        List<QnSchedulingEntity> entities =  qnSchedulingDao.selectQnSchedulingList(qnScheduling);
        if(CollectionUtils.isEmpty(entities)){
            return Lists.newArrayList();
        }
        List<QnCourseEntity> courseEntities =  qnCourseDao.selectByIds(entities.stream().map(QnSchedulingEntity::getCourseId).collect(Collectors.toList()));
        Map<Long,String>  colorMap =  courseEntities.stream().collect(Collectors.toMap(QnCourseEntity::getId,QnCourseEntity::getBackgroundColor));
        return entities.stream().map(e -> QnSchedulingConverter.INSTANCE.toQnScheduling(e,colorMap)).collect(Collectors.toList());
    }

    /**
     * 新增排班管理
     * 
     * @param qnScheduling 排班管理
     * @return 结果
     */
    @Override
    public int insertQnScheduling(QnScheduling qnScheduling)
    {
        qnScheduling.setCreateTime(DateUtils.getNowDate());
        QnSchedulingEntity updateEntity =  QnSchedulingConverter.INSTANCE.toQnSchedulingEntity(qnScheduling);
        updateEntity.setCreateBy(getUsername());

        Long id =  qnSchedulingDao.save(updateEntity);
        return Objects.nonNull(id) ? 1: 0;
    }
    /**
     * 获取当前登录用户名
     * @return
     */
    private String getUsername() {
        return SecurityUtils.getLoginUser().getUsername();
    }

    /**
     * 修改排班管理
     * 
     * @param qnScheduling 排班管理
     * @return 结果
     */
    @Override
    public int updateQnScheduling(QnScheduling qnScheduling)
    {
        qnScheduling.setUpdateTime(DateUtils.getNowDate());
        QnSchedulingEntity updateEntity =  QnSchedulingConverter.INSTANCE.toQnSchedulingEntity(qnScheduling);
        updateEntity.setUpdateBy(getUsername());
        //check
        check(updateEntity.getId(), updateEntity.getUserId(), qnScheduling.getSchedulDate(), updateEntity.getAttendTime(), updateEntity.getFinishTime());

        return qnSchedulingDao.updateById(updateEntity)? 1 : 0;
    }

    /**
     * 排班校验
     * @param id 排班ID
     * @param userId 医生ID
     * @param schedulDate 上班日期
     * @param attendTime 门诊开始时间
     * @param finishTime 门诊结束时间
     */
    private void check(Long id, Long userId, String schedulDate, LocalTime attendTime, LocalTime finishTime) {

        //医生排班冲突检查
        List<QnSchedulingEntity> teacherVoList = qnSchedulingDao.selectQnSchedulingList(new QnScheduling().setUserId(userId).setSchedulDate(schedulDate));
        if(!CollectionUtils.isEmpty(teacherVoList)){

            for (QnSchedulingEntity vo : teacherVoList) {
                //课程本身
                if(Objects.nonNull(id) && id.compareTo(vo.getId()) == 0){
                    continue;
                }
                /**
                 * 同一老师不能同时上多节课
                 */
                if (isTimeConflict(attendTime, finishTime, vo.getAttendTime(), vo.getFinishTime())) {
                    SysUser sysUser = userService.selectUserById(vo.getUserId());
                    QnCourseEntity course = qnCourseDao.selectById(vo.getCourseId());
                    throw new ServiceException(String.format("门诊排班时间冲突，冲突信息：%s %s-%s %s %s", vo.getSchedulDate(), vo.getAttendTime(), vo.getFinishTime(), course.getName(), sysUser.getNickName()));
                }
            }
        }

    }

    /**
     * 批量删除排班管理
     * 
     * @param ids 需要删除的排班管理主键
     * @return 结果
     */
    @Override
    public int deleteQnSchedulingByIds(Long[] ids)
    {
        return qnSchedulingDao.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除排班管理信息
     * 
     * @param id 排班管理主键
     * @return 结果
     */
    @Override
    public int deleteQnSchedulingById(Long id)
    {
        return qnSchedulingDao.deleteById(id)? 1 : 0;
    }

    /**
     * 批量排班
     * @param saveDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult batchSaveCourseScheduling(QnSchedulingBatchSave saveDTO) {
        List<Integer> weekList = saveDTO.getWeekList();
        LocalDate startDate = saveDTO.getStartDate();
        LocalDate endDate = saveDTO.getEndDate();
        List<LocalDate> dateList = new ArrayList<>();
        while (startDate.compareTo(endDate) <= 0) {
            if (weekList.contains(startDate.getDayOfWeek().getValue())) {
                dateList.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }

        Set<String> errorSet = new HashSet<>();

        //医生排班冲突检查
        List<QnSchedulingEntity> teacherVoList = qnSchedulingDao.selectCourseSchedulingList(saveDTO.getUserId(),dateList);
        if(!CollectionUtils.isEmpty(teacherVoList)){
            for (QnSchedulingEntity vo : teacherVoList) {
                if (isTimeConflict(saveDTO.getAttendTime(), saveDTO.getFinishTime(), vo.getAttendTime(), vo.getFinishTime())) {
                    SysUser sysUser = userService.selectUserById(vo.getUserId());
                    QnCourseEntity course = qnCourseDao.selectById(vo.getCourseId());
                    errorSet.add(String.format("%s %s-%s %s %s", vo.getSchedulDate(), vo.getAttendTime(), vo.getFinishTime(), course.getName(), sysUser.getNickName()));
                }
            }
        }

        if (errorSet.size() > 0) {
            return  AjaxResult.error("检测到排课时间冲突",errorSet);
        }

        LoginUser user = SecurityUtils.getLoginUser();
        for (LocalDate date : dateList) {
            QnSchedulingEntity dataObj = QnSchedulingConverter.INSTANCE.batchSaveDto2Entity(saveDTO);
            dataObj.setSchedulDate(date);
            dataObj.setCreateBy(user.getUsername());
            qnSchedulingDao.save(dataObj);
        }


        return AjaxResult.success();
    }
    /**
     * 新增时间范围跨度较小，在现有时间范围内
     *     新增时间是否在已有课程的时间段，在则冲突
     * 新增时间范围跨度较大大，包含现有时间段
     *     现有时间是否在新增时间范围内，在则冲突
     * 时间完全相等
     */
    private boolean isTimeConflict(LocalTime attendTime1, LocalTime finishTime1, LocalTime attendTime2, LocalTime finishTime2) {
        return isBetween(attendTime1, attendTime2, finishTime2)
                || isBetween(finishTime1, attendTime2, finishTime2)
                || isBetween(attendTime2, attendTime1, finishTime1)
                || isBetween(finishTime2, attendTime1, finishTime1)
                || (attendTime1.equals(attendTime2) && finishTime1.equals(finishTime2));
    }
    private boolean isBetween(LocalTime time, LocalTime destTime1, LocalTime destTime2) {
        Integer minute = calcMinute(time);
        return calcMinute(destTime1) < minute && minute < calcMinute(destTime2);
    }
    private Integer calcMinute(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }
}
