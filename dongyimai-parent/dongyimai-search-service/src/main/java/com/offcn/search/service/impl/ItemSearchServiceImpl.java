package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String, Object> map = new HashMap<String, Object>();

        //去掉关键字中的空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ", ""));

        Map<String, Object> highLightSearch = highLightSearch(searchMap);
        map.putAll(highLightSearch);


        //根据商品分类查询品牌与分类集合
        List categoryList = searchCategroy(searchMap);
        map.put("categoryList",categoryList);

        String category = (String) searchMap.get("category");
        if(!"".equals(category)) {
            searchBrandAndSpec(category);
        }else {
            if(categoryList.size() > 0 ) {
                map.putAll(searchBrandAndSpec((String)categoryList.get(0)));
            }
        }



        return map;
    }


    private Map<String,Object> highLightSearch(Map searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();

        HighlightQuery query = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);

        //1.1高亮
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2分类查询，增加过滤条件
        if(!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3品牌查询，增加过滤条件
        if(!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4 查询规格
        if(searchMap.get("spec")!=null) {
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria(Pinyin.toPinyin("key","").toLowerCase()).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5 查询价格
        if(!"".equals(searchMap.get("price"))) {

            String price = (String) searchMap.get("price");
            String[] priceArr =  price.split("-");

            //说明不在最左边,比最小的大就好
            if(!"0".equals(priceArr[0])) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThan(priceArr[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            //不是最右边的，只要小于最大的就行
            if(!"*".equals(priceArr[1])) {
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThan(priceArr[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6查询页数
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if(pageNo== null) {
            pageNo = 1;
        }

        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize == null) {
            pageSize = 20;
        }

        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //1.7 排序
        String sortField = (String) searchMap.get("sortField");
        String sortValue = (String) searchMap.get("sort");

        if(sortValue.equals("ASC")) {
            query.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
        }else if(sortValue.equals("DESC")) {
            query.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
        }


        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntryList) {

            TbItem item = highlightEntry.getEntity();

            if(highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                List<String> snipplets = highlightEntry.getHighlights().get(0).getSnipplets();
                item.setTitle(snipplets.get(0));
            }
        }

        List<TbItem> itemList = page.getContent();

        map.put("rows",itemList);
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());

        return map;
    }

    private List searchCategroy(Map searchMap) {
        List list = new ArrayList();

        Query query = new SimpleQuery();

        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");

        query.setGroupOptions(groupOptions);

        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {

            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }

        return list;
    }

    public Map searchBrandAndSpec(String category) {

        Map map = new HashMap() ;

        Long itemCatId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        List brandList = (List) redisTemplate.boundHashOps("brandList").get(itemCatId);

        map.put("brandList",brandList);

        List specList = (List) redisTemplate.boundHashOps("specList").get(itemCatId);

        map.put("specList",specList);

        return map;
    }

    @Override
    public void importSolr(List<TbItem> itemList) {
        for(TbItem item : itemList) {
            System.out.println("title:"+item.getTitle()+",");

            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);

            Map<String,String> pinYinMap = new HashMap<String, String>();

            for (String key : specMap.keySet()) {
                pinYinMap.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(pinYinMap);
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void delete(List goodsIdList) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goods").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);

        solrTemplate.commit();
    }
}
