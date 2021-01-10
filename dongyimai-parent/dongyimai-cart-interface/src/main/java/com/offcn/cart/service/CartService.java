package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

public interface CartService {

    /**
     *  向购物车列表中添加商品
     * @param cartList 要添加到这个购物车集合
     * @param itemId    商品的id
     * @param num   商品的数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);


    /**
     * 从Redis中查找cartList购物车集合
     * @param userName
     * @return
     */
    public List<Cart> findCartListFromRedis(String userName);

    /**
     *将cartList购物车集合存入Redis中
     * @param userName
     * @param cartList
     */
    public void saveCartListToRedis(String userName, List<Cart> cartList);


    /**
     * 登录后将cookie中的购物车合并到redis中的购物车列表
     * @param cartList_cookie
     * @param cartList_redis
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList_cookie,List<Cart> cartList_redis);
}
