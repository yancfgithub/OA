package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String, Object> search(Map searchMap);


    /**
     * 将传入的item集合都传入solr库中
     */
    public void importSolr(List<TbItem> itemList);

    /**
     * 根据商品id删除solr中对应的商品
     */
    public void delete(List goodsIdList);
}
