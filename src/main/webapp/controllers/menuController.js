angular.module("komGikkApp")
    .constant("menuActiveCLass", "active")
    .controller("menuCtrl", function($scope, menuActiveCLass) {

        $scope.menu = [
            {id: 1, name: "Registrere tid", url: "#/time"},
            {id: 2, name: "Aktivteter", url: "#/activities"},
            {id: 3, name: "Ukeliste", url: "#/summary"}
        ];

        var selectedMenuItem = 1;

        $scope.selectMenu = function (menuItem) {
            selectedMenuItem = menuItem.id;
        };

        $scope.getMenuClass = function (menuItem) {
            return selectedMenuItem == menuItem.id ? menuActiveCLass : "";
        }

    });