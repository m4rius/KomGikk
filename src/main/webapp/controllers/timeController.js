var app = angular.module("komGikkApp");

app.controller("timeCtrl", function($scope, $http, $filter, properties, activityService, timeEventService) {

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
            return "btn-info"
        }
        return "btn-primary";
    };

    $scope.getActivityName = function(activityKey) {
        if (angular.isUndefined(activityKey)) {
            return "";
        }

        return activityService.findActivityByKey(activityKey, $scope.data).name;
    };


    $scope.filterEvent = function(event) {
        return event.isDeleted !== true;
    };

    $scope.showActivities = function(event) {
        if (event.specialEvent) {
            return event.specialEvent;
        }
        if(event.activityKey) {
            return activityService.findActivityByKey(event.activityKey, $scope.data).name;
        }
        return '';
    };

    $scope.checkTime = function(data, event) {
        console.log("checkTime");
        //todo impl

        //returner string som eventuel feilmelding
        return true;
    };

    $scope.deleteEvent = function(event) {
        event.isDeleted = true;
    };

    $scope.addEvent = function() {
        var date = new Date();
        $scope.data.events.list.push({
            time: date.getHours() + ":" + date.getMinutes(),
            date: date.getFullYear() + "." + date.getMonth() + "." + date.getDay(),
            specialEvent: null,
            isNew: true
        });
    };

    $scope.saveTable = function() {
        console.log("saveTable");

        $http.put(properties.timeEventListUrl, $scope.data.events.list)
            .success(function(returnValue) {
                timeEventService.addAllTimeEvents($scope.data, returnValue);
            })
    };

    $scope.cancel = function() {
        for (var i = $scope.data.events.list.length; --i;) {
            var event = $scope.data.events.list[i];
            //remove deleted flag
            if (event.isDeleted) {
                delete event.isDeleted;
            }
            if (event.isNew) {
                $scope.data.events.list.splice(i, 1);
            }
        }
    };

});