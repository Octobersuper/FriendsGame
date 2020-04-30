package com.zcf.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@MapperScan(value="com.zcf.framework.mapper")
@ServletComponentScan
@SpringBootApplication
public class FrameApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FrameApplication.class, args);
        System.out.print("启动成功");
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
