package com.naoki;

import com.naoki.crm.settings.domain.DicValue;
import com.naoki.crm.settings.service.DicService;
import com.naoki.crm.settings.service.impl.DicServiceImpl;
import com.naoki.crm.utils.DateTimeUtil;
import com.naoki.crm.utils.ServiceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kazama
 * @create 2021-03-04-23:37
 */
public class test {
    @Test
    public void time(){
        System.out.println("2021-3-10".compareTo(DateTimeUtil.getSysTime()));
    }
    @Test
    public void demo(){
        DicService service= (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> all = service.getAll();
        Set<String> strings = all.keySet();
        for(String str:strings){
            System.out.println(str);
        }
    }
}


