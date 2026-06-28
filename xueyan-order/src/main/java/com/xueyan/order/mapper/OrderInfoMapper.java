package com.xueyan.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueyan.order.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
