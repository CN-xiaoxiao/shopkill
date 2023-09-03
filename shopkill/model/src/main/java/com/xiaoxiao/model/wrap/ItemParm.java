package com.xiaoxiao.model.wrap;

import lombok.Data;

@Data
public class ItemParm {
    // 当前页
    private Integer currentPage;
    // 页容量
    private Integer pageSize;
    // 商品名称
    private String name;
}
