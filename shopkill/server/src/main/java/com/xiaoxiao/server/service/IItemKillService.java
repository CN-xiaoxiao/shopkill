package com.xiaoxiao.server.service;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemKillParm;
import org.springframework.stereotype.Service;

public interface IItemKillService {
    PageInfo<ItemKill> selectQuestionInfoListByCondition(ItemKillParm itemKillParm);

    Boolean killItemRedis(Integer killId, Integer userId);

    Boolean killItemRedisson(Integer killId, Integer userId);

    Boolean killItemZookeeper(Integer killId, Integer userId);

    ItemKill selectItemKillById(Integer killId);

    boolean insertKillItem(ItemKill itemKill);

    boolean updateIsActive(Integer id);
}
