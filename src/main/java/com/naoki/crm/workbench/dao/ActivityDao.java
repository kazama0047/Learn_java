package com.naoki.crm.workbench.dao;

import com.naoki.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-05-16:41
 */
public interface ActivityDao {
    int save(Activity a);

    /**根据创建活动的时间排序 降序**/
    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getById(String id);

    int update(Activity a);

    Activity detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);
}
