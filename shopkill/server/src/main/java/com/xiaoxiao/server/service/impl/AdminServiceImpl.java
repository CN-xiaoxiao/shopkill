package com.xiaoxiao.server.service.impl;


import com.xiaoxiao.model.entity.Admin;
import com.xiaoxiao.model.mapper.AdminMapper;
import com.xiaoxiao.server.service.IAdminService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements IAdminService {
    @Resource
    private AdminMapper adminMapper;

    @Override
    public Admin selectAdminByNameAndPwd(Admin admin) {

        if (admin.getUsername() == null || admin.getUsername().equals("")) {
            return null;
        }

        Admin adminDao = adminMapper.selectAdminByName(admin.getUsername());

        if (adminDao == null || !adminDao.getPassword().equals(admin.getPassword())) {
            return null;
        }
        return adminDao;
    }

    @Override
    public Admin getAdminById(Integer userId) {

        if (userId == null || userId <= 0) {
            return null;
        }

        return adminMapper.selectAdminById(userId);
    }
}
