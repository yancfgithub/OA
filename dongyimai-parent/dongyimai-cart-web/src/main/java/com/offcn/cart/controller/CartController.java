package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 5000)
    private CartService cartService;


    @RequestMapping("findCartList")
    public List<Cart> findCartList() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartList == null || cartList.equals("")) {
            cartList = "[]";
        }

        List<Cart> cartList_cookie = JSON.parseArray(cartList, Cart.class);
        
        if(name.equals("anonymousUser")) {//未登录，从cookie中获取

            return cartList_cookie;
        }else{  //已登录，从redis中获取

            System.out.println("已登录，从redis中获取！！！！！！！！！！！！！！");

            List<Cart> cartList_redis = cartService.findCartListFromRedis(name);

            //判断cookie中是否有购物车
            if(cartList_cookie != null && cartList_cookie.size() > 0) {     //cookie中有购物车
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);

                //清空cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");

                //将cartList_redis存入redis中
                cartService.saveCartListToRedis(name,cartList_redis);
            }

            return cartList_redis;
        }



    }


    @RequestMapping("addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList( Long itemId, Integer num) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //查询购物车列表
            List<Cart> cartList = findCartList();

            //添加
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if(name.equals("anonymousUser")) {//未登录，从cookie中获取
                //重新刷新cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600,"UTF-8");

            }else {
                cartService.saveCartListToRedis(name,cartList);
            }

            return new Result(true,"购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"购物车添加失败");
        }
    }
}
