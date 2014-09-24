angular.module("komGikkApp")
    .factory("timeEventService", function () {

        function setEventProperties(scopeData, timeEvent) {
            if (timeEvent.specialEvent == 'START') {
                scopeData.events.isStarted = true;
            } else if (timeEvent.specialEvent == 'END') {
                scopeData.events.isEnded = true;
            } else {
                scopeData.events.currentAction = timeEvent.activityKey;
            }

        }

        return {

            initTimeEvents: function(scopeData) {
                scopeData.events = {};
                scopeData.events.isStarted = false;
                scopeData.events.isEnded = false;
                scopeData.events.list = [];
                scopeData.events.currentAction = null;
            },

            addAllTimeEvents: function(scopeData, timeEvents) {
                if (angular.isUndefined(timeEvents) || !angular.isArray(timeEvents)) {
                    console.log("timeEvent is undefined or not an array");
                    return;
                }

                //reset previous data
                scopeData.events.isStarted = false;
                scopeData.events.isEnded = false;
                scopeData.events.list = timeEvents;
                scopeData.events.currentAction = null;

                for (var i = 0; i < timeEvents.length; ++i) {
                    setEventProperties(scopeData, timeEvents[i]);

                }
            },

            addNewTimeEvent: function(scopeDate, timeEvent) {
                setEventProperties(scopeDate, timeEvent);
                if (angular.isUndefined(scopeDate.events.list)) {
                    scopeDate.events.list = [];
                }
                scopeDate.events.list.push(timeEvent);


            }
        }


    });