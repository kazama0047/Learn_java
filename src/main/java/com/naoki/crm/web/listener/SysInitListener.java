package com.naoki.crm.web.listener;

import com.naoki.crm.settings.domain.DicValue;
import com.naoki.crm.settings.service.DicService;
import com.naoki.crm.settings.service.impl.DicServiceImpl;
import com.naoki.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kazama
 * @create 2021-03-09-17:23
 */
public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("数据字典监听器");
        // 全局作用域对象
        ServletContext application = event.getServletContext();
        // 调用 数据字典业务
        DicService service= (DicService) ServiceFactory.getService(new DicServiceImpl());
        // 数据字典集合
        Map<String, List<DicValue>> map=service.getAll();
        // 遍历集合,将 字典类型+list后缀 作为属性名 ,关联值的集合作为值保存到application
        Set<String> set=map.keySet();
        for(String key :set){
            application.setAttribute(key, map.get(key));
        }
    }
}
