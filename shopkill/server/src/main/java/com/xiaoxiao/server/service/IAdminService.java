package com.xiaoxiao.server.service;


import com.xiaoxiao.model.entity.Admin;

public interface IAdminService {
    Admin selectAdminByNameAndPwd(Admin admin);

    Admin getAdminById(Integer userId);
}
