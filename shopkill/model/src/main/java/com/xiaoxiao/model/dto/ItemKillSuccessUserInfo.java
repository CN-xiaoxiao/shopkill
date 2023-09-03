package com.xiaoxiao.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class ItemKillSuccessUserInfo implements Serializable {
    private String code;

    private Integer itemId;

    private Integer killId;

    private String userId;

    private Byte status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String userName;

    private String phone;

    private String email;

    private String itemName;

    public ItemKillSuccessUserInfo() {
    }
}
