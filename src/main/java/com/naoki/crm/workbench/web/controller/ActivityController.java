package com.naoki.crm.workbench.web.controller;

import com.naoki.crm.exception.ActivityException;
import com.naoki.crm.settings.domain.User;
import com.naoki.crm.settings.service.UserService;
import com.naoki.crm.settings.service.impl.UserServiceImpl;
import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.PrintJson;
import com.naoki.crm.utils.ServiceFactory;
import com.naoki.crm.utils.UUIDUtil;
import com.naoki.crm.vo.PageinationVO;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.ActivityRemark;
import com.naoki.crm.workbench.service.ActivityService;
import com.naoki.crm.workbench.service.impl.ActivityServiceImpl;

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
 * @create 2021-03-05-16:44
 */
public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("activity controller");
        String path = request.getServletPath();
        if ("/workbench/activity/getUserList.do".equals(path)) {
            getUserList(request, response);
        } else if ("/workbench/activity/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/activity/pageList.do".equals(path)) {
            pageList(request, response);
        } else if ("/workbench/activity/delete.do".equals(path)) {
            delete(request, response);
        } else if ("/workbench/activity/getUserListAndActivity.do".equals(path)) {
            getUserListAndActivity(request, response);
        } else if ("/workbench/activity/update.do".equals(path)) {
            update(request, response);
        } else if ("/workbench/activity/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/activity/getRemarkListByAid.do".equals(path)) {
            getRemarkListByAid(request, response);
        } else if ("/workbench/activity/deleteRemark.do".equals(path)) {
            deleteRemark(request, response);
        } else if ("/workbench/activity/saveRemark.do".equals(path)) {
            saveRemark(request, response);
        } else if ("/workbench/activity/updateRemark.do".equals(path)) {
            updateRemark(request, response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editFlag = "1";
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setEditFlag(editFlag);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.updateRemark(ar);
        if (flag) {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("ar",ar);
            map.put("success",true);
            // 让前端 获取 修改时间 和 修改用户
            PrintJson.printJsonObj(response, map);
        } else {
            PrintJson.printJsonFlag(response, flag);
        }
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setActivityId(activityId);
        ar.setNoteContent(noteContent);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.saveRemark(ar);
        if (flag) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", flag);
            map.put("ar", ar);
            PrintJson.printJsonObj(response, map);
        } else {
            PrintJson.printJsonFlag(response, flag);
        }
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.deleteRemark(id);
        PrintJson.printJsonFlag(response, flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("activityId");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        // 返回 备注list集合
        List<ActivityRemark> list = service.getRemarkListByAid(id);
        PrintJson.printJsonObj(response, list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = service.detail(id);
        // 将获取的activity对象,写入request作用域
        request.setAttribute("a", a);
        // 使用请求转发,传递request作用域数据
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动修改");
        // 读取表单信息,以及生成对应的字段值
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        // 创建 ActivityService代理类对象
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        // 封装对象
        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        // 修改时间,修改人
        a.setEditTime(editTime);
        a.setEditBy(editBy);
        try {
            // 持久化对象
            service.update(a);
            // 持久化成功 返回的json
            PrintJson.printJsonFlag(response, true);
        } catch (ActivityException e) {
            e.printStackTrace();
            // 持久化失败返回的json
            String msg = e.getMessage();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            map.put("msg", msg);
            PrintJson.printJsonObj(response, map);
        }
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String, Object> map = service.getUserListAndActivity(id);
        PrintJson.printJsonObj(response, map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        // 可能传递多个参数,使用数组接收
        String[] ids = request.getParameterValues("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        // 创建业务方法,返回删除成功与否
        boolean flag = service.delete(ids);
        PrintJson.printJsonFlag(response, flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("分页功能");
        // 搜索表单的值
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        // 分页参数
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        PageinationVO vo = as.pageList(map);
        PrintJson.printJsonObj(response, vo);
    }

    /**
     * 市场活动添加
     **/
    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动添加");
        // 读取表单信息,以及生成对应的字段值
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        // 创建 ActivityService代理类对象
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        // 封装对象
        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);
        try {
            // 持久化对象
            service.save(a);
            // 持久化成功 返回的json
            PrintJson.printJsonFlag(response, true);
        } catch (ActivityException e) {
            e.printStackTrace();
            // 持久化失败返回的json
            String msg = e.getMessage();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            map.put("msg", msg);
            PrintJson.printJsonObj(response, map);
        }
    }

    /**
     * 下拉列表获取用户名
     **/
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取用户列表");
        // 发生在 市场活动模块下 ,但是处理的是 用户业务,所以要调用用户模块内的方法
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        // 创建用户模块dao方法,获取用户列表
        List<User> users = service.getUserList();
        // 使用工具 将list集合,转换为json发送
        PrintJson.printJsonObj(response, users);
    }
}
