package com.naoki.crm.workbench.web.controller;

import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.PrintJson;
import com.naoki.crm.utils.ServiceFactory;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.Contacts;
import com.naoki.crm.workbench.service.ActivityService;
import com.naoki.crm.workbench.service.ContactsService;
import com.naoki.crm.workbench.service.CustomerService;
import com.naoki.crm.workbench.service.impl.ActivityServiceImpl;
import com.naoki.crm.workbench.service.impl.ContactsServiceImpl;
import com.naoki.crm.workbench.service.impl.CustomerServiceImpl;

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
        }else if("/workbench/transaction/searchActivityByName.do".equals(path)){
            searchActivityByName(request,response);
        }else if("/workbench/transaction/searchContactsListByName.do".equals(path)){
            searchContactsListByName(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        CustomerService service= (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> list=service.getCustomerName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void searchContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ContactsService service= (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        List<Contacts> list=service.getContactsListByName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void searchActivityByName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ActivityService service= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list = service.getActivityListByName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService service= (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList=service.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }
}
