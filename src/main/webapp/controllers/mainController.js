angular.module("komGikkApp")
    .controller("mainCtrl", function ($scope, $http, properties, activityService, timeEventService) {

        $scope.data = {};
        timeEventService.initTimeEvents($scope.data);

        $http.get(properties.activitiesUrl)
            .success(function(returnValue) {
                activityService.addAllActivities($scope.data, returnValue);
            })
            .error(function(error) {
                console.log("Feil ved henting av aktiviteter: " + error);
               $scope.data.error = error;
            });

        $http.get(properties.userUrl)
            .success(function (returnValue){
                $scope.data.user = returnValue;
                $scope.username = $scope.data.user.name;
            })
            .error(function (error) {
                console.log("Feil ved henting av bruker: " + error);
                $scope.data.error = error;
            });
    });