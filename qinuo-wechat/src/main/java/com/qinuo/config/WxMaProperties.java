package com.qinuo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wechat.ma")
public class WxMaProperties {

    /**
     * 小程序配置信息
     */
    private List<Config> configs;

    /**
     * 支付回调域名
     */
    private String notifyHost;

    /**
     * 支付回调服务器IP
     */
    private String spbillCreateIp;

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appId;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
        /**
         * 微信支付商户号
         */
        private String mchId;
        /**
         * 微信支付商户密钥
         */
        private String mchKey;
        /**
         * p12证书的位置，可以指定绝对路径，也可以指定类路径（以classpath:开头）
         */
        private String keyPath;
    }

}