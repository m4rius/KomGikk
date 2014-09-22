angular.module("komGikkApp")
    .factory("activityService", function () {

        return {


            addActivity : function(scopeData, activity) {
                if (angular.isUndefined(scopeData.activities)) {
                    scopeData.activities = [];
                }

                if (angular.isUndefined(scopeData.activitiesByKey)) {
                    scopeData.activitiesByKey = {};
                }

                scopeData.activities.push(activity);
                scopeData.activitiesByKey[activity.key] = activity;


            },

            removeActivity: function(scopeDate, activity) {
                var index;
                for (index = 0; index < scopeDate.activities.length ; ++index) {
                    if (scopeDate.activities[index].key == activity.key) {
                        scopeDate.activities.splice(index ,1);
                        break;
                    }
                }

            },

            updateActivity: function(scopeDate, updatedActivity) {
                //remove will remove original (uses key)
                this.removeActivity(scopeDate, updatedActivity);

                //add the updated
                this.addActivity(scopeDate, updatedActivity);

            },

            findActivityByKey: function(activityKey, scopeData) {
                console.log("findActivityByKey");
                var activity = scopeData.activitiesByKey[activityKey];
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