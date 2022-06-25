package com.yuhao.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.waimai.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
