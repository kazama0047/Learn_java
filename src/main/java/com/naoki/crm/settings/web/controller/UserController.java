package com.naoki.crm.settings.web.controller;

import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.MD5Util;
import com.naoki.crm.utils.PrintJson;
import com.naoki.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-04-23:00
 */
public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("user controller");
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)) {
            login(request, response);
        }
    }

    /**
     * 登录验证
     **/
    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("登录验证");
        // 接收前端参数
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        // 将密码进行加密 匹配 数据库保存的32位密码
        loginPwd = MD5Util.getMD5(loginPwd);
        // 获取访问的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("ip=>" + ip);
        // 生成 service代理类对象
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        try {
            // 创建方法,查询用户
            User user = service.login(loginAct, loginPwd, ip);
            // 将用户信息 写入session
            request.getSession().setAttribute("user", user);
            // 使用工具提供方法,用于仅传递boolean的json数据 {"success":true}
            PrintJson.printJsonFlag(response, true);
        } catch (Exception e) {
            e.printStackTrace();
            // 获取自定义异常提示信息
            String msg = e.getMessage();
            // 生成返回的json数据
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            map.put("msg", msg);
            // 工具类,将map转换为json并通过response输出
            PrintJson.printJsonObj(response, map);
        }
    }
}
