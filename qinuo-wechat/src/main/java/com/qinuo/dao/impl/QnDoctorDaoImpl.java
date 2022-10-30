package com.qinuo.dao.impl;

import cn.org.atool.fluent.mybatis.If;
import com.qinuo.dao.base.QnDoctorBaseDao;
import com.qinuo.dao.intf.QnDoctorDao;
import com.qinuo.domain.QnDoctor;
import com.qinuo.entity.QnDoctorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QnDoctorDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class QnDoctorDaoImpl extends QnDoctorBaseDao implements QnDoctorDao {


    @Override
    public int countQnDoctor(QnDoctor param) {
        return this.query()
                .where
                .enableState().eq(param.getEnableState())
                .and.showFlg().eq(param.getShowFlg(),If::notBlank)
                .and.expertise().like(param.getExpertise(),If::notBlank)
                .and.introduce().like(param.getIntroduce(),If::notBlank)
                .end()
                .execute(this::count);
    }

    @Override
    public List<QnDoctorEntity> selectQnDoctorList(QnDoctor param, Integer pageNum, Integer pageSize) {
        return this.query()
                .where
                .enableState().eq(param.getEnableState())
                .and.showFlg().eq(param.getShowFlg(),If::notBlank)
                .and.expertise().like(param.getExpertise(),If::notBlank)
                .and.introduce().like(param.getIntroduce(),If::notBlank)
                .end()
                .limit(pageNum,pageSize)
                .execute(this::listEntity);
    }
}
