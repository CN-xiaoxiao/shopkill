package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemKillParm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemKillMapper {

    List<ItemKill> selectByPageAndCondition(ItemKillParm itemKillParm);

    /**
     * 查询可秒杀的
     * @param killId 商品id
     * @return
     */
    ItemKill selectById(@Param("killId") Integer killId);

    int updateKillItem(@Param("killId") Integer killId);

    /**
     * 查询一个在表中的秒杀信息
     * @param itemId 商品id
     * @return
     */
    ItemKill selectOneById(@Param("itemId") Integer itemId);

    boolean insertKillItem(ItemKill itemKill);

    boolean updateIsActive(@Param("id") Integer id);

    List<ItemKill> findAllKills();

    List<ItemKill> selectAllByPageAndCondition(ItemKillParm itemKillParm);

    boolean updateKillItemByItemId(ItemKill itemKill1Dao);

    ItemKill selectItemKillById(@Param("id") Integer id);

    boolean deleteItemKillByItemId(@Param("itemId") Integer itemId);

    boolean updateActiveById(@Param("isActive") Integer isActive, @Param("itemId") Integer itemId);
}
