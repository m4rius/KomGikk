angular.module("komGikkApp")
    .factory("properties", function ($http) {

        return {
            activitiesUrl: "/api/activities",
            activityUrl: "/api/activity",
            userUrl: "/api/user",
            timeeventUrl: "/api/timeevent"

        }

    });