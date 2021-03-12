package com.naoki.crm.workbench.service;

import com.naoki.crm.workbench.domain.Contacts;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-12-21:47
 */
public interface ContactsService {
    List<Contacts> getContactsListByName(String name);
}
