angular.module("komGikkApp")
    .constant("activitiesUrl", "http://localhost:9090/api/activities")
    .controller("mainCtrl", function ($scope, $http, activitiesUrl) {

        $scope.data = {};

        $http.get(activitiesUrl)
            .success(function(data) {
                $scope.data.activities = data;
            })
            .error(function(error) {
               $scope.data.error = error;
            });


    });