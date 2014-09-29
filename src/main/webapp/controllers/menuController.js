angular.module("komGikkApp")
    .constant("menuActiveCLass", "active")
    .controller("menuCtrl", function($scope, $http, menuActiveCLass, properties) {



        $scope.menu = [
            {id: 1, name: "Registrere aktivitet", url: "#/time"},
            {id: 3, name: "Ukeliste", url: "#/summary"},
            {id: 4, name: "Innstillinger", url: "#/settings"}
        ];

        var selectedMenuItem = 1;

        $scope.selectMenu = function (menuItem) {
            if (angular.isUndefined(menuItem)) {
                selectedMenuItem = 0;
            } else {
                selectedMenuItem = menuItem.id;
            }

        };

        $scope.getMenuClass = function (menuItem) {
            return selectedMenuItem == menuItem.id ? menuActiveCLass : "";
        };

        $http.get("/api/properties")
            .success(function(data) {
                $scope.logoutUrl = data.logoutUrl;
            });

        $scope.doLogout = function() {
            window.location = $scope.logoutUrl;
        };

        $scope.userIconClass = function() {
          if (!$scope.username ) {
              return "text-danger";
          }

        };

        $scope.$watch('username', function() {
            if ($scope.username) {
                if ($scope.username != $scope.data.user.name) {
                    $scope.data.user.name = $scope.username;
                    $http.post(properties.userUrl, $scope.data.user)
                        .error(function(error) {
                            $scope.data.error = error;
                        })
                }
            }
        });

    });