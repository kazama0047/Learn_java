package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.workbench.dao.TranDao;
import com.naoki.crm.workbench.dao.TranHistoryDao;
import com.naoki.crm.workbench.service.TranService;

/**
 * @author Kazama
 * @create 2021-03-12-19:40
 */
public class TranServiceImpl implements TranService {
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

}
