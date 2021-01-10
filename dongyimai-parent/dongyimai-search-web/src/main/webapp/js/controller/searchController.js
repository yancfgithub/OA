 app.controller("searchController",function($scope,$controller,$http,searchService,$location) {

     $scope.searchMap = {'keywords':'','category':'','brand':'','price':'','pageNo':1,'pageSize':20,'sortField':'','sort':'','spec':{}};

        $scope.search = function () {
            $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
            searchService.search($scope.searchMap).success(
                function (response) {
                    $scope.resultMap = response;

                    buildPageLabel();
                })
        }





        buildPageLabel = function() {

            $scope.pageLabel = [];

            var startPage = 1;
            var lastPage = $scope.resultMap.totalPages;

            if($scope.resultMap.totalPages > 5) {
                if($scope.searchMap.pageNo <= 3) {
                    lastPage = 5;
                }
                else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) {
                    startPage = $scope.resultMap.totalPages - 4;
                }
                else {
                    startPage = $scope.searchMap.pageNo - 2;
                    lastPage = $scope.searchMap.pageNo + 2;
                }
            }

            for(var i = startPage; i <= lastPage; i++) {
                $scope.pageLabel.push(i);
            }

        }

        //点击翻页
     $scope.queryPage = function(pageNo) {
            if(pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
                return;
            }
            $scope.searchMap.pageNo = pageNo;
         $scope.search();
     }

     //点击跳转
     $scope.queryToPage = function(page) {
         if(page < 1 || page > $scope.resultMap.totalPages) {
             return;
         }
         $scope.searchMap.pageNo = page;
         $scope.search();
     }

     //是否是品牌
     $scope.keywordsIsBrand = function() {

        for(var i = 0;i<$scope.resultMap.brandList.length;i++) {
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
                return true;
            }
        }
        return false;
     }

     //判断是否为本页
     $scope.isPage = function(p) {
            if(parseInt($scope.searchMap.pageNo) == parseInt(p)) {
                return true;
            }
            return false;
     }

     //点击排序
     $scope.sort = function(sortField,sortValue) {
            $scope.searchMap.sortField = sortField;
            $scope.searchMap.sort = sortValue;
            $scope.search();
     }



        //点击-赋值
     $scope.addSearchItem = function (key,value) {
         if(key=='category' || key=='brand'|| key=='price') {
              $scope.searchMap[key] = value;
         }else {
             $scope.searchMap.spec[key] = value;
         }
         $scope.search();
     }

     //点击-删除
     $scope.deleteSearchItem = function (key) {
         if(key=='category' || key=='brand'|| key=='price') {
             $scope.searchMap[key] = '';
         }else {
             delete $scope.searchMap.spec[key];
         }
         $scope.search();
     }

     //从首页跳转来进行搜索
     $scope.fromIndexPage = function () {
         $scope.searchMap.keywords = $location.search()['keywords'];
         $scope.search();
     }

    })