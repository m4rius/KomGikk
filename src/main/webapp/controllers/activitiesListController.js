angular.module("komGikkApp")
    .controller("activitiesListCtrl", function ($scope, $location, $http, properties, activityService) {

        $scope.saveNewActivity = function(newActivity) {
            $http.post(properties.activityUrl, newActivity)
                .success(function(returnValue) {
                    newActivity.key = returnValue.key;
                    $scope.data.newactivity = null;

                    activityService.addActivity($scope.data, newActivity);
                })
                .error(function(error) {
                    $scope.data.activityStoreError = error;
                })
                .finally(function() {
                    $location.path("/activities");
                });

        };

        $scope.cancel = function() {
            $scope.data.newactivity = null;
            $location.path("/activities");

        };

        $scope.updateActivity = function(updatedActivity) {
            $http.put(properties.activityUrl, updatedActivity)
                .success(function(returnValue) {
                    activityService.updateActivity($scope.data, returnValue);

                })
                .error(function(error) {
                    console.log("Feil ved oppdatering av aktivitet: " + error);
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
                    activityService.removeActivity($scope.data, activity);
                })
        };

        $scope.selectActivityForUpdate = function(activity) {
            $scope.data.activityToUpdate = activity;
        }



    });