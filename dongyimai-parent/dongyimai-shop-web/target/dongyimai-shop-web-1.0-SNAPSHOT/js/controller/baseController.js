app.controller("baseController",function($scope) {

    var ids = [];
    $scope.getIds = function(id) {
        var index  = ids.indexOf(id);
        if(index>-1) { //说明该数字存在
            ids.splice(index, 1);
        }else { //说明该数字不存在
            ids.push(id);
        }
    //    由于ids是当前域的变量，要让另外一个brandController也是用该变量就需要将该变量赋到$scope域当中
    $scope.ids = ids;
    console.log($scope.ids);
    }

    $scope.reloadList = function() {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.paginationConf = {
        currentPage : 1,
        totalItems : 10,
        itemsPerPage : 10,
        perPageOptions:[10,20,30,40],

        onChange : function () {
            $scope.reloadList();
        }
    }

    //给定的集合找指定关键字段的值是否存在
    /*$scope.searchObjectByKey = function (list,key,value) {
        for(var i=0;i<list.length;i++) {
            if(list[i][key] == value) {
                return list;
            }
        }

        return null;
    }*/

    $scope.searchObjectByKey=function (list, key, value) {
        for(var i=0;i<list.length;i++){
            if(list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }





    //一个小小的疑问点：如果对象不为空，说明该对象存在，
        //判断勾选以后，直接push添加value，如果里面已经含有该值，在刷新页面以后该值前面的复选框是被勾选的状态吗？如果不是，那再次勾选会不会产生两个一样的内容呢
        //判断取消，
})