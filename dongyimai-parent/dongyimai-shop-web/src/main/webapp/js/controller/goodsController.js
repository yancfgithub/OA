 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope})
	

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]}

	//查询实体 
	$scope.findOne=function(){

		var id = $location.search()['id'];

		if(id == null) {
			return;
		}

		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);

				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=  JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

				for(var i = 0;i<$scope.entity.itemList.length;i++) {
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}

	$scope.specCheck = function(name,value) {
		var item = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(item,"attributeName",name);

		if(object == null) {
			return false;
		}else {
			if(object.attributeValue.indexOf(value)>=0) {
				return true;
			}else {
				return false;
			}
		}

	}
	
	//批量删除
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.ids=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}



	$scope.addImageList = function() {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	$scope.del = function(id) {
		$scope.entity.goodsDesc.itemImages.splice(id,1);
	}

	$scope.save = function(){
		$scope.entity.goodsDesc.introduction = editor.html();

		var saves;

		if($scope.entity.goods.id == null) {
			saves = goodsService.add($scope.entity);
		}else {
			saves = goodsService.update($scope.entity);
		}


		saves.success(
			function(response){
				if(response.success){
					//重新查询
					alert(response.message)
					$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]}
					editor.html('');
				}else{
					alert(response.message);
				}
			}
		);
	}

	$scope.image_entity= {};

	$scope.upload = function () {
		uploadService.upload().success(
			function (response) {
				console.log(response)
				if(response.message) {
					$scope.image_entity.url = response.message;
					console.log(response.message)
				}else {
					alert(response.message);
				}
			}
		).error(function () {
			alert("上传错误")
		})

	}

	$scope.selectItemCat1List = function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.ItemCat1List= response;
			}
		);
	}

	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		if(newValue) {
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.ItemCat2List= response;
				}
			);
		}
	})

	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		if(newValue) {
			itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.ItemCat3List= response;
				}
			);
		}
	})

	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		if(newValue) {
			itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.goods.typeTemplateId= response.typeId;
				}
			);
		}
	})

	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
		if(newValue) {
			typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate = response;
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

					/*if($location.search()['id'] == null) {
						$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
					}*/

					if($location.search()['id']==null){
						$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
					}
				}
			);

			typeTemplateService.findSpecByTempId(newValue).success(
				function (response) {
					$scope.specList = response;
				}
			)
		}
	})


	$scope.updateSpecAttribute = function ($event,name,value) {
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);

		if(object != null) {	//如果存在该对象
			if($event.target.checked) {		//勾选
				object.attributeValue.push(value);
			}else {		//取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);

				if(object.attributeValue.length == 0) {
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else {		//如果不存在该对象
			$scope.entity.goodsDesc.specificationItems.push({"attributeValue":[value],"attributeName":name});
		}
	}

	$scope.creatItemList = function () {
		$scope.entity.itemList = [{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];

		var items = $scope.entity.goodsDesc.specificationItems;
		for(var i = 0;i<items.length;i++) {
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}

	addColumn = function (skuList,attributeName,attributeValue) {

		var newList = [];

		for(var i =  0;i<skuList.length;i++) {

			var oldRow = skuList[i];

			for(j = 0;j<attributeValue.length;j++) {

				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[attributeName] = attributeValue[j];
				newList.push(newRow);
			}
		}

		return newList;
	};

	// $scope.status['未审核','已审核','审核未通过','关闭'];
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

	$scope.itemCatList=[];

	$scope.findItemCatList = function () {
		itemCatService.findAll().success(
			function (response) {
				for(var i = 0;i<response.length;i++) {
					$scope.itemCatList[response[i].id] = response[i].name;
				}
			}
		);
	}

});	