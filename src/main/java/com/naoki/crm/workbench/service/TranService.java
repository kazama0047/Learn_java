package com.naoki.crm.workbench.service;

import com.naoki.crm.workbench.domain.Tran;

/**
 * @author Kazama
 * @create 2021-03-12-19:40
 */
public interface TranService {
    boolean save(Tran t, String customerName);
}
