angular.module("komGikkApp")
    .controller("userCtrl", function($scope, $http, $location, properties) {


        $scope.editUser = function(updateUser) {
            $http.post(properties.userUrl, updateUser)
                .error(function(error) {
                    $scope.data.error = error;
                })
                .finally(function() {
                    $location.path("/time");
                })
        }


});