package com.xiaoxiao.model.wrap;

import lombok.Data;

@Data
public class UserParm {
    // 当前页
    private Integer currentPage;
    // 页容量
    private Integer pageSize;
    private String userName;
}
