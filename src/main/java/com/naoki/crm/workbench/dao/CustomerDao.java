package com.naoki.crm.workbench.dao;

import com.naoki.crm.workbench.domain.Customer;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer cus);
}
