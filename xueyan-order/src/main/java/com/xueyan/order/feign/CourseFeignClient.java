package com.xueyan.order.feign;

import com.xueyan.order.feign.dto.CourseFeignVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 课程服务 Feign 客户端 — order → course
 * <p>
 * 下单时需要：查询课程信息 + 扣减库存
 */
@FeignClient(name = "xueyan-course", url = "http://localhost:8102", path = "/course")
public interface CourseFeignClient {

    /**
     * 查询课程信息
     */
    @GetMapping("/feign/{id}")
    CourseFeignVO getCourseById(@PathVariable("id") Long courseId);

    /**
     * 扣减库存
     */
    @PutMapping("/{id}/stock/deduct")
    Boolean deductStock(@PathVariable("id") Long courseId,
                        @RequestParam("count") int count);

    /**
     * 恢复库存（订单取消补偿）
     */
    @PutMapping("/{id}/stock/restore")
    Boolean restoreStock(@PathVariable("id") Long courseId,
                         @RequestParam("count") int count);
}
