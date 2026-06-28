package com.xueyan.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueyan.order.entity.LocalMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 本地消息表 Mapper
 */
@Mapper
public interface LocalMessageMapper extends BaseMapper<LocalMessage> {

    /**
     * 查询待重发的消息（状态=PENDING 且 已到达重试时间）
     */
    @Select("SELECT * FROM local_message WHERE status = 'PENDING' AND next_retry_at <= #{now} AND retry_count < max_retry LIMIT 100")
    List<LocalMessage> findPendingMessages(LocalDateTime now);
}
