app.controller("contentController",function($scope,contentService) {

    $scope.contentList = [];

    $scope.findByCartegoryId = function (categoryId) {
        contentService.findByCartegoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        );
    }

    $scope.toSearchPage = function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }


})