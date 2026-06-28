package com.xueyan.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xueyan.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper — MyBatis-Plus BaseMapper 自动提供 CRUD
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 复杂查询可在此扩展，简单 CRUD 由 MyBatis-Plus 自动生成
}
