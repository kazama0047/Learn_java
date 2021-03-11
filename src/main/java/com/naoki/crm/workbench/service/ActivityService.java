package com.naoki.crm.workbench.service;

import com.naoki.crm.exception.ActivityException;
import com.naoki.crm.vo.PageinationVO;
import com.naoki.crm.workbench.domain.Activity;
import com.naoki.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-05-16:42
 */
public interface ActivityService {
    void save(Activity a) throws ActivityException;

    PageinationVO pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    void update(Activity a) throws ActivityException;

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String id);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityByNameAndNotByClueId(Map<String, String> map);
}
