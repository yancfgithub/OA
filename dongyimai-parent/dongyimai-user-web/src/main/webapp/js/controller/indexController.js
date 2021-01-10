app.controller('indexController',function ($scope,$controller,loginService) {
    $scope.showLoginName = function () {
        loginService.loginName().success(
            function(response){
                console.log(response)
                $scope.loginName = response.loginName;
            });
    }
})