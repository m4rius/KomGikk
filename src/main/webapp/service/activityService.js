angular.module("komGikkApp")
    .factory("activityService", function () {

        return {


            addActivity : function(data, activity) {
                if (angular.isUndefined(data.activities)) {
                    data.activities = [];
                }

                if (angular.isUndefined(data.activitiesByKey)) {
                    data.activitiesByKey = {};
                }

                data.activities.push(activity);
                data.activitiesByKey[activity.key] = activity;


            },

            findActivityByKey: function(activityKey, data) {
                console.log("findActivityByKey")
                var activity = data.activitiesByKey[activityKey];
                if (angular.isUndefined(activity)) {
                    return "missing activity";
                }
                return activity;
            },

            //TODO grisetet logikk
            isStarted: function(timeEvents) {

                if (angular.isUndefined(timeEvents)) {
                    return false;
                }

                var index;

                var stated = false;
                var ended = false;
                for (index = 0; index < timeEvents.length; ++index) {
                    var sEvent = timeEvents[index].specialEvent;

                    if (!angular.isUndefined(sEvent)) {
                        if (sEvent == 'START') {
                            stated = true;
                        } else {
                            ended = true;
                        }
                    }

                }

                if (stated && !ended) {
                    return true;
                }

                return false;
            }



        }
    });