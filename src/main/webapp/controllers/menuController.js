angular.module("komGikkApp")
    .constant("menuActiveCLass", "active")
    .controller("menuCtrl", function($scope, menuActiveCLass) {

        $scope.menu = [
            {id: 1, name: "Registrere tid", url: "#/time"},
            {id: 2, name: "Aktivteter", url: "#/activities"},
            {id: 3, name: "Ukeliste", url: "#/summary"},
            {id: 4, name: "Profil", url: "#/user"}
        ];

        var selectedMenuItem = 1;

        $scope.selectMenu = function (menuItem) {
            if (angular.isUndefined(menuItem)) {
                selectedMenuItem = 1;
            } else {
                selectedMenuItem = menuItem.id;
            }

        };

        $scope.getMenuClass = function (menuItem) {
            return selectedMenuItem == menuItem.id ? menuActiveCLass : "";
        }

    });