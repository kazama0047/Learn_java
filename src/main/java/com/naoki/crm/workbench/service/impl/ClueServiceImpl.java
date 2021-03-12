package com.naoki.crm.workbench.service.impl;

import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.SqlSessionUtil;
import com.naoki.crm.utils.UUIDUtil;
import com.naoki.crm.workbench.dao.*;
import com.naoki.crm.workbench.domain.*;
import com.naoki.crm.workbench.service.ClueService;

import java.util.List;

/**
 * @author Kazama
 * @create 2021-03-09-16:23
 */
public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao= SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao=SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    private ContactsDao contactsDao=SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    private TranDao tranDao=SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue clue) {
        boolean flag=true;
        int count=clueDao.save(clue);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c=clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag=true;
        int count=clueActivityRelationDao.unbund(id);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean bund(String cid, String[] aids) {
        boolean flag=true;
        for(String aid:aids){
            ClueActivityRelation car=new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(cid);
            car.setActivityId(aid);
            int count=clueActivityRelationDao.bund(car);
            if(count!=1){
                flag=false;
            }
        }
        return flag;
    }

    @Override
    // 线索id,交易信息,创建人(不建议将request引入)
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag=true;
        String createTime= DateTimeUtil.getSysTime();
        Clue c=clueDao.getById(clueId);
        String company=c.getCompany();
        // 查询该潜在客户的公司是否已经存在
        Customer cus=customerDao.getCustomerByName(company);
        if(cus==null){
            // 公司客户不存在,创建,将线索中公司相关的信息进行迁移
            cus=new Customer();
            cus.setId(UUIDUtil.getUUID());
            // 公司地址
            cus.setAddress(c.getAddress());
            // 公司网站
            cus.setWebsite(c.getWebsite());
            // 公司座机
            cus.setPhone(c.getPhone());
            // 拥有者
            cus.setOwner(c.getOwner());
            // 下次联系时间
            cus.setNextContactTime(c.getNextContactTime());
            // 公司名称
            cus.setName(company);
            // 描述信息
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            int count1=customerDao.save(cus);
            if(count1!=1){
                flag=false;
            }
        }
        // 创建联系人信息,将线索的信息迁移至联系人
        Contacts con=new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateBy(createBy);
        con.setCreateTime(createTime);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
        int count2=contactsDao.save(con);
        if(count2!=1){
            flag=false;
        }
        // 将线索内的备注信息,迁移至顾客备注和联系人备注中
        List<ClueRemark> clueRemarkList=clueRemarkDao.getListByClueId(clueId);
        for(ClueRemark clueRemark:clueRemarkList){
            String noteContent = clueRemark.getNoteContent();
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3=customerRemarkDao.save(customerRemark);
            if(count3!=1){
                flag=false;
            }
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4=contactsRemarkDao.save(contactsRemark);
            if(count4!=1){
                flag=false;
            }
        }
        //与线索关联的活动,写入 联系人活动关联表中,联系人在上面已经创建
        List<ClueActivityRelation> clueActivityRelationList=clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            String activityId=clueActivityRelation.getActivityId();
            ContactsActivityRelation contactsActivityRelation=new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());
            int count5=contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                flag=false;
            }
        }
        // 如果转换时提交了交易表单,将交易表需要的其他信息写入
        if(t!=null){
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
            int count6=tranDao.save(t);
            if(count6!=1){
                flag=false;
            }
            // 创建交易历史记录
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            int count7=tranHistoryDao.save(th);
            if(count7!=1){
                flag=false;
            }
        }
        // 删除线索备注
        for(ClueRemark clueRemark:clueRemarkList){
            int count8=clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                flag=false;
            }
        }
        // 删除线索活动关联
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            int count9=clueActivityRelationDao.delete(clueActivityRelation);
            if(count9!=1){
                flag=false;
            }
        }
        // 删除线索
        int count10=clueDao.delete(clueId);
        if(count10!=1){
            flag=false;
        }
        return flag;
    }
}
