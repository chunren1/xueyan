package com.xueyan.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 支付服务启动类
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.xueyan.payment", "com.xueyan.common"})
public class XueyanPaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(XueyanPaymentApplication.class, args);
    }
}
