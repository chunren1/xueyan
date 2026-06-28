package com.xueyan.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueyan.payment.entity.LocalMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LocalMessageMapper extends BaseMapper<LocalMessage> {

    /** 查询待重发的消息 */
    @Select("SELECT * FROM local_message WHERE status = 'PENDING' AND next_retry_at <= #{now} AND retry_count < max_retry LIMIT 100")
    List<LocalMessage> findPendingMessages(LocalDateTime now);
}
