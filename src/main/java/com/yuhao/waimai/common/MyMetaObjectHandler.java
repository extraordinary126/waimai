package com.yuhao.waimai.common;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;




import java.time.LocalDateTime;

//元数据处理器
//在这里给insert update的操作的 createTime createUser之类的重复字段赋值
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Insert操作的时候 这些公共字段自动填充");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        // 调用自己写的BaseContext工具类 获取LocalThread 中存的id  因为同一个线程 所以可以存
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update操作 公共字段自动填充");
        long id = Thread.currentThread().getId();
        log.info("元数据处理器MyMetaObjectHandler 线程id{}",id );
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
