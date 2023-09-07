package com.xiaoxiao.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.User;
import com.xiaoxiao.model.mapper.UserMapper;
import com.xiaoxiao.model.wrap.UserParm;
import com.xiaoxiao.server.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User selectByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public Boolean addUser(User user) {

        User userDao = userMapper.selectByUserName(user.getUserName());

        if (userDao != null) {
            return false;
        }

        user.setCreateTime(new Date());

        return userMapper.insertUser(user);
    }

    @Override
    public boolean updateById(User user) {

        User userDao = userMapper.selectByUserName(user.getUserName());

        if (userDao == null) {
            return false;
        }

        return userMapper.updateUserById(user);
    }

    @Override
    public boolean removeById(Integer userId) {

        return userMapper.removeUserById(userId);
    }

    @Override
    public PageInfo<User> selectUserInfoListByCondition(UserParm userParm) {
        //1.分页助手开始分页
        PageHelper.startPage(userParm.getCurrentPage(), userParm.getPageSize());
        //2.调用dao层的select查询方法，第一个select方法会被分页
        List<User> sysUsers = userMapper.selectByPageAndCondition(userParm);
        //3。封装分页结果到PageInfo中
        return new PageInfo<>(sysUsers, 10);
    }
}
