'use strict';
/*global angular,_*/

angular.module('myBus.vehicleModule', ['ngTable', 'ui.bootstrap'])
    .controller('VehicleController', function ($scope,$rootScope, $state,$http, $log, NgTableParams, $uibModal, $filter,$stateParams, vehicleManager, $location) {
        $log.debug('vehicleController');
        $scope.vehicle = {};
        $scope.count = 0;
        $scope.loading = false;
        $scope.vehiclesCount = 0;
        $scope.currentPageOfVehicles = [];
        $scope.id = $stateParams.id;




        var loadTableData = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            vehicleManager.getVehicles($scope.query,pageable, function(response){
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
            vehicleManager.count($scope.query,function(vehiclesCount){
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
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-vehicle-modal.html',
                controller: 'editVehicleController',
                resolve : {
                    vehicleId : function(){
                        return null;
                    }
                }
            })
        };

        $scope.deleteVehicleOnClick = function(id) {
            vehicleManager.deleteVehicle(id,function(data) {
            })
        };
        $scope.updateVehicleOnClick = function(id) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'update-vehicle-modal.html',
                controller: 'editVehicleController',
                resolve : {
                    vehicleId : function(){
                        return id;
                    }
                }
            })
        }
    })

    .controller('editVehicleController', function ($scope,$rootScope, $http,$log, vehicleManager,vehicleId) {
        $scope.vehicles= [];
        $scope.vehicle = {
            regNo:'',
            description:'',
            vehicleType:''
        };
        if(vehicleId) {
            $scope.setVehicleIntoModal = function (vehicleId) {
                vehicleManager.getVehicleById(vehicleId, function (data) {
                    $scope.vehicle = data;
                    console.log("vehicle ID:" + vehicleId)
                    console.log("vehicle data:" + $scope.vehicle)
                });
            };
            $scope.setVehicleIntoModal(vehicleId);
        }

        $scope.ok = function () {
            if(vehicleId){

                if ($scope.updateVehicleForm.$invalid) {
                    swal("Error!", "Please fix the errors in the form", "error");
                    return;
                }
                vehicleManager.updateVehicle(vehicleId, $scope.vehicle, function (data) {
                    $rootScope.modalInstance.close(data);
                });
            }
            else{
                if($scope.addVehicleForm.$dirty) {
                    if ($scope.addVehicleForm.$invalid) {
                        swal("Error!", "Please fix the errors in the form", "error");
                        return;
                    }
                    else{
                    vehicleManager.createVehicle($scope.vehicle, function(data){
                    $scope.vehicle = data;
                    $rootScope.modalInstance.close(data);
                    });
                    }
                }
            }
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })

    .factory('vehicleManager', function ($rootScope, $http, $log) {
        var vehicles = {}
            , rawChildDataWithGeoMap = {};
        return {
            getVehicles: function (query, pageable, callback) {
                $http({url: '/api/v1/vehicles?query=' + query, method: "GET", params: pageable})
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
            count: function (query,callback) {
                $http.get('/api/v1/vehicle/count')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving vehicles count");
                    });
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
                        $rootScope.$broadcast('reloadVehicleInfo');
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            deleteVehicle: function(id,callback) {
                console.log("deleteVehicle() invoked");
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

