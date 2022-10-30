package com.qinuo.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.qinuo.common.constant.HttpStatus;
import com.qinuo.common.core.page.PageDomain;
import com.qinuo.common.core.page.TableDataInfo;
import com.qinuo.common.core.page.TableSupport;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.common.utils.PageUtils;
import com.qinuo.coverter.QnSchedulingConverter;
import com.qinuo.dao.intf.QnSchedulingDao;
import com.qinuo.entity.QnSchedulingEntity;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinuo.mapper.QnSchedulingMapper;
import com.qinuo.domain.QnScheduling;
import com.qinuo.service.IQnSchedulingService;
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
        return QnSchedulingConverter.INSTANCE.toQnScheduling(entity);
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
            List<QnScheduling> list  = entities.stream().map(QnSchedulingConverter.INSTANCE::toQnScheduling).collect(Collectors.toList());
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
        return entities.stream().map(QnSchedulingConverter.INSTANCE::toQnScheduling).collect(Collectors.toList());
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
        return qnSchedulingDao.save(updateEntity);
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
        return qnSchedulingDao.updateById(updateEntity)? 1 : 0;
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
}
