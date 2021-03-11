package com.naoki.crm.settings.service;

import com.naoki.crm.exception.LoginException;
import com.naoki.crm.settings.domain.User;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-04-22:58
 */
public interface UserService {
    /**登录验证**/
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    /**用户列表**/
    List<User> getUserList();
}
