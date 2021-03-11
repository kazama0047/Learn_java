package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.workbench.dao.ClueActivityRelationDao;
import com.naoki.crm.workbench.dao.ClueDao;
import com.naoki.crm.workbench.domain.Clue;
import com.naoki.crm.workbench.service.ClueService;

/**
 * @author Kazama
 * @create 2021-03-09-16:23
 */
public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao= SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    @Override
    public boolean save(Clue clue) {
        boolean flag=true;
        int count=clueDao.save(clue);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c=clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag=true;
        int count=clueActivityRelationDao.unbund(id);
        if(count!=1){
            flag=false;
        }
        return flag;
    }
}
