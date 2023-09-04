package com.xiaoxiao.server.controller;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import com.xiaoxiao.model.entity.User;
import com.xiaoxiao.model.wrap.UserParm;
import com.xiaoxiao.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody User user) {
        User userDao = userService.selectByUserName(user.getUserName());

        if (userDao == null || !userDao.getPassword().equals(user.getPassword())) {
            return ResultUtils.error("登录失败！账号或密码错误");
        }

        return ResultUtils.success("登录成功！", user.getId());
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping
    public ResultVo add(@RequestBody User user) {
        boolean flag = userService.addUser(user);

        if (!flag) {
            return ResultUtils.error("注册失败！");
        }

        return ResultUtils.success("注册成功！");

    }

    /**
     * 编辑用户信息
     * @param user
     * @return
     */
    @PutMapping
    public ResultVo edit(@RequestBody User user) {

        boolean flag = userService.updateById(user);

        if (flag) {
            return ResultUtils.success("编辑用户成功！");
        }

        return ResultUtils.error("编辑用户失败！");
    }

    /**
     * 删除用户（用户注销）
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Integer userId) {
        boolean flag = userService.removeById(userId);

        if (flag) {
            return ResultUtils.success("删除用户成功！");
        }

        return ResultUtils.error("删除用户失败！");
    }

    /**
     * 获取所有用户信息
     * @return
     */
    @GetMapping("/list")
    public ResultVo list(UserParm userParm) {
        PageInfo<User> userPageInfo = userService.selectUserInfoListByCondition(userParm);

        return ResultUtils.success("查询成功", userPageInfo);
    }
}
