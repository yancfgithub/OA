package com.offcn.group;

import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {

    private TbGoods Goods;
    private TbGoodsDesc GoodsDesc;
    private List<TbItem> itemList;

    public Goods() {
    }

    public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
        Goods = goods;
        GoodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public TbGoods getGoods() {
        return Goods;
    }

    public void setGoods(TbGoods goods) {
        Goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return GoodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        GoodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
