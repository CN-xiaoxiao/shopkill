package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.User;
import com.xiaoxiao.model.wrap.UserParm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User selectByUserName(@Param("userName") String userName);

    Boolean insertUser(User user);

    boolean updateUserById(User user);

    boolean removeUserById(@Param("userId") Integer userId);

    List<User> selectByPageAndCondition(UserParm userParm);

    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    User selectById(@Param("id") Integer id);
}
