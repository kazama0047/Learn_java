package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.workbench.dao.CustomerDao;
import com.naoki.crm.workbench.service.CustomerService;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-12-22:39
 */
public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao= SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerName(name);
    }
}
