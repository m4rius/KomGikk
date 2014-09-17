angular.module("komGikkApp")
    .controller("mainCtrl", function ($scope, $http, properties) {

        $scope.data = {};

        $http.get(properties.activitiesUrl)
            .success(function(data) {
                $scope.data.activities = data;
            })
            .error(function(error) {
               $scope.data.error = error;
            });

        $http.get(properties.userUrl)
            .success(function (data){
                $scope.data.user = data;
            })
            .error(function (error) {
                $scope.data.error = error;
            });


    });