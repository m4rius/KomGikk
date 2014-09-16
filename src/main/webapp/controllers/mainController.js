angular.module("komGikkApp")
    .constant("activitiesUrl", "http://localhost:9090/api/activities")
    .constant("userUrl", "http://localhost:9090/api/user")
    .controller("mainCtrl", function ($scope, $http, activitiesUrl, userUrl) {

        $scope.data = {};

        $http.get(activitiesUrl)
            .success(function(data) {
                $scope.data.activities = data;
            })
            .error(function(error) {
               $scope.data.error = error;
            });

        $http.get(userUrl)
            .success(function (data){
                $scope.data.user = data;
            })
            .error(function (error) {
                $scope.data.error = error;
            });


    });