'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', ['ngTable', 'ui.bootstrap'])
    .controller('VehicleController', function ($scope, $http, $log, ngTableParams, $modal, $filter, vehicleManager, $location) {
        console.log("in vehicleController");
        $scope.vehicles=[];
        $scope.displayVehicles = function(data){
            $scope.vehicles = data;
        };

        $scope.loadVehicles = function () {
            vehicleManager.loadVehicles($scope.displayVehicles);
        };

        $scope.loadVehicles();


      /*  $log.debug('vehicleController');
        console.log("in vehicleController..............");
       //$scope.headline = "Vehicle List";
      //  $scope.vehicleId = $routeParams.id;
        $scope.currentPageOfVehicles = [];
        var loadTableData = function (tableParams, $defer) {
            var data = vehicleManager.getAllvehicles();
                $scope.vehicles = data;
                console.log("found vehicle" + angular.toJson($scope.vehicles));
                var orderedData = tableParams.sorting() ? $filter('orderBy')($scope.vehicles, tableParams.orderBy()) : $scope.vehicles;
                tableParams.total($scope.vehicles.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfVehicles = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
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
        })*/
    });