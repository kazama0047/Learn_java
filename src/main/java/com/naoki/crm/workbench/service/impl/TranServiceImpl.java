package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.utils.UUIDUtil;
import com.naoki.crm.workbench.dao.CustomerDao;
import com.naoki.crm.workbench.dao.TranDao;
import com.naoki.crm.workbench.dao.TranHistoryDao;
import com.naoki.crm.workbench.domain.Customer;
import com.naoki.crm.workbench.domain.Tran;
import com.naoki.crm.workbench.domain.TranHistory;
import com.naoki.crm.workbench.service.TranService;

/**
 * @author Kazama
 * @create 2021-03-12-19:40
 */
public class TranServiceImpl implements TranService {
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag=true;
        // 精确查找该customerName,如果有,则将id写入tran对象的customerId属性
        // 如果没有,则创建客户信息,获取id,封装到tran对象中
        Customer cus=customerDao.getCustomerByName(customerName);
        if(cus==null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(t.getCreateTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());
            int count1=customerDao.save(cus);
            if(count1!=1){
                flag=false;
            }
        }
        //tran对象信息全了之后,执行添加交易操作
        t.setCustomerId(cus.getId());
        int count2=tranDao.save(t);
        if(count2!=1){
            flag=false;
        }
        //添加交易后,创建一条交易记录
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(t.getCreateTime());
        th.setCreateBy(t.getCreateBy());
        int count3=tranHistoryDao.save(th);
        if(count3!=1){
            flag=false;
        }
        return flag;
    }
}
