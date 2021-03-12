package com.naoki.crm.workbench.web.controller;

import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-12-19:42
 */
public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path=request.getServletPath();
        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService service= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList=service.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }
}
