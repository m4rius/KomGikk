angular.module("komGikkApp")
    .controller("sumCtrl", function($scope, $http, properties) {

        function getSummary(year, week) {
            $http.get(properties.summaryUrl + "/" + year + "/" + week)
                .success(function(returnValue) {
                    $scope.summary = returnValue;
                })
        }


        getSummary(0, 0);

        $scope.showPreviousWeek = function() {
            var week = $scope.summary.week - 1;
            var year = $scope.summary.year;

            //todo handle turn off year

            getSummary(year, week)
        };

        $scope.showNextWeek = function() {
            var week = $scope.summary.week + 1;
            var year = $scope.summary.year;

            //todo handle turn off year

            getSummary(year, week)
        }

    });