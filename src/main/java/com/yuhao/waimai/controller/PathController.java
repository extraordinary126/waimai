package com.yuhao.waimai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PathController {

    @RequestMapping("/")
    public void test(HttpServletResponse response) throws IOException {
        response.sendRedirect("/welcome/welcome.html");
    }
}
