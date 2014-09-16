angular.module("komGikkApp")
    .constant("userUrl", "http://localhost:9090/api/user")
    .controller("userCtrl", function($scope, $http, $location, userUrl) {


        $scope.editUser = function(updateUser) {
            $http.post(userUrl, updateUser)
                .error(function(error) {
                    $scope.data.error = error;
                })
                .finally(function() {
                    $location.path("/time");
                })
        }


});