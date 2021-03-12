package com.utisl;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Kazama
 * @create 2021-03-13-1:36
 */
public class util {
}
class SqlSessionUtil{
    private SqlSessionUtil(){}
    private static SqlSessionFactory factory;
    private static ThreadLocal<SqlSession> t=new ThreadLocal<SqlSession>();
    static{
        String config="mybatis.xml";
        InputStream in=null;
        try{
            in= Resources.getResourceAsStream(config);
        }catch(Exception e){
            e.printStackTrace();
        }
        factory= new SqlSessionFactoryBuilder().build(in);
    }
    public static SqlSession getSqlSession(){
        SqlSession session = t.get();
        if(session==null){
            session=factory.openSession();
            t.set(session);
        }
        return session;
    }
    public static void myClose(SqlSession session){
        if(session!=null){
            session.close();
        }
    }
}
class TransactionInvocationHandler implements InvocationHandler {
    private Object target;
    public TransactionInvocationHandler(Object target){
        this.target=target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session=null;
        Object obj=null;
        try{
            session=SqlSessionUtil.getSqlSession();
            obj=method.invoke(target, args);
            session.commit();
        }catch(Exception e){
            e.printStackTrace();
            session.rollback();
            throw e.getCause();
        }finally{
            SqlSessionUtil.myClose(session);
        }
        return obj;
    }
    public Object getProxy(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }
}
class ServiceFactory{
    public static Object getService(Object service){
        return new TransactionInvocationHandler(service).getProxy();
    }
}
