app.service('brandService',function ($http) {


    this.findAll = function () {
       return $http.get('../brand/findAll.do');
    }

    this.findPage = function (page,rows) {
        return $http.get('../brand/findPage.do?page='+page+"&rows="+rows);
    }

    this.findOne = function (id) {
        return  $http.get('../brand/findOne.do?id='+id);
    }

    this.update = function (entity) {
        return $http.post('../brand/update.do?',entity);
    }

    this.add = function (entity) {
        return $http.post('../brand/add.do?',entity);
    }

    this.del = function (ids) {
        return $http.post('../brand/del.do?ids='+ids);
    }

    this.search = function (page,rows,searchEntity) {
        return $http.post('../brand/search.do?page='+page+"&rows="+rows, searchEntity);
    }


    this.selectOptionList = function () {
        return $http.post('../brand/selectOptionList.do?');
    }
})