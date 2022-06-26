package com.yuhao.waimai.service;


public interface MailService {
    void sendSimpleMail(String from, String to, String subject, String text);
}
