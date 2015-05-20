/**
 * Created by skandula on 5/19/15.
 */

'use strict';
/*global angular,_*/

angular.module('myBus.boardingPointModule', [])
    .controller('BoardingPointsListController', function($scope,$routeParams, $log, $http, userManager) {
        $scope.headline = "Boarding points";

        $scope.user = {};
        $scope.groups = [];
        $scope.cityId = $routeParams.id;
        $scope.city = {};

        $scope.findCity = function (id) {
            if (!id) {
                var errorMsg = "no id was specified.  city can not be found.";
                $log.error(errorMsg);
                alert(errorMsg);
                return;
            }
            $http.get('/api/v1/city/' + id)
                .success(function (data) {
                    $scope.city = data;
                })
                .error(function (error) {
                    alert("error finding city. " + angular.toJson(error));
                });
        };
        $scope.findCity($scope.cityId);
    });