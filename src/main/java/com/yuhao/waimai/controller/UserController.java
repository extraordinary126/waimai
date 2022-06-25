package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yuhao.waimai.bean.User;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.service.UserService;
import com.yuhao.waimai.utils.SMSUtils;
import com.yuhao.waimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/sendMsg")    //传过来手机号
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String code = null;
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            //随机生成四位验证码
            code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用短信API发送短信
            //SMSUtils.sendMessage("signName","templateCode",phone,"param");
            log.info("短信验证码是:{}",code);
            //向session中存入验证码 登录时验证用
            //session.setAttribute(phone,code);
            session.setAttribute(phone,"0000");
            return R.success("您的验证码是:" + code);
        }
        return R.error("发送失败,手机号为空");
    }
    @PostMapping("/login")    //传过来手机号和验证码 key:phone,code
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取传过来的验证码
        String code = map.get("code").toString();
        //获取session 中的验证码
        Object codeInSession = session.getAttribute(phone);
        //验证码的比对
        if (codeInSession != null && code.equals(codeInSession)){
            //比对成功 成功登录
            //判断是否为新用户 如果是 则自动注册 偷偷放到数据库里
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            // 为空则说明没有查到手机号对应的用户 则自动注册
            if (user == null){
                User newUser = new User();
                newUser.setPhone(phone);
                userService.save(newUser);
                session.setAttribute("user",newUser.getId());
                return R.success(newUser);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败!");
    }
    /*请求网址: http://localhost:8083/user/loginout
      请求方法: POST      */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功!");
    }
}
