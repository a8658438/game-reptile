package com.tc.reptile.constant;

/**
 * @author loocao
 * @date 2019-02-16
 */
public class ApiCode {
    /**
     * 成功
     */
    public static final String SUCCESS = "200";
    /**
     * 客户端错误
     */
    public static final String BAD_REQUEST = "400";
    /**
     * 验证码不正确
     */
    public static final String BAD_REQUEST_CAPTCHA = "40001";

    /**
     * 账号未审核
     */
    public static final String ACCOUNT_UN_VERIFY = "40002";

    /**
     * 账号审核已拒绝
     */
    public static final String ACCOUNT_REJECTED = "40003";

    /**
     * 未认证
     */
    public static final String UNAUTHORIZED = "401";
    /**
     * 拒绝执行
     */
    public static final String FORBIDDEN = "403";
    /**
     * Not Found
     */
    public static final String NOT_FOUND = "404";
    /**
     * 冲突验证
     */
    public static final String CONFLICT = "409";
    /**
     * 服务器错误
     */
    public static final String INTERNAL_SERVER_ERROR = "500";
}
