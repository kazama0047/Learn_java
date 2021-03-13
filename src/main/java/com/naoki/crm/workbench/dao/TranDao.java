package com.naoki.crm.workbench.dao;

import com.naoki.crm.workbench.domain.Tran;

public interface TranDao {

    int save(Tran t);

    Tran detail(String id);

    int changeStage(Tran t);
}
