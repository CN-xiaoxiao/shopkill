package com.xiaoxiao.server.controller;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import com.xiaoxiao.model.entity.User;
import com.xiaoxiao.model.wrap.UserParm;
import com.xiaoxiao.server.service.IUserService;
import com.xiaoxiao.server.utils.SaltMD5Util;
import org.apache.zookeeper.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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

        String username = user.getUsername();
        String password = user.getPassword();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {

            return ResultUtils.error("用户名或密码不能为空！");
        }

        User userDao = userService.selectByUserName(user.getUsername());

        if (userDao == null) {
            return ResultUtils.error("登录失败！账号或密码错误");
        }

        boolean passwordFlag = SaltMD5Util.verifySaltPassword(password, userDao.getPassword());

        if (!passwordFlag) {
            return ResultUtils.error("登录失败！账号或密码错误");
        }

        return ResultUtils.success("登录成功！", userDao.getId());
    }

    @GetMapping("/getInfo")
    public ResultVo getInfo(Integer id){
        User user = userService.getUserById(id);

        return ResultUtils.success("查询成功",user.getUsername());
    }

    @PostMapping("/register")
    public ResultVo register(@RequestBody User user) {
        if ("".equals(user.getUsername()) || user.getUsername()==null) {
            return ResultUtils.error("用户名错误！");
        }
        if (!Objects.equals(user.getPassword(), user.getRepassword())
                || user.getPassword() == null
                || "".equals(user.getPassword())) {
            return ResultUtils.error("密码不一致！");
        }
        if ("".equals(user.getEmail()) || user.getEmail() == null) {
            return ResultUtils.error("邮箱错误！");
        }
        if ("".equals(user.getPhone()) || user.getPhone()==null) {
            return ResultUtils.error("手机号为空！");
        }

        boolean flag = userService.addUser(user);

        if (!flag) {
            return ResultUtils.error("注册失败！");
        }

        return ResultUtils.success("注册成功!");
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
