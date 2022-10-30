package com.qinuo.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.qinuo.common.core.page.TableSupport;
import com.qinuo.common.utils.DateUtils;
import com.qinuo.coverter.QnCourseConverter;
import com.qinuo.dao.intf.QnCourseDao;
import com.qinuo.entity.QnCourseEntity;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinuo.mapper.QnCourseMapper;
import com.qinuo.domain.QnCourse;
import com.qinuo.service.IQnCourseService;
import org.springframework.util.CollectionUtils;

/**
 * 门诊科目Service业务层处理
 * 
 * @author qinuo
 * @date 2022-10-30
 */
@Service
public class QnCourseServiceImpl implements IQnCourseService 
{
    @Autowired
    private QnCourseDao qnCourseDao;

    /**
     * 查询门诊科目
     * 
     * @param id 门诊科目主键
     * @return 门诊科目
     */
    @Override
    public QnCourse selectQnCourseById(Long id)
    {
        QnCourseEntity entity =  qnCourseDao.selectById(id);
        return QnCourseConverter.INSTANCE.toQnCourse(entity);
    }

    /**
     * 分页查询门诊科目列表
     * 
     * @param qnCourse 门诊科目
     * @return 门诊科目
     */
    @Override
    public List<QnCourse> selectQnCourseList(QnCourse qnCourse)
    {
        Integer pageNum = Math.toIntExact(TableSupport.getPage().getStartRow());
        Integer pageSize = TableSupport.getPage().getPageSize();
        List<QnCourseEntity> entities =  qnCourseDao.selectQnCourseList(qnCourse,pageNum,pageSize);
        if(CollectionUtils.isEmpty(entities)){
            return Lists.newArrayList();
        }
        return entities.stream().map(QnCourseConverter.INSTANCE::toQnCourse).collect(Collectors.toList());
    }

    /**
     * 查询总件数
     * @param qnCourse
     * @return
     */
    @Override
    public int countQnDoctor(QnCourse qnCourse) {
        return qnCourseDao.countQnDoctor(qnCourse);
    }
    /**
     * 新增门诊科目
     * 
     * @param qnCourse 门诊科目
     * @return 结果
     */
    @Override
    public int insertQnCourse(QnCourse qnCourse)
    {
        qnCourse.setCreateTime(DateUtils.getNowDate());
        QnCourseEntity entity = QnCourseConverter.INSTANCE.toQnCourseEntity(qnCourse);
        Long id = qnCourseDao.save(entity) ;
        return Objects.nonNull(id) ? 1 : 0;
    }

    /**
     * 修改门诊科目
     * 
     * @param qnCourse 门诊科目
     * @return 结果
     */
    @Override
    public int updateQnCourse(QnCourse qnCourse)
    {
        qnCourse.setUpdateTime(DateUtils.getNowDate());
        QnCourseEntity entity = QnCourseConverter.INSTANCE.toQnCourseEntity(qnCourse);
        return qnCourseDao.updateById(entity) ? 1: 0;
    }

    /**
     * 批量删除门诊科目
     * 
     * @param ids 需要删除的门诊科目主键
     * @return 结果
     */
    @Override
    public int deleteQnCourseByIds(Long[] ids)
    {
        return qnCourseDao.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 删除门诊科目信息
     * 
     * @param id 门诊科目主键
     * @return 结果
     */
    @Override
    public int deleteQnCourseById(Long id)
    {
        return qnCourseDao.deleteById(id) ? 1: 0;
    }

}
