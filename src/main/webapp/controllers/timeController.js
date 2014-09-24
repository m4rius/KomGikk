angular.module("komGikkApp")
    .controller("timeCtrl", function($scope, $http, properties, activityService, timeEventService) {

        function postNewTimeEvent(json) {
            $http.post(properties.timeeventUrl, json)
                .success(function(returnValue) {
                    timeEventService.addNewTimeEvent($scope.data, returnValue);
                })
        }

        $scope.doStart = function() {
            postNewTimeEvent("{\"specialEvent\":\"START\"}");
        };

        $scope.doEnd = function() {
            postNewTimeEvent("{\"specialEvent\":\"END\"}");
        };

        $scope.startActivity = function (activity) {
            //post new save timeevent
            postNewTimeEvent("{\"activityKey\":\"" + activity.key + "\"}");
        };

        $scope.showStart = function() {
            return !$scope.data.events.isStarted;
        };

        $scope.showEnd = function() {
            return $scope.data.events.isStarted && !$scope.data.events.isEnded;
        };

        $scope.showActivityButtons = function() {
            return $scope.data.events.isStarted && !$scope.data.events.isEnded;
        };

        $scope.activityButtonClass = function(activity) {
            if ($scope.data.events.currentAction == null) {
                return "btn-primary";
            }
            if ($scope.data.events.currentAction == activity.key) {
                return "btn-warning"
            }
            return "btn-primary";
        };

        $scope.getActivityName = function(activityKey) {
            if (angular.isUndefined(activityKey)) {
                return "";
            }

            return activityService.findActivityByKey(activityKey, $scope.data).name;
        }



    });