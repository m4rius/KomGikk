angular.module("komGikkApp")
    .controller("timeCtrl", function($scope, $http, properties, activityService) {

        var currentAction = null;

        function postJson(json) {
            $http.post(properties.timeeventUrl, json)
                .success(function(data) {
                    $scope.data.timeevents.push(data);
                })
        }

        $scope.doStart = function() {
            postJson("{\"specialEvent\":\"START\"}");
        };

        $scope.doEnd = function() {
            postJson("{\"specialEvent\":\"END\"}");
        };

        $scope.startActivity = function (activity) {
            //post new save timeevent
            currentAction = activity.key;
            postJson("{\"activityKey\":\"" + activity.key + "\"}");
        };

        //TODO grisetet logikk
        //TODO: hvorfor blir denne kalt så mange ganger?
        $scope.showSpecialEventButton = function(type) {
            var started = activityService.isStarted($scope.data.timeevents);
            var typeIsStart = type == 'START';
            var typeIsEnd = type == 'END';

            if (started && typeIsStart) {
                return false;
            }
            if (started && typeIsEnd) {
                return true;
            }

            if (!started && typeIsStart) {
                return true;
            }

            if (!started && typeIsEnd) {
                return false;
            }

            return false;
        };

        $scope.showActivityButton = function(activity) {
            return activity.key != currentAction;
        };

        $scope.getActivityName = function(activityKey) {
            if (angular.isUndefined(activityKey)) {
                return "";
            }

            return activityService.findActivityByKey(activityKey, $scope.data);
        }



    });