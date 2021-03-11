package com.naoki.crm.workbench.service;

import com.naoki.crm.workbench.domain.Clue;

/**
 * @author Kazama
 * @create 2021-03-09-16:23
 */
public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);
}
