package com.yuhao.waimai.common;


/** 基于ThreadLocal的工具类  用于保存和获取当前线程里的用户ID
 *
 * 什么是ThreadLocal?
 * ThreadLocal并不是一个Thread，而是Thread的局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每个使用该
 * 变量的线程提供独立的变量副本，所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
 * ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不
 * 能访问。
 *
 *
 * 我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id，并调用ThreadLocal的set方法来设置当前线程的线
 * 程局部变量的值（用户id），然后在MyMetaObjectHandler的updateFill方法中调用ThreadLocal的get方法来获得当前
 * 线程所对应的线程局部变量的值（用户id）
 *
 *  以下方法属于同一线程 获得的线程id相同
 * LoginCheckFilter的doFilter方法  EmployeeController的update方法  MyMetaObjectHandler的updateFill方法
 *
 * */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}