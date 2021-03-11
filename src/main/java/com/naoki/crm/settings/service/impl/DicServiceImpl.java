package com.naoki.crm.settings.service.impl;

import com.naoki.crm.settings.dao.DicTypeDao;
import com.naoki.crm.settings.dao.DicValueDao;
import com.naoki.crm.settings.domain.DicType;
import com.naoki.crm.settings.domain.DicValue;
import com.naoki.crm.settings.service.DicService;
import com.naoki.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kazama
 * @create 2021-03-09-16:37
 */
public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        // 通过键名,访问 同一字典类型下的数据
        Map<String,List<DicValue>> map =new HashMap<String,List<DicValue>>();
        // 获取 字典类型集合
        List<DicType> dtList = dicTypeDao.getTypeList();
        // 遍历字典类型,获取关联的字典类型值的集合
        for (DicType dt : dtList) {
            String code = dt.getCode();
            List<DicValue> dvList = dicValueDao.getListByCode(code);
            // 将 字典类型 +list后缀 作为键,相关联的字典值集合作为值
            map.put(code+"List",dvList);
        }
        return map;
    }
}
