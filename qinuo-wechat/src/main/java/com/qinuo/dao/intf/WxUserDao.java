package com.qinuo.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.qinuo.entity.WxUserEntity;

import java.util.List;

/**
 * WxUserDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface WxUserDao extends IBaseDao<WxUserEntity> {

    /**
     * 根据微信用户标识查询
     * @param openId 用户标识
     * @return
     */
    WxUserEntity selectByOpenId(String openId);

    /**
     * 查询微信用户一览
     * @param wxUser
     * @return
     */
    List<WxUserEntity> selectByEntity(WxUserEntity wxUser);
}
