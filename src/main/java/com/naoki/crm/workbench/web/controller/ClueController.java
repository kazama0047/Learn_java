package com.naoki.crm.workbench.web.controller;

import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.PrintJson;
import com.naoki.crm.utils.ServiceFactory;
import com.naoki.crm.utils.UUIDUtil;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.Clue;
import com.naoki.crm.workbench.service.ActivityService;
import com.naoki.crm.workbench.service.ClueService;
import com.naoki.crm.workbench.service.impl.ActivityServiceImpl;
import com.naoki.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-09-16:24
 */
public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/clue/getUserList.do".equals(path)) {
            getUserList(request, response);
        } else if ("/workbench/clue/save.do".equals(path)) {
            save(request, response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/getActivityList.do".equals(path)){
            getActivityList(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);
        }else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("新增关联市场活动");
        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");
        ClueService service= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.bund(cid,aids);
        PrintJson.printJsonFlag(response, flag);
    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表(模糊查询+排除已关联)");
        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");
        Map<String,String> map=new HashMap<String,String>();
        map.put("aname",aname);
        map.put("clueId",clueId);
        ActivityService service= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list=service.getActivityByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response, list);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ClueService service= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.unbund(id);
        PrintJson.printJsonFlag(response, flag);
    }

    /**根据 clue的id 查询市场活动**/
    private void getActivityList(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        ActivityService service= (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list=service.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response, list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ClueService service= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue=service.detail(id);
        request.setAttribute("c", clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        String id= UUIDUtil.getUUID();
        String fullname=request.getParameter("fullname");
        String appellation=request.getParameter("appellation");
        String owner=request.getParameter("owner");
        String company=request.getParameter("company");
        String job=request.getParameter("job");
        String email=request.getParameter("email");
        String phone=request.getParameter("phone");
        String website=request.getParameter("website");
        String mphone=request.getParameter("mphone");
        String state=request.getParameter("state");
        String source=request.getParameter("source");
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        String createTime= DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String address=request.getParameter("address");
        Clue clue = new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);
        ClueService service= (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag=service.save(clue);
        PrintJson.printJsonFlag(response, flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = service.getUserList();
        PrintJson.printJsonObj(response, userList);
    }
}
