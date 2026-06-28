package com.xueyan.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.xueyan.order", "com.xueyan.common"})
public class XueyanOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(XueyanOrderApplication.class, args);
    }
}
