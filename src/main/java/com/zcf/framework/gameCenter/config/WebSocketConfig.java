package com.zcf.framework.gameCenter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/3/4
 * Time:9:26
 */
@Configuration
@ConditionalOnWebApplication
public class WebSocketConfig  {

    //使用boot内置tomcat时需要注入此bean
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


}