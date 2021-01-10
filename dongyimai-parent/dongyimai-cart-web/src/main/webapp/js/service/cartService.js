//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    this.sum = function (cartList) {

        totalValue={totalNum:0,totalMoney:0.00};

        for(var i = 0;i<cartList.length;i++) {
            var cart = cartList[i];

            for(var j = 0;j<cart.orderItemList.length;j++) {
                totalValue.totalNum += cart.orderItemList[j].num;
                totalValue.totalMoney += cart.orderItemList[j].totalFee;
            }
        }
        return totalValue;
    }

    //获取地址列表
    this.findAddressList=function(){
        return $http.get('address/findAddressByUserId.do');
    }

    //增加
    this.submitOrder=function(entity){
        return  $http.post('../order/add.do',entity );
    }
});