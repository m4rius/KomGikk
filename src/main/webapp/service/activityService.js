angular.module("komGikkApp")
    .factory("activityService", function () {

        return {

            addActivity : function(scopeData, activity) {
                scopeData.activities.addActivity(activity);
            },

            addAllActivities: function(scopeData, activities) {
                scopeData.activities = new Activities(activities);
            },

            removeActivity: function(scopeDate, activity) {
                scopeDate.activities.removeActivity(activity);

            },

            updateActivity: function(scopeDate, updatedActivity) {
                //remove will remove original (uses key)
                scopeDate.activities.removeActivity(updatedActivity);

                //add the updated
                scopeDate.activities.addActivity(updatedActivity);

            },

            findActivityByKey: function(activityKey, scopeData) {
                scopeData.activities.getActivityByKey(activityKey);
            }
        }
    });