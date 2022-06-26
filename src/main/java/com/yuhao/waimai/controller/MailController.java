package com.yuhao.waimai.controller;

import com.yuhao.waimai.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MailController {
    @Resource
    MailService mailService;

    @GetMapping("/sendMail")
    public void sendSimpleMail() {
        mailService.sendSimpleMail("yuhao_work1@163.com",
                "1349502609@qq.com",
                "您的验证码",
                "[俞浩的外卖项目登录],您的验证码为 0000");
    }
}
