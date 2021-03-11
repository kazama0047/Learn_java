package com.naoki.crm.settings.dao;

import com.naoki.crm.settings.domain.DicValue;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-09-16:34
 */
public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
