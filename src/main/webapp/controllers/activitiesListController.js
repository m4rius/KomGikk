angular.module("komGikkApp")
    .controller("activitiesListCtrl", function ($scope, $location, $http, properties) {

        $scope.saveNewActivity = function(newActivity) {
            $http.post(properties.activityUrl, newActivity)
                .success(function(data) {
                    $scope.data.activities.push(newActivity);
                    $scope.data.newactivity = null;
                })
                .error(function(error) {
                    $scope.data.activityStoreError = error;
                })
                .finally(function() {
                    $location.path("/activities");
                });
        };

        $scope.deleteActivity = function(activity) {
            //$http.delete(properties.activityUrl, activity)
            //todo bruk delete
            activity.action = "delete";
            $http.post(properties.activityUrl, activity)
                .success(function(data) {
                    var index = -1;
                    for (var i = 0; i < $scope.data.activities.length; i++) {
                        if ($scope.data.activities[i].key == activity.key) {
                            index = i;
                        }
                    }
                    if (index >= 0) {
                        $scope.data.activities.splice(index, 1);
                    }
                })

        }



    });