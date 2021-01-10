 app.controller("itemController",function($scope, $http) {

 //购物车数量的增减
     $scope.addNum = function(num){
		 $scope.num = $scope.num + num;
		 if($scope.num < 1){
			 $scope.num = 1;
		 }
	 }
	 
	 $scope.itemspecList = {};
	 
	 //用户点击记录
	 $scope.selectIn = function(name,value){
		 $scope.itemspecList[name] = value;
		 searchMap();
	 }
	 
	 //点击被选中
	 $scope.isSelected = function(name,value){
		 if($scope.itemspecList[name] == value){
			 return true;
		 }else {
			 return false;
		 }
	 }
	 
	 //默认加载的
	 $scope.loadSku = function(){
		 $scope.sku = skuList[0];
		 $scope.itemspecList = JSON.parse(JSON.stringify($scope.sku.spec));
	 }
	 
	 //判断两个map集合是否完全相等
	 matchObj = function(map1,map2){
		 for(var k in map1){
			 if(map1[k] != map2[k]){
				 return false;
			 }
		 }
		 for(var k in map2){
			 if(map1[k] != map2[k]){
				 return false;
			 }
		 }
		 return true;
	 }
	 
	 //寻找sku
	 searchMap = function(){
		 
		 for(var i = 0; i < skuList.length; i++){
			 if(matchObj(skuList[i].spec,$scope.itemspecList)){
				 $scope.sku = skuList[i];
				 return;
			}
		 } 
		 $scope.sku = {id:0,title:"",price:0};
	 }


	 //添加商品到购物车
	 $scope.addToCart=function(){
		 $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
			 + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
			 function(response){
				 if(response.success){
					 location.href='http://localhost:9107/cart.html';//跳转到购物车页面
				 }else{
					 alert(response.message);
				 }
			 }
		 );
	 }
	 
    })