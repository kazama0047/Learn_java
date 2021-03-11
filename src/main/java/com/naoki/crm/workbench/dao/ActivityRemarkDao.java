package com.naoki.crm.workbench.dao;

import com.naoki.crm.workbench.domain.ActivityRemark;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-05-17:54
 */
public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String id);

    int deleteById(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
