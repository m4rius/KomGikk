var app = angular.module("komGikkApp");

app.controller("timeCtrl", function($scope, $http, $filter, properties, activityService, timeEventService) {

    function postNewTimeEvent(json) {
        $http.post(properties.timeeventUrl, json)
            .success(function(returnValue) {
                timeEventService.addNewTimeEvent($scope.data, returnValue);
            })
    }

    $scope.doStart = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.startDayActivity.key + "\"}");
    };

    $scope.doEnd = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.endDayActivity.key + "\"}");
    };

    $scope.startActivity = function (activity) {
        postNewTimeEvent("{\"key\":\"" + activity.key + "\"}");
    };

    $scope.showStart = function() {
        return !$scope.data.events.isStarted;
    };

    $scope.showEnd = function() {
        return $scope.data.events.isStarted && !$scope.data.events.isEnded;
    };

    $scope.filterActivities = function(activity) {
        return !activity.defaultType;
    };

    $scope.showActivityButtons = function() {
        return $scope.data.events.isStarted && !$scope.data.events.isEnded;
    };

    $scope.activityButtonClass = function(activity) {
        if ($scope.data.events.currentAction == null) {
            return "btn-primary";
        }
        if ($scope.data.events.currentAction.key == activity.key) {
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
            date: date.getDate() + "." + (date.getMonth()+1) + "." + date.getFullYear(),
            activity: {
                key: null
            },
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