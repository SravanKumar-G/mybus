'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', [])
    .controller('VehicleController', function ($scope, $state,$http, $log, NgTableParams, $modal, $filter,$stateParams, vehicleManager, $location) {
        $log.debug('vehicleController');
        $scope.currentPageOfVehicles = [];
        $scope.id = $stateParams.id;
        var loadTableData = function (tableParams, $defer) {
            var data=vehicleManager.getVehicles(function(data) {
                $scope.vehicles = data;
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total(data.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfVehicles = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.vehicleContentTableParams = new NgTableParams({
            page: 1,
            count: 25,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfVehicles.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $scope.$on('reloadVehicleInfo',function(e,value){
            loadTableData($scope.vehicleContentTableParams)
        });
        $scope.addVehicleOnClick = function () {
            $state.go('createvehicle');
        };
        $scope.deleteVehicleOnClick = function(id) {
            vehicleManager.deleteVehicle(id,function(data) {
                //$route.reload();
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
    .controller('AddVehicleController', function ($scope, $modalInstance, $http,$log, vehicleManager) {
        $scope.vehicle = {};
        $scope.ok = function () {
            vehicleManager.createVehicle($scope.vehicle, function(data){
                $modalInstance.close(data);

                //$route.reload();
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
    // ========================== Modal - editVehicle controller =================================
    .controller('EditVehicleController', function ($scope, $http,$log, vehicleManager) {
        $scope.vehicle = {};
        $scope.dateOptions = {
            dateDisabled: 'disabled',
            formatYear: 'yy',
            maxDate: new Date(2020, 5, 22),
            minDate: new Date(),
            startingDay: 1
        };
        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };
        $scope.popup1 = {
            opened: false
        };
        $scope.ok = function () {
            vehicleManager.createVehicle($scope.vehicle, function(data){
                $modalInstance.close(data);

                //$route.reload();
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
    .controller('UpdateVehicleModalController', function ($scope, $modalInstance, $http,$log,vehicleId, vehicleManager) {
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
                //$route.reload();
            })
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }).factory('vehicleManager', function ($rootScope, $http, $log) {
        var vehicles = {}
            , rawChildDataWithGeoMap = {};
        return {
            fetchAllVehicles: function () {
                $log.debug("fetching vehicles data ...");
                $http.get('/api/v1/vehicles')
                    .then(function (response) {
                        vehicles = response.data;
                        $rootScope.$broadcast('vehicleInitComplete');
                    },function (error) {
                        $log.debug("error retrieving vehicles");
                    });
            },
            getVehicles: function (callback) {
                $log.debug("fetching vehicles data ...");
                $http.get('/api/v1/vehicles')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving vehicles");
                    });
            },
            getAllData: function () {
                return vehicles;
            },
            getAllVehicles: function () {
                return vehicles;
            },
            createVehicle: function (vehicle, callback) {
                $http.post('/api/v1/vehicle', vehicle).then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast("reloadVehicleInfo");
                    swal("Great", "Your vehicle has been successfully added", "success");
                },function (err,status) {
                    sweetAlert("Error",err.message,"error");

                });
            },
            getVehicleById: function (id,callback) {
                $log.debug("fetching vehicle data ...");
                $http.get('/api/v1/vehicle/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            updateVehicle: function (id,vehicle,callback) {
                $log.debug("fetching vehicle data ...");
                $http.put('/api/v1/vehicle/'+id,vehicle)
                    .then(function (response) {
                        callback(response.data);
                        sweetAlert("Great","Your vehicle has been successfully updated", "success");
                        $log.debug(" vehicle data is updated ...");
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            deleteVehicle: function(id,callback) {
                console.log("deleteVehicle() invoked");
                swal({   title: "Are you sure?",   text: "You will not be able to recover this vehicle !",   type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, delete it!",
                    closeOnConfirm: false }, function() {
                    $http.delete('/api/v1/vehicle/' + id)
                        .then(function (response) {
                            callback(response.data);
                            sweetAlert("Great", "Your Vehicle has been successfully deleted", "success");
                        },function (error) {
                            sweetAlert("Oops...", "Error finding vehicle data!", "error" + angular.toJson(error));
                        });
                });
            },
        }

    });

