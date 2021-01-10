package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbContentMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData() {
        TbItemExample exp = new TbItemExample();
        TbItemExample.Criteria criteria = exp.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(exp);

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

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");

        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }


}
