package com.naoki.crm.settings.service.impl;

import com.naoki.crm.exception.LoginException;
import com.naoki.crm.settings.dao.UserDao;
import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-04-22:58
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        // 使用map,对dao进行多参数传递
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        // 向数据库传递 账号密码,获得用户对象
        User user = userDao.login(map);
        if (user == null) {
            throw new LoginException("账号密码错误");
        }
        // 失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        // 当前时间 > 失效时间 return >0
        if (currentTime.compareTo(expireTime) > 0) {
            throw new LoginException("账号已失效");
        }
        // 账号是否被锁定
        String lockState = user.getLockState();
        if ("0".equals(lockState)) {
            throw new LoginException("账号已锁定");
        }
        // 验证ip
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)) {
            throw new LoginException("ip地址受限");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        return userDao.getUserList();
    }
}
