package com.qinuo.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonObject;
import com.qinuo.common.constant.HttpStatus;
import com.qinuo.common.core.page.PageDomain;
import com.qinuo.common.core.page.TableDataInfo;
import com.qinuo.common.core.page.TableSupport;
import com.qinuo.common.utils.PageUtils;
import com.qinuo.common.utils.SecurityUtils;
import com.qinuo.common.utils.sql.SqlUtil;
import com.qinuo.coverter.QnDoctorConverter;
import com.qinuo.dao.intf.QnDoctorDao;
import com.qinuo.domain.QnDoctor;
import com.qinuo.entity.QnDoctorEntity;
import com.qinuo.service.IQnDoctorService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 医生管理Service业务层处理
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@Service
public class QnDoctorServiceImpl implements IQnDoctorService
{
    @Autowired
    private QnDoctorDao qnDoctorDao;

    /**
     * 查询医生管理
     * 
     * @param id 医生管理主键
     * @return 医生管理
     */
    @Override
    public QnDoctor selectQnDoctorById(Long id)
    {
        return QnDoctorConverter.INSTANCE.toQnDoctor(qnDoctorDao.selectById(id));
    }

    /**
     * 查询医生管理列表
     * 
     * @param qnDoctor 医生管理
     * @return 医生管理
     */
    @Override
    public TableDataInfo selectQnDoctorList(QnDoctor qnDoctor)
    {
        Integer pageNum = Math.toIntExact(TableSupport.getPage().getStartRow());
        Integer pageSize = TableSupport.getPage().getPageSize();

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(Lists.newArrayList());
        rspData.setTotal(0);

        int total =  qnDoctorDao.countQnDoctor(qnDoctor);
        if(total > 0) {
            rspData.setTotal(total);
            List<QnDoctorEntity> entities =  qnDoctorDao.selectQnDoctorList(qnDoctor, pageNum, pageSize);
            rspData.setRows(entities.stream().map(QnDoctorConverter.INSTANCE::toQnDoctor).collect(Collectors.toList()));
        }

        return rspData;
    }

    /**
     * 新增医生管理
     * 
     * @param qnDoctor 医生管理
     * @return 结果
     */
    @Override
    public int insertQnDoctor(QnDoctor qnDoctor)
    {
        QnDoctorEntity saveEntity = QnDoctorConverter.INSTANCE.toQnDoctorEntity(qnDoctor);
        saveEntity.setCreateBy(getUsername());
        Long id = qnDoctorDao.save(saveEntity) ;
        return Objects.nonNull(id) ? 1 : 0;
    }

    /**
     * 获取当前登录用户名
     * @return
     */
    private String getUsername() {
        return SecurityUtils.getLoginUser().getUsername();
    }

    /**
     * 修改医生管理
     * 
     * @param qnDoctor 医生管理
     * @return 结果
     */
    @Override
    public int updateQnDoctor(QnDoctor qnDoctor)
    {
        QnDoctorEntity saveEntity = QnDoctorConverter.INSTANCE.toQnDoctorEntity(qnDoctor);
        saveEntity.setUpdateBy(getUsername());
        return qnDoctorDao.updateById(saveEntity)? 1 : 0;
    }

    /**
     * 批量删除医生管理
     * 
     * @param ids 需要删除的医生管理主键
     * @return 结果
     */
    @Override
    public int deleteQnDoctorByIds(Long[] ids)
    {
        return qnDoctorDao.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除医生管理信息
     * 
     * @param id 医生管理主键
     * @return 结果
     */
    @Override
    public int deleteQnDoctorById(Long id)
    {
        return qnDoctorDao.deleteById(id) ? 1 : 0;
    }
}
