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
        $scope.boardingPoint={};
        console.log('cityId='+ $scope.cityId);
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
            console.log("delete clicked "+ id);
            var modalInstance = $modal.open({
                templateUrl: 'delete-boardingPoint-to-city-state-modal.html',
                controller: 'DeleteBoardingPointController',
                resolve: {
                    deleteCityId: function () {
                        return id;
                    }
                }
            })
        }


       /* $scope.handleClickAddBoardingPoint = function (size) {
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
                $scope.findCity($scope.cityId);
            }, function () {
                $log.debug('Modal dismissed at: ' + new Date());
            });
        };*/
        $scope.handleClickAddBoardingPoint = function () {
            var modalInstance = $modal.open({
                templateUrl: 'add-boardingpoint-to-city-state-modal.html',
                controller: 'AddBoardingPointController',
                resolve: {
                    cityId:function(){
                       return $scope.cityId;
                    },
                    city: function() {
                        return $scope.city;
                    }
                }
            })
        }
    })
    // ========================== Modal - Boarding point controller =================================
    //
    .controller('AddBoardingPointController', function ($scope, $modalInstance, $http, $log, city,cityId, cityManager) {
       /* $scope.boardingPoint = {
            name: null,
            state: null
        };*/
        $scope.boardingPoint = {};
       $scope.city = city;
        $scope.ok = function () {
            if ($scope.boardingPoint.name === null || $scope.boardingPoint.state === null) {
                $log.error("null city or state.  nothing was added.");
                $modalInstance.close(null);
            }
            cityManager. createBordingPoint(cityId,$scope.boardingPoint, function(data){
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.boardingPoint.name || '') !== '' &&
                ($scope.boardingPoint.state || '') !== '';
        };


    })

    //======================Model - DeleteBoardingPointController=============================================
    .controller('DeleteBoardingPointController', function ($scope, $modalInstance, $http, $log, deleteCityId, cityManager) {
        $scope.id = deleteCityId;
        $scope.ok = function (id) {
            cityManager.deleteCity(id, function() {cityManager.fetchAllCities();});
            $modalInstance.close();
        }
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });
