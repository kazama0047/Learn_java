package com.naoki.crm.exception;

/**
 * @author Kazama
 * @create 2021-03-04-23:31
 */
//登录错误异常
public class LoginException extends Exception{
    public LoginException(String message) {
        super(message);
    }
}
