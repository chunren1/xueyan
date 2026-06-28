package com.xueyan.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.xueyan.user", "com.xueyan.common"})
public class XueyanUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(XueyanUserApplication.class, args);
    }
}
