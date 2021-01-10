app.service('uploadService',function ($http) {
    this.upload = function () {
        var formData = new FormData();
        formData.append("file",file.files[0]);

        return $http({
            url:'../upload.do',
            method:'POST',
            data:formData,
            headers:{'Content-type':undefined},
            transformRequest:angular.identity
        })
    }
})

