package com.qinuo.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.qinuo.domain.QnDoctor;
import com.qinuo.entity.QnDoctorEntity;

import java.util.List;

/**
 * QnDoctorDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface QnDoctorDao extends IBaseDao<QnDoctorEntity> {

    /**
     * 查询件数
     * @param qnDoctor
     * @return
     */
    int countQnDoctor(QnDoctor qnDoctor);

    /**
     * 分页查询
     * @param qnDoctor
     * @return
     */
    List<QnDoctorEntity> selectQnDoctorList(QnDoctor qnDoctor);
}
