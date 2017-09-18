'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', ['ngTable', 'ui.bootstrap'])
    .controller('VehicleController', function ($scope,$rootScope, $state,$http, $log,paginationService, NgTableParams, $uibModal, $filter,$stateParams, vehicleManager, $location) {
        $log.debug('vehicleController');
        $scope.vehicle = {};
        $scope.count = 0;
        $scope.loading = false;
        $scope.vehiclesCount = 0;
        $scope.currentPageOfVehicles = [];
        $scope.id = $stateParams.id;
        var pageable ;

        var loadTableData = function (tableParams) {
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            $scope.loading = true;
            vehicleManager.getVehicles(pageable, function(response){
                if(angular.isArray(response.content)){
                    $scope.loading = false;
                    $scope.vehicles = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.vehicles;
                    $scope.currentPageOfVehicles = $scope.vehicles;
                }
            })
        };

        $scope.init = function(){
            vehicleManager.count(function(vehiclesCount){
                $scope.vehicleContentTableParams = new NgTableParams(
                    {
                        page: 1,
                        size: 10,
                        count: 10,
                        sorting: {
                            regNo: 'asc'
                        }
                    },
                    {
                        counts:[],
                        total: vehiclesCount,
                        getData: function (params) {
                            loadTableData(params);
                        }
                    }
                )
            })
        };
        $scope.init();

        $scope.$on('reloadVehicleInfo',function(e,value){
            $scope.init();
        });

        $scope.addVehicleOnClick = function (){
            $state.go('createvehicle');
        };

        $scope.deleteVehicleOnClick = function(id) {
            vehicleManager.deleteVehicle(id,function(data) {
            });
        };
        $scope.updateVehicleOnClick = function(id) {
            //$state.go('vehicle/'+id);
            $location.url('vehicle/'+id);
        };
    })

    .controller('EditVehicleController', function ($scope,$state,$stateParams, $rootScope, $http,$log, vehicleManager) {
        $scope.headline = "Vehicle";
        $scope.vehicles= [];
        console.log('edit called');
        $scope.vehicleId = $stateParams.id;
        if($scope.vehicleId){
            vehicleManager.getVehicleById($scope.vehicleId, function(vehicle) {
                $scope.vehicle = vehicle;
            });
        } else {
            $scope.vehicle = {
                insuranceExpiry: new Date(),
                permitExpiry : new Date(),
                fitnessExpiry : new Date(),
                pollutionExpiry : new Date()
            };
        }

        $scope.save = function(){
            vehicleManager.createVehicle($scope.vehicle, function (data) {
                $state.go('vehicles');
            }, function (error) {
                console.log('error');
            });
        }
        $scope.cancel = function() {
            $state.go('vehicles');
        }
    }).factory('vehicleManager', function ($rootScope, $http, $log) {
        var vehicles = {}
            , rawChildDataWithGeoMap = {};
        return {
            getVehicles: function ( pageable, callback) {
                $http({url: '/api/v1/vehicles', method: "GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function(error){
                        swal("oops", error, "error");
                    });
            },

            getAllData: function () {
                return vehicles;
            },
            getAllVehicles: function () {
                return vehicles;
            },
            count: function (callback) {
                $http.get('/api/v1/vehicle/count',{})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving vehicles count");
                    });
            },
            createVehicle: function (vehicle, callback, error) {
                console.log();
                $http.post('/api/v1/vehicle', vehicle).then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast("reloadVehicleInfo");
                    swal("Great", "Your vehicle has been successfully added", "success");
                },function (err,status) {
                    sweetAlert("Error Saving Vehicle info",err.data.message,"error");
                });
            },
            getVehicleById: function (id,callback) {
                $log.debug("fetching vehicle data ...");
                $http.get('/api/v1/vehicle/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            updateVehicle: function (id,vehicle,callback) {
                $log.debug("fetching vehicle data ...");
                $http.put('/api/v1/vehicle/'+id,vehicle)
                    .then(function (response) {
                        callback(response.data);
                        sweetAlert("Great","Your vehicle has been successfully updated", "success");
                        $log.debug(" vehicle data is updated ...");
                        $rootScope.$broadcast('reloadVehicleInfo');
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            deleteVehicle: function(id,callback) {
                swal({   title: "Are you sure?",   text: "You will not be able to recover this vehicle !",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false }, function() {
                    $http.delete('/api/v1/vehicle/' + id)
                        .then(function (response) {
                            callback(response.data);
                            sweetAlert("Great", "Your Vehicle has been successfully deleted", "success");
                            $rootScope.$broadcast('reloadVehicleInfo');
                        },function (error) {
                            sweetAlert("Oops...", "Error finding vehicle data!", "error" + angular.toJson(error));
                        });
                });
            },
        }

    });

