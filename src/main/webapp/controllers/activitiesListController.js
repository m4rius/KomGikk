angular.module("komGikkApp")
    .constant("activityUrl", "http://localhost:9090/api/activity")
    .controller("activitiesListCtrl", function ($scope, $location, $http, activityUrl) {

        $scope.saveNewActivity = function(newActivity) {
            console.log(newActivity);

            $http.post(activityUrl, newActivity)
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
        }

    });