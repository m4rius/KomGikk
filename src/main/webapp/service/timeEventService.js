angular.module("komGikkApp")
    .factory("timeEventService", function (activityService) {

        function setEventProperties(scopeData, timeEvent) {

            if (timeEvent.activity.defaultType) {
                switch (timeEvent.activity.defaultType) {
                    case 'START':
                        scopeData.events.isStarted = true;
                        break;
                    case 'END':
                        scopeData.events.isEnded = true;
                        break;
                    case 'START_EXTRA':
                        scopeData.events.isExtraOngoing = true;
                        scopeData.events.currentAction = null;
                        break;
                    case 'END_EXTRA':
                        scopeData.events.isExtraOngoing = false;
                        break;
                    default:
                        break;

                }

            } else {
                scopeData.events.currentAction = timeEvent.activity;
            }
            scopeData.events.list.push(timeEvent);
        }

        return {

            initTimeEvents: function(scopeData) {
                scopeData.events = {};
                scopeData.events.isStarted = false;
                scopeData.events.isEnded = false;
                scopeData.events.isExtraOngoing = false;
                scopeData.events.list = [];
                scopeData.events.currentAction = null;
            },

            addAllTimeEvents: function(scopeData, timeEvents) {
                if (angular.isUndefined(timeEvents) || !angular.isArray(timeEvents)) {
                    console.log("timeEvent is undefined or not an array");
                    return;
                }

                //TODO: sort timeEvents på tid

                //reset previous data
                scopeData.events.isStarted = false;
                scopeData.events.isEnded = false;
                scopeData.events.isExtraOngoing = false;
                scopeData.events.list = [];

                for (var i = 0; i < timeEvents.length; ++i) {
                    setEventProperties(scopeData, timeEvents[i]);
                }
            },

            addNewTimeEvent: function(scopeData, timeEvent) {
                if (angular.isUndefined(scopeData.events.list)) {
                    scopeData.events.list = [];
                }
                setEventProperties(scopeData, timeEvent);
            }
        }


    });