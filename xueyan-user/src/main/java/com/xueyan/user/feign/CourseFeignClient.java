package com.xueyan.user.feign;

import com.xueyan.common.result.Result;
import com.xueyan.user.feign.dto.CourseFeignVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 课程服务 Feign 客户端 — user → course
 * <p>
 * 通过 Nacos 服务发现自动解析 xueyan-course 的实际地址。
 * Feign 接口只需要声明，Spring Cloud 自动生成代理实现。
 */
@FeignClient(name = "xueyan-course", url = "http://localhost:8102", path = "/course")
public interface CourseFeignClient {

    /**
     * 查询课程信息
     * <p>
     * 对应 xueyan-course: GET /course/feign/{id}
     */
    @GetMapping("/feign/{id}")
    Result<CourseFeignVO> getCourseById(@PathVariable("id") Long courseId);
}
