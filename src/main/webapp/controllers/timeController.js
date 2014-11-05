var app = angular.module("komGikkApp");

app.controller("timeCtrl", function($scope, $http, $filter, properties, activityService, timeEventService) {

    var prevDate;
    var selectedDate;
    var nextDate;

    function getEvents(year, month, day) {

        $http.get(properties.timeeventUrl + "/list/" + year + "/" + month + "/" + day)
            .success(function(returnValue) {
                timeEventService.addAllTimeEvents($scope.data, returnValue.events);
                prevDate = new Date(returnValue.prevDate.year, returnValue.prevDate.month-1, returnValue.prevDate.day);
                selectedDate = new Date(returnValue.selectedDate.year, returnValue.selectedDate.month-1, returnValue.selectedDate.day);
                if (returnValue.nextDate) {
                    nextDate = new Date(returnValue.nextDate.year, returnValue.nextDate.month-1, returnValue.nextDate.day);
                }

                console.log("PrevDate: " + prevDate);
                console.log("SelectedDate: " + selectedDate);
            })
            .error(function (error) {
                console.log("Feil ved henting av timeevents: " + error);
                $scope.data.error = error;
            });
    }

    getEvents(0, 0, 0);

    function postNewTimeEvent(json) {
        $http.post(properties.timeeventUrl, json)
            .success(function(returnValue) {
                timeEventService.addNewTimeEvent($scope.data, returnValue);
            })
    }

    function leftPad(s, pad) {
        var str = "" + s;
        return pad.substring(0, pad.length - str.length) + str

    }

    $scope.showPreviousDay = function() {
        getEvents(prevDate.getFullYear(), prevDate.getMonth()+1, prevDate.getDate());
    };

    $scope.showNextDay = function() {
        getEvents(nextDate.getFullYear(), nextDate.getMonth()+1, nextDate.getDate());
    };

    $scope.showNextDayButton = function() {
        return !!nextDate;
    };

    $scope.verboseToday = function() {
        if (selectedDate) {
            var dager = ['Søndag', 'Mandag', 'Tirsdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lørdag'];
            return dager[selectedDate.getDay()] + " " + leftPad(selectedDate.getDate(), '00') + "." + leftPad(selectedDate.getMonth()+1, '00');
        }
    };

    $scope.doStart = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.startDayActivity.key + "\"}");
    };

    $scope.doStartExtra = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.startExtraSession.key + "\"}");
    };

    $scope.doEnd = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.endDayActivity.key + "\"}");
    };

    $scope.doEndExtra = function() {
        postNewTimeEvent("{\"key\":\"" + $scope.data.activities.endExtraSession.key + "\"}");
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

    $scope.showStartExtra = function() {
        return $scope.data.events.isEnded && !$scope.data.events.isExtraOngoing;
    };

    $scope.showEndExtra = function() {
        return $scope.data.events.isEnded && $scope.data.events.isExtraOngoing;
    };

    $scope.filterActivities = function(activity) {
        return !activity.defaultType;
    };

    $scope.showActivityButtons = function() {
        return ($scope.data.events.isStarted && !$scope.data.events.isEnded) || $scope.data.events.isExtraOngoing;
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
        var date = selectedDate;
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

    $scope.rowClass = function(event) {
        if (event.activity.defaultType == 'START_EXTRA') {
            return "thickRow";
        }
    }

});