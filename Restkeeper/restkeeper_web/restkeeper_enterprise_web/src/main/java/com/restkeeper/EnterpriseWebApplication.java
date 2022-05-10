package com.restkeeper;

import org.aspectj.weaver.ast.Var;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class},scanBasePackages={"com.restkeeper"})
public class EnterpriseWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EnterpriseWebApplication.class, args);
        applicationContext.getBean("aaaa");
    }

}
