angular.module("komGikkApp")
    .factory("activityService", function () {

        return {

            findActivityByKey: function(activityKey, activities) {
                var index;
                for (index = 0; index < activities.length; ++index) {
                    if (activities[index].key == activityKey) {
                        return activities[index].name;
                    }
                }
                console.log("unable to find activity with key " + activityKey);
                return "missing activity";
            },

            //TODO grisetet logikk
            isStarted: function(timeEvents) {
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