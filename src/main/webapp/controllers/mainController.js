angular.module("komGikkApp")
    .controller("mainCtrl", function ($scope, $http, properties, activityService, timeEventService) {

        $scope.data = {};
        timeEventService.initTimeEvents($scope.data);

        $http.get(properties.activitiesUrl)
            .success(function(returnValue) {
                var index;
                if (angular.isArray(returnValue)) {
                    for (index = 0; index < returnValue.length; ++index) {
                        activityService.addActivity($scope.data, returnValue[index]);
                    }
                }
            })
            .error(function(error) {
                console.log("Feil ved henting av aktiviteter: " + error);
               $scope.data.error = error;
            });

        $http.get(properties.userUrl)
            .success(function (returnValue){
                $scope.data.user = returnValue;
            })
            .error(function (error) {
                console.log("Feil ved henting av bruker: " + error);
                $scope.data.error = error;
            });

        $http.get(properties.timeeventUrl + "/list")
            .success(function(returnValue) {
                timeEventService.addAllTimeEvents($scope.data, returnValue);
            })
            .error(function (error) {
                console.log("Feil ved henting av timeevents: " + error);
                $scope.data.error = error;
            })

    });