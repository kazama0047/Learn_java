package com.naoki.crm.settings.service;

import com.naoki.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-09-16:37
 */
public interface DicService {
    Map<String, List<DicValue>> getAll();
}
