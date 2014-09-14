angular.module("komGikkApp")
    .constant("userUrl", "http://localhost:8080/api/user")
    .controller("createUserCtrl", function($scope, $http, userUrl) {

        $scope.data = {};

        $http.get(userUrl)
            .success(function (data){
                $scope.data.user = data;
            })
            .error(function (error) {
                $scope.data.error = error;
            });
});