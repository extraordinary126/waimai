package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.waimai.bean.Employee;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    //@RequestBody : 把请求体中的数据，读取出来， 转为java对象使用
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 1.将页面提交的密码password进行md5加密处理
         * 2.根据页面提交的用户名username查询数据库
         * 3.如果没有查询到则返回登录失败结果
         * 4.密码比对,如果不一致则返回登录失败结果
         * 5.查看员工状态,如果为已禁用状态,返回员工已禁用结果
         * 6.登录成功,将员工id存入Session并返回登录成功结果*/

        //1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 我们此时调用的是他的eq方法，如图可知，eq即相当于等于，
        // 在此处用于判断在数据库中遍历出的用户名是否和前端页面用户输入的用户名相同，相同的话即代表数据库中有这个值，反之则无
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);
        //3.如果没有查询到则返回登录失败结果
        if (emp == null){
            return R.error("登录失败 没有查到该用户");
        }
        //4.密码比对,如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败 密码不一致");
        }
        //5.查看员工状态,如果为已禁用状态,返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6.登录成功,将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("成功退出");
    }

    @PostMapping
                //前端将数据封装成employee对象打包成json数组
                //@RequestBody主要用来接收前端传递给后端的json字符串中的数据的(请求体中的数据的)
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始密码123456 进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间

        //获取当前登录用户的ID************************
        Long id = (Long) request.getSession().getAttribute("employee");
        /*employee.setCreateUser(id);
        employee.setUpdateUser(id);*/
        employeeService.save(employee);
        return R.success("插入成功! 用户名:" + employee.getUsername());
    }

    @GetMapping("/page")
    public R<Page> pageQuery(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件        like
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        //添加排序条件 根据更新时间排序   order by
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //构造查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    //员工的通用更新
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long id = (Long) request.getSession().getAttribute("employee");
        long threadId = Thread.currentThread().getId();
        log.info("Controller线程id{}",threadId);
        employeeService.updateById(employee);
        return R.success("员工信息更新成功!");
    }

    //请求网址: http://localhost:8083/employee/1538339406966968322
    //编辑页面 根据ID查询到用户信息显示
    //传一个对象 自动通过对象转换器转换成JSON给前端
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
