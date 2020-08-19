package com.gupaoedu.vip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author : lipu
 * @since : 2020-08-19 22:51
 */
@Configuration
@ComponentScan("com.gupaoedu.vip")
public class SpringConfig {

    @Bean(name = "gpRpcServer")
    public GpRpcServer gpRpcServer(){
        return new GpRpcServer(8080);
    }

}
