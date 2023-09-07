package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    Admin selectAdminByName(@Param("userName") String userName);

    Admin selectAdminById(@Param("userId") Integer userId);
}
