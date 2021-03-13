package com.naoki.crm.workbench.web.controller;

import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.PrintJson;
import com.naoki.crm.utils.ServiceFactory;
import com.naoki.crm.utils.UUIDUtil;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.Contacts;
import com.naoki.crm.workbench.domain.Tran;
import com.naoki.crm.workbench.domain.TranHistory;
import com.naoki.crm.workbench.service.ActivityService;
import com.naoki.crm.workbench.service.ContactsService;
import com.naoki.crm.workbench.service.CustomerService;
import com.naoki.crm.workbench.service.TranService;
import com.naoki.crm.workbench.service.impl.ActivityServiceImpl;
import com.naoki.crm.workbench.service.impl.ContactsServiceImpl;
import com.naoki.crm.workbench.service.impl.CustomerServiceImpl;
import com.naoki.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-12-19:42
 */
public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request, response);
        } else if ("/workbench/transaction/searchActivityByName.do".equals(path)) {
            searchActivityByName(request, response);
        } else if ("/workbench/transaction/searchContactsListByName.do".equals(path)) {
            searchContactsListByName(request, response);
        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request, response);
        } else if ("/workbench/transaction/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/transaction/getHistoryByTranId.do".equals(path)) {
            getHistoryByTranId(request, response);
        }
    }

    private void getHistoryByTranId(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TranService service = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> list = service.getHistoryListByTranId(tranId);
        // 阶段与可能性
        Map<String,String> pMap= (Map<String, String>) this.getServletContext().getAttribute("pMap");
        for(TranHistory th:list){
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            // 新建属性
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response, list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TranService service = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = service.detail(id);
        String stage = t.getStage();
        // 获取保存在全局作用域中的 阶段和可能性值的映射关系
        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        // 获取交易中的阶段对应的可能性值
        String possibility = pMap.get(stage);
        // tran对象中创建possiblity属性,写入后一起传递
        t.setPossibility(possibility);
        request.setAttribute("t", t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t, customerName);
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        CustomerService service = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> list = service.getCustomerName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void searchContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ContactsService service = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        List<Contacts> list = service.getContactsListByName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void searchActivityByName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list = service.getActivityListByName(name);
        PrintJson.printJsonObj(response, list);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = service.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }
}
