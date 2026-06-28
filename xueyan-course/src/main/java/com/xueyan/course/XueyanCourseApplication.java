package com.xueyan.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 课程服务启动类
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.xueyan.course", "com.xueyan.common"})
public class XueyanCourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(XueyanCourseApplication.class, args);
    }
}
