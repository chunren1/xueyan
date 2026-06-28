package com.xueyan.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 注意：Gateway 基于 WebFlux，不使用 spring-boot-starter-web
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.xueyan.gateway", "com.xueyan.common"})
public class XueyanGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(XueyanGatewayApplication.class, args);
    }
}
