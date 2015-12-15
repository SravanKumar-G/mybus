/**
 * Created by skandula on 5/19/15.
 */

'use strict';
/*global angular,_*/

angular.module('myBus.boardingPointModule', [])
    .controller('BoardingPointsListController', function($scope,$routeParams, $log, $modal, userManager, cityManager) {
        $scope.headline = "Boarding Points";
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
            cityManager.getCity(id, function(city) {
                $scope.city = city;
            });
        };
        $scope.findCity($scope.cityId);
        $scope.handleDeleteButtonClicked = function(id) {
            cityManager.deleteCity(id, function() {cityManager.fetchAllCities();})
        }
        $scope.handleClickAddBoardingPoint = function (size) {
             var modalInstance = $modal.open({
                templateUrl: 'add-boardingpoint-to-city-state-modal.html',
                controller: 'AddBoardingPointController',
                size: size,
                resolve: {
                    neighborhoodId: function () {
                        return null;
                    },
                    city: function() {
                        return $scope.city;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $log.debug("results from modal: " + angular.toJson(data));
                citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
            }, function () {
                $log.debug('Modal dismissed at: ' + new Date());
            });
        };
    })// ========================== Modal - Boarding point controller =================================
    //
    .controller('AddBoardingPointController', function ($scope, $modalInstance, $http, $log, city, citiesManager) {
        $scope.boardingPoint = {
            name: null,
            state: null
        };
        $scope.city = city;
        $scope.ok = function () {
            if ($scope.boardingPoint.name === null || $scope.boardingPoint.state === null) {
                $log.error("null city or state.  nothing was added.");
                $modalInstance.close(null);
            }


        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.boardingPoint.name || '') !== '' &&
                ($scope.boardingPoint.state || '') !== '';
        };


    });