package com.xiaoxiao.model.wrap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaoxiao.model.entity.ItemKill;
import lombok.Data;

import java.util.Date;

@Data
public class ItemKillParm {
    // 当前页
    private Integer currentPage;
    // 页容量
    private Integer pageSize;
    // 商品名称
    private String itemName;
}
