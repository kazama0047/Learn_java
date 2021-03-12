package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.workbench.dao.ContactsDao;
import com.naoki.crm.workbench.domain.Contacts;
import com.naoki.crm.workbench.service.ContactsService;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-12-21:47
 */
public class ContactsServiceImpl implements ContactsService {
    private ContactsDao contactsDao= SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    @Override
    public List<Contacts> getContactsListByName(String name) {
        return contactsDao.getContactsListByName(name);
    }
}
