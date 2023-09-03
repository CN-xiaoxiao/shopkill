package com.xiaoxiao.server.service;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemKillParm;
import org.springframework.stereotype.Service;

public interface IItemKillService {
    PageInfo<ItemKill> selectQuestionInfoListByCondition(ItemKillParm itemKillParm);

    Boolean killItem(Integer killId, Integer userId);
}
