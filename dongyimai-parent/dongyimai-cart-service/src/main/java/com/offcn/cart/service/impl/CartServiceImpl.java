package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(timeout = 5000)
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //2.获取商家ID
        if(item == null) {  //如果item不存在
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")) {  //如果item商品审核状态为未审核
            throw new RuntimeException("商品未审核");
        }

        String sellerId = item.getSellerId();

        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList,sellerId);

        //4.如果购物车列表中不存在该商家的购物车
        if(cart == null) {
            //4.1 新建购物车对象
            cart = new Cart();

            //4.2 将新建的购物车对象添加到购物车列表
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
            /*TbOrderItem orderItem = creatOrderItem(item,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);*/



            TbOrderItem orderItem = creatOrderItem(item, num);

            orderItemList.add( orderItem );
            cart.setOrderItemList( orderItemList );//有购物车了
            cartList.add( cart );

        }else{//5.如果购物车列表中存在该商家的购物车
        // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemListByItemId(cart.getOrderItemList(),itemId);
            if(orderItem == null) {
                //5.1. 如果没有，新增购物车明细
                orderItem = creatOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else{//5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee( new BigDecimal( orderItem.getNum()*orderItem.getPrice().doubleValue()) );

                //如果商品数量为0，则将商品从商品列表中移除
                if(orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }

                //如果商品列表长度为0，则将购物车从购物车列表中移除
                if(cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }

    private TbOrderItem creatOrderItem(TbItem item, Integer num) {
        if(num<=0){
            throw new RuntimeException("数量非法");
        }

        TbOrderItem orderItem = new TbOrderItem();

        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }

    private TbOrderItem searchOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }

        return null;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {

        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }

        return null;
    }


    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(userName);

        if(cartList == null) {
            cartList = new ArrayList<Cart>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(userName,cartList);
        System.out.println("向Redis中添加购物车");
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis) {
        for (Cart cart : cartList_cookie) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addGoodsToCartList(cartList_redis,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList_redis;
    }
}
