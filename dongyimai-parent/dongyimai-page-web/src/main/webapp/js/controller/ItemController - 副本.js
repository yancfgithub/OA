 app.controller("brandController",function($scope,$controller,$http,brandService) {

     //这里是继承baseController，套格式
     $controller('baseController',{$scope:$scope})



        $scope.findAll = function () {
            brandService.findAll().success(
                function (response) {
                    $scope.list = response;
                })
        }

        $scope.findPage = function(page, rows) {
            brandService.findPage(page,rows).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;
                }
            )
        }

        $scope.findOne = function(id) {
            brandService.findOne(id).success( //2.此处的$scope.entity的值是哪里来的？
                function (response) {
                    $scope.entity = response;
                }
            )
        }

        $scope.save = function() {
            if($scope.entity.id != null) {
                brandService.update($scope.entity).success(//此处是不是写上$scope.entity就可以将对象传到后台呢？
                    function (response) {
                        if(response.success) {
                            $scope.reloadList();
                        }else {
                            alert(response.message);
                        }
                    }
                )
            }else {
                brandService.add($scope.entity).success(
                    function (response) {
                        if(response.success) {
                            $scope.reloadList();
                        }else {
                            alert(response.message);
                        }
                    }
                )
            }
        }

        $scope.del = function() {

         console.log($scope.ids);
            brandService.del($scope.ids).success(
                function (response) {
                    if(response.success) {
                        $scope.reloadList();
                    }else {
                        alert(response.message);
                    }
                }
            )
        }

        $scope.searchEntity = {}
        $scope.search = function(page,rows) {

         alert(33)

            brandService.search(page,rows,$scope.searchEntity).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;
                }
            )
        }
    })