package com.xuegong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
    exclude = {
        dev.langchain4j.openai.spring.AutoConfig.class
    }
)
@EntityScan("com.xuegong.entity")
@MapperScan("com.xuegong.mapper")
@EnableCaching
public class XuegongApplication {
    public static void main(String[] args) {
        SpringApplication.run(XuegongApplication.class, args);
    }
}
