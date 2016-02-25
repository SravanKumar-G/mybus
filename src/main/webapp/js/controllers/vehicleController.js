'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', [])
    .controller('VehicleController', function ($scope, $http, $log, ngTableParams, $modal, $filter,$route,$routeParams, vehicleManager, $location) {
        $log.debug('vehicleController');
        $scope.currentPageOfVehicles = [];
        $scope.id = $routeParams.id;
        var loadTableData = function (tableParams, $defer) {
            var data=vehicleManager.getVehicles(function(data) {
                $scope.vehicles = data;
                console.log("found city"+angular.toJson(data));
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total(data.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfVehicles = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.vehicleContentTableParams = new ngTableParams({
            page: 1,
            count: 25,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfVehicles.length,

            getData: function ($defer, params) {
                loadTableData(params);
            }
        });
        $scope.addVehicleOnClick = function () {
            var modalInstance = $modal.open({
                templateUrl: 'add-vehicle-modal.html',
                controller: 'AddVehicleController',
            })
        };
        $scope.deleteVehicleOnClick = function(id) {
            vehicleManager.deleteVehicle(id,function(data) {
                $route.reload();
            })
        }
        $scope.updateVehicleOnClick = function(id) {
            var modalInstance = $modal.open({
                templateUrl: 'update-vehicle-modal.html',
                controller: 'UpdateVehicleModalController',
                resolve : {
                    vehicleId : function(){
                        return id;
                    }
                }
            })
        }
    })
    // ========================== Modal - addVehicle controller =================================
    .controller('AddVehicleController', function ($scope, $modalInstance, $http,$log,$route, vehicleManager) {
        $scope.vehicle = {};
        $scope.ok = function () {
            vehicleManager.createVehicle($scope.vehicle, function(data){
                $modalInstance.close(data);
                $route.reload();
            });
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.vehicle.type || '') !== '' &&
                ($scope.vehicle.description || '') !== '' &&
                ($scope.vehicle.regNo || '') !== '';
        };
    })
    // ========================== Modal - updateVehicle controller =================================
    .controller('UpdateVehicleModalController', function ($scope, $modalInstance, $http,$log,$route,vehicleId, vehicleManager) {
        $scope.vehicle = {};
        $scope.setVehicleIntoModal = function(vehicleId){
            vehicleManager.getVehicleById(vehicleId,function(data){
                $scope.vehicle = data;
                console.log("vehicle ID:"+ vehicleId)
                console.log("vehicle data:"+ $scope.vehicle)
            });
        };
        $scope.setVehicleIntoModal(vehicleId);
        $scope.ok = function () {
            vehicleManager.updateVehicle(vehicleId,$scope.vehicle,function(data){
                $modalInstance.close(data);
                $route.reload();
            })
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })

