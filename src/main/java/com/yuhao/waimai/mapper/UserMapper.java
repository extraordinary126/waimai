package com.yuhao.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.waimai.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
