package com.xiaoxiao.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Item {
    private Integer id;

    private String name;

    private String code;

    private Double price;

    private Long stock;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date purchaseTime;

    private Integer isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    private Integer isKill;
}
