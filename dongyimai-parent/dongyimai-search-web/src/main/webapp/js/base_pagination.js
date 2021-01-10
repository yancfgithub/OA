var app = angular.module("dongyimai",['pagination']);

app.filter('trustHtml',["$sce",function ($sce) {
    return function (data) {
        return    $sce.trustAsHtml(data);
    }
}])