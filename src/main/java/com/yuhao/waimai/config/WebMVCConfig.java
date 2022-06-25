package com.yuhao.waimai.config;

import com.yuhao.waimai.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/***
 *  改变SpringBoot默认存放静态资源的位置   设置静态资源映射
 */

@Configuration
public class WebMVCConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        registry.addResourceHandler("/welcome/**").addResourceLocations("classpath:/welcome/");
    }

    /**
     * 自己拓展新的webmvc框架的消息转换器*/
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器,底层使用Jackson将java对象转换成JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());//com.yuhao.waimai.common.JacksonObjectMapper
        //将上面的转换器对象添加到MVC框架中的转换器集合中
        //给一个下标 优先使用
        converters.add(0,messageConverter);
    }

}
