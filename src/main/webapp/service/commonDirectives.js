angular.module("komGikkApp")
    .directive('confirmationNeeded', function () {
    return {
        priority: 1,
        terminal: true,
        link: function (scope, element, attr) {
            var msg = attr.confirmationNeeded || "Er du sikker?";
            var clickAction = attr.ngClick;
            element.bind('click',function () {
                if ( window.confirm(msg) ) {
                    scope.$eval(clickAction)
                }
            });
        }
    };
});