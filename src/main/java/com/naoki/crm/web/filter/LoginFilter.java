package com.naoki.crm.web.filter;

import com.naoki.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Kazama
 * @create 2021-03-05-15:10
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
//        强转,调用子类方法,获取资源路径
        HttpServletRequest request= (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) resp;
        String path=request.getServletPath();
        // 如果是 登录页面 以及 校验登录信息的 控制器,允许访问
        if("/login.jsp".equals(path)||"/settings/user/login.do".equals(path)){
            chain.doFilter(req, resp);
        // 如果访问其他页面,需要先判断用户是否登录过
        }else {
            // 获取session中的user属性
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            // 如果user属性有值,则放行
            if (user != null) {
                chain.doFilter(req, resp);
            } else {
                // 使用重定向,改变用户浏览器路径
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        }
    }
}
