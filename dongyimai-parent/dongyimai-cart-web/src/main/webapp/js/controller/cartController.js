//购物车控制层
app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    }

    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.success){
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;

                for(var i = 0;i<$scope.addressList.length;i++) {
                    if($scope.addressList[i].isDefault) {
                        $scope.address = $scope.addressList[i];
                    }
                }
            }
        );
    }
    
    //被选中的地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    //判断是否是被选中的地址
    $scope.isSelectedAddress  = function (address) {
        if($scope.address == address) {
            return true;
        }else {
            return false;
        }
    }

    $scope.order = {paymentType:1};

    //选择在线还是货到付款
    $scope.selectPay = function (type) {
        $scope.order.paymentType = type;
    }

    $scope.submitOrder = function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success) {

                    if($scope.order.paymentType == '1') {
                        location.href = "pay.html";
                    }
                    if($scope.order.paymentType == '2') {
                        location.href = "paysuccess.html";
                    }
                }else {
                    alert(response.message);
                }
            }
        );

    }


});