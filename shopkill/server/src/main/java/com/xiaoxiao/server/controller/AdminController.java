package com.xiaoxiao.server.controller;

import com.xiaoxiao.model.entity.Admin;
import com.xiaoxiao.server.service.IAdminService;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    /**
     * 管理员用户登录
     * @param admin
     * @return
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody Admin admin) {
        Admin adminDao = adminService.selectAdminByNameAndPwd(admin);

        if (adminDao == null) {
            return ResultUtils.error("用户名或密码错误!");
        }

        return ResultUtils.success("登录成功！",adminDao.getId());
    }

    @GetMapping("/getInfo")
    public ResultVo getInfo(Integer userId) {
        Admin adminDao = adminService.getAdminById(userId);

        return ResultUtils.success("查询成功！", adminDao.getUsername());
    }
}
