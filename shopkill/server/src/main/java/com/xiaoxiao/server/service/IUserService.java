package com.xiaoxiao.server.service;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.User;
import com.xiaoxiao.model.wrap.UserParm;

public interface IUserService {
    User selectByUserName(String userName);

    Boolean addUser(User user);

    boolean updateById(User user);

    boolean removeById(Integer userId);

    PageInfo<User> selectUserInfoListByCondition(UserParm userParm);
}
