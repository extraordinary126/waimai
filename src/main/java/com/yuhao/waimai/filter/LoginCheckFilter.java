package com.yuhao.waimai.filter;


import com.alibaba.fastjson.JSON;
import com.yuhao.waimai.bean.Employee;
import com.yuhao.waimai.common.BaseContext;
import com.yuhao.waimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**过滤器 检查用户是否登录
 *
 * 1.获取本次拦截的URI
 * 2.判断本次请求是否需要处理
 * 3.不需要处理 直接放行
 * 4.判断登录状态 已登录 放行
 * 5.判断登录状态 未登录 返回登录
 * */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*  {} 相当于占位符 */
        /*log.info("要拦截的请求 {}", request.getRequestURI());*/

        /*1.获取本次拦截的URI*/
        String requestURI = request.getRequestURI();

        /*不拦截的数组*/
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/",
                "/user/sendMsg",//发送短信
                "/user/login",   //移动端登录
                "/welcome/welcome.html"
        };
        boolean matcherValue = loginPathMatcher(urls, requestURI);
        // 路径在数组里 不需要处理 直接放行
        if (matcherValue) {
            filterChain.doFilter(request, response);
            return;
        }
        //判断登录状态 (网页端 后台用户)用户已经登录 放行
        if (request.getSession().getAttribute("employee") != null){
            Long id = Thread.currentThread().getId();
            //log.info("线程id为{}",id);
            log.info("用户id为{}",id);
            Long empId = (Long) request.getSession().getAttribute("employee");
            //调用自己写的BaseContext工具类的方法 将用户id存入ThreadLocal中 因为是一个线程 所以可以存取到
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态 (移动端 消费者用户)
        if (request.getSession().getAttribute("user") != null){
            Long id = Thread.currentThread().getId();
            log.info("用户id为{}",id);
            //log.info("线程id为{}",id);
            Long userId = (Long) request.getSession().getAttribute("user");
            //调用自己写的BaseContext工具类的方法 将用户id存入ThreadLocal中 因为是一个线程 所以可以存取到
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("路径已经拦截{}",requestURI);
        // 未登录  通过输出流的方式向客户端返回页面 让前端去处理页面 将R对象转换成JSON字符串返回给前端
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    //判断当前路径是否在不拦截的数组里
    public boolean loginPathMatcher(String[] urls, String requestURI){
        for (String url : urls) {
            boolean value = PATH_MATCHER.match(url, requestURI);
            if (value){
                return true;
            }
        }
        return false;
    }

}
