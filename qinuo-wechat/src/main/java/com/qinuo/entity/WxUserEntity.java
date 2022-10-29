package com.qinuo.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * WxUserEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@AllArgsConstructor
@NoArgsConstructor
@FluentMybatis(
    table = "wx_user",
    schema = "qinuo"
)
public class WxUserEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId(
      value = "id",
      auto = false,
      desc = "主键"
  )
  private String id;

  @TableField(
      value = "app_type",
      desc = "应用类型(1:小程序，2:公众号)"
  )
  private String appType;

  @TableField(
      value = "cancel_subscribe_time",
      desc = "取消关注时间"
  )
  private Date cancelSubscribeTime;

  @TableField(
      value = "city",
      desc = "所在城市"
  )
  private String city;

  @TableField(
      value = "country",
      desc = "所在国家"
  )
  private String country;

  @TableField(
      value = "create_id",
      desc = "创建者"
  )
  private String createId;

  @TableField(
      value = "create_time",
      desc = "创建时间"
  )
  private Date createTime;

  @TableField(
      value = "del_flag",
      desc = "逻辑删除标记（0：显示；1：隐藏）"
  )
  private String delFlag;

  @TableField(
      value = "group_id",
      desc = "用户组"
  )
  private String groupId;

  @TableField(
      value = "headimg_url",
      desc = "头像"
  )
  private String headimgUrl;

  @TableField(
      value = "language",
      desc = "用户语言"
  )
  private String language;

  @TableField(
      value = "latitude",
      desc = "地理位置纬度"
  )
  private Double latitude;

  @TableField(
      value = "longitude",
      desc = "地理位置经度"
  )
  private Double longitude;

  @TableField(
      value = "nick_name",
      desc = "昵称"
  )
  private String nickName;

  @TableField(
      value = "open_id",
      desc = "用户标识"
  )
  private String openId;

  @TableField(
      value = "phone",
      desc = "手机号码"
  )
  private String phone;

  @TableField(
      value = "precision",
      desc = "地理位置精度"
  )
  private Double precision;

  @TableField(
      value = "province",
      desc = "所在省份"
  )
  private String province;

  @TableField(
      value = "qr_scene_str",
      desc = "二维码扫码场景"
  )
  private String qrSceneStr;

  @TableField(
      value = "remark",
      desc = "用户备注"
  )
  private String remark;

  @TableField(
      value = "session_key",
      desc = "会话密钥"
  )
  private String sessionKey;

  @TableField(
      value = "sex",
      desc = "性别（1：男，2：女，0：未知）"
  )
  private String sex;

  @TableField(
      value = "subscribe",
      desc = "是否订阅（1：是；0：否；2：网页授权用户）"
  )
  private String subscribe;

  @TableField(
      value = "subscribe_num",
      desc = "关注次数"
  )
  private Integer subscribeNum;

  @TableField(
      value = "subscribe_scene",
      desc = "返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENEPROFILE LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_OTHERS 其他"
  )
  private String subscribeScene;

  @TableField(
      value = "subscribe_time",
      desc = "关注时间"
  )
  private Date subscribeTime;

  @TableField(
      value = "tagid_list",
      desc = "标签列表"
  )
  private String tagidList;

  @TableField(
      value = "union_id",
      desc = "union_id"
  )
  private String unionId;

  @TableField(
      value = "update_id",
      desc = "更新者"
  )
  private String updateId;

  @TableField(
      value = "update_time",
      desc = "更新时间"
  )
  private Date updateTime;

  @Override
  public final Class entityClass() {
    return WxUserEntity.class;
  }
}
