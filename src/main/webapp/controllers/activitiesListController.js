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
        }

    });