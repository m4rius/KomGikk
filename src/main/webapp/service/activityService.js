angular.module("komGikkApp")
    .factory("activityService", function () {

        function Activities(activities) {
            this.startDayActivity = null;
            this.endDayActivity = null;
            this.activityList = [];
            this.activitiesByKey = {};

            this.addActivity = function(activity) {
                if (activity.defaultType) {
                    switch (activity.defaultType) {
                        case 'START':
                            this.startDayActivity = activity;
                            activity.name = 'Kom';
                            break;
                        case 'END':
                            this.endDayActivity = activity;
                            activity.name = 'Gikk';
                            break;
                    }
                }
                this.activityList.push(activity);
                this.activitiesByKey[activity.key] = activity;
            };

            this.removeActivity = function(activity) {
                for (var i = 0; i < this.activityList.length; ++i) {
                    if (this.activityList[i].key == activity.key) {
                        this.activityList.splice(i ,1);
                        break;
                    }
                    //activitiesByKey er strengt talt ikke nødvendig å fjerne fra.
                }
            };

            this.getActivityByKey = function(activityKey) {
                return this.activitiesByKey[activityKey];
            };

            for (var i = 0; i < activities.length; ++i) {
                this.addActivity(activities[i]);
            }

            console.log("Activities initiert");
            console.log("Listen har " + this.activityList.length + " elementer");
            console.log("Start day " + this.startDayActivity.key);
            console.log("End day " + this.endDayActivity.key);

        }

        return {

            addActivity : function(scopeData, activity) {
                scopeData.activities.addActivity(activity);
            },

            addAllActivities: function(scopeData, activities) {
                scopeData.activities = new Activities(activities);
                console.log("Activities i scope data " + scopeData.activities);
                console.log("Activities list i scope data " + scopeData.activities.activityList);
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