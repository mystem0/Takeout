package com.sias.waimai.config;

import com.sias.waimai.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 项目启动时会加载此类，所以要加@Configuration注解
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
/**
 *         /backend/**   指代的是浏览器访问路径  localhost：8080\backend\**
 *         classpath:/backend/   指代的是本地文件夹 resources\backend\
 */
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 消息转换器（HttpMessageConverter）在 Spring MVC 框架中用于处理 HTTP 请求和响应中的数据转换。具体用途包括：
     * 请求体解析：将 HTTP 请求体中的数据（如 JSON、XML 等）转换为 Java 对象，供控制器方法使用。
     * 响应体生成：将控制器方法返回的 Java 对象转换为 HTTP 响应体中的数据格式（如 JSON、XML 等）。
     * MappingJackson2HttpMessageConverter 可以方便地处理 JSON 数据，
     * Jaxb2RootElementHttpMessageConverter 则用于处理 XML 数据。
     * @param converters 要扩展的已配置转换器列表（默认是MVC预配的转换器）默认列表集合中有8个，当添加拓展转换器并设置优先级为0时，列表集合会变为9个且该拓展转换器位于第1个
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("加载拓展MVC框架的消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中，索引0表示最高优先级，越小优先级越高
        converters.add(0,messageConverter);
    }
}
