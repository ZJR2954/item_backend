package com.item_backend.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @Description: 将一些不方便加@Component注解的类放在此处 加入spring容器
 * @Author: Mt.Li
*/

@Component
public class BeanConfig {

    /**
     * spring-boot内置的json工具
     *
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {

        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


}
