package com.offcn.page.service.impl;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;



    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map dataMap = new HashMap();

            //获取goods对象
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            dataMap.put("goods",tbGoods);

            //获取goodsDesc对象
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataMap.put("goodsDesc",goodsDesc);

            //根据goods的三级分类id获取对应的分类名，面包屑显示
            String name1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String name2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String name3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            dataMap.put("itemCat1",name1);
            dataMap.put("itemCat2",name2);
            dataMap.put("itemCat3",name3);

            //获取sku内容
            TbItemExample exp = new TbItemExample();
            TbItemExample.Criteria criteria = exp.createCriteria();
            //状态为1，同时为同一件商品
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);

            //按照默认排序，第一个就是默认规格
            exp.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(exp);
            dataMap.put("itemList",itemList);


            Writer out = new FileWriter(pagedir+goodsId+".html");
            template.process(dataMap,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] ids) {

        try {
            for (Long id : ids) {
                new File(pagedir+id+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
