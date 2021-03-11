package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.exception.ActivityException;
import com.naoki.crm.settings.dao.UserDao;
import com.naoki.crm.settings.domain.User;
import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.vo.PageinationVO;
import com.naoki.crm.workbench.dao.ActivityDao;
import com.naoki.crm.workbench.dao.ActivityRemarkDao;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.ActivityRemark;
import com.naoki.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-05-16:42
 */
public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public void save(Activity a) throws ActivityException {
        if (a.getStartDate().compareTo(a.getEndDate()) > 0) {
            throw new ActivityException("活动开始时间不能晚于结束时间");
        }
        int i = activityDao.save(a);
        if (i != 1) {
            throw new ActivityException("活动添加失败");
        }
    }

    @Override
    public PageinationVO pageList(Map<String, Object> map) {
        int total = activityDao.getTotalByCondition(map);
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        PageinationVO<Activity> vo = new PageinationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        // 查询需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        // 删除备注,返回受到影响的记录数
        int count2 = activityRemarkDao.deleteByAids(ids);
        if (count1 != count2) {
            flag = false;
        }
        // 事件删除的活动记录数与输入的活动id数
        int count3 = activityDao.delete(ids);
        if (count3 != ids.length) {
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        // 获取用户list
        List<User> userList = userDao.getUserList();
        // 获取activity对象
        Activity activity = activityDao.getById(id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", userList);
        map.put("a", activity);
        return map;
    }

    @Override
    public void update(Activity a) throws ActivityException {
        if (a.getStartDate().compareTo(a.getEndDate()) > 0) {
            throw new ActivityException("活动开始时间不能晚于结束时间");
        }
        int i = activityDao.update(a);
        if (i != 1) {
            throw new ActivityException("活动修改失败");
        }
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        List<ActivityRemark> list = activityRemarkDao.getRemarkListByAid(id);
        return list;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteById(id);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        return activityDao.getActivityListByClueId(clueId);
    }

    @Override
    public List<Activity> getActivityByNameAndNotByClueId(Map<String, String> map) {
        return activityDao.getActivityListByNameAndNotByClueId(map);
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        return activityDao.getActivityListByName(aname);
    }
}
