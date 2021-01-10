 app.controller("searchController",function($scope,$controller,$http,searchService) {

        $scope.search = function () {
            searchService.search($scope.searchMap).success(
                function (response) {
                    $scope.resultMap = response;
                })
        }

    })