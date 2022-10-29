package com.qinuo.dao.impl;

import cn.org.atool.fluent.mybatis.If;
import com.qinuo.dao.base.WxUserBaseDao;
import com.qinuo.dao.intf.WxUserDao;
import com.qinuo.entity.WxUserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WxUserDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class WxUserDaoImpl extends WxUserBaseDao implements WxUserDao {


    @Override
    public WxUserEntity selectByOpenId(String openId) {
        return this.query()
                .where.openId().eq(openId).end()
                .limit(0)
                .execute(this::findOne)
                .orElse(null);
    }

    @Override
    public List<WxUserEntity> selectByEntity(WxUserEntity wxUser) {
        return this.query()
                .where.openId().eq(wxUser.getOpenId(), If::notBlank)
                .sex().eq(wxUser.getSex(), If::notBlank)
                .nickName().like(wxUser.getNickName(), If::notBlank)
                .updateId().eq(wxUser.getUpdateId(), If::notBlank)
                .phone().eq(wxUser.getPhone(), If::notBlank)
                .tagidList().like(wxUser.getTagidList(), If::notBlank)
                .end()
                .execute(this::listEntity);
    }
}
