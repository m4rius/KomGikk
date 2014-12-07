function Activities(activities) {
    this.startDayActivity = null;
    this.endDayActivity = null;
    this.startExtraSession = null;
    this.endExtraSession = null;
    this.activityList = [];
    this.activitiesByKey = {};
    this.defaultActivities = {};

    this.addActivity = function(activity) {
        if (activity.defaultType) {
            this.defaultActivities[activity.defaultType] = activity;
            switch (activity.defaultType) {
                case 'START':
                    this.startDayActivity = activity;
                    break;
                case 'END':
                    this.endDayActivity = activity;
                    break;
                case 'START_EXTRA':
                    this.startExtraSession = activity;
                    break;
                case 'END_EXTRA':
                    this.endExtraSession = activity;
                    break;
                default:
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
}