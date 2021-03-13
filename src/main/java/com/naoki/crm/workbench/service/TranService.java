package com.naoki.crm.workbench.service;

import com.naoki.crm.workbench.domain.Tran;
import com.naoki.crm.workbench.domain.TranHistory;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-12-19:40
 */
public interface TranService {
    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changeStage(Tran t);
}
