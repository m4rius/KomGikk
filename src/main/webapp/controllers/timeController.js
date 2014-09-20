angular.module("komGikkApp")
    .controller("timeCtrl", function($scope, $http, properties) {


        function postJson(json) {
            $http.post(properties.timeeventUrl, json)
                .success(function(data) {
                    $scope.data.timeevents.push(data);
                })
        }

        $scope.doStart = function() {
            postJson("{\"specialEvent\":\"START\"}");
        };

        $scope.doEnd = function() {
            postJson("{\"specialEvent\":\"END\"}");
        }



    });