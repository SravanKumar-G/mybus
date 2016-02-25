/**
 * Created by SREEDHAR on 2/15/2016.
 */
'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');
portalApp.factory('vehicleManager', function ($rootScope, $http, $log, $window) {
    var vehicles = {}
        , rawChildDataWithGeoMap = {};
    return {
        fetchAllVehicles: function () {
            $log.debug("fetching vehicles data ...");
            $http.get('/api/v1/vehicles')
                .success(function (data) {
                    vehicles = data;
                    $rootScope.$broadcast('vehicleInitComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving vehicles");
                });
        },
        getVehicles: function (callback) {
            $log.debug("fetching vehicles data ...");
            $http.get('/api/v1/vehicles')
                .success(function (data) {
                    callback(data);
                })
                .error(function (error) {
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
            $http.post('/api/v1/vehicle', vehicle).success(function (data) {
                callback(data);
                swal("Great", "Your vehicle has been successfully added", "success");
            }).error(function (err,status) {
                sweetAlert("Error",err.message,"error");

            });
        },
        getVehicleById: function (id,callback) {
            $log.debug("fetching vehicle data ...");
            $http.get('/api/v1/vehicle/'+id)
                .success(function (data) {
                    callback(data);
                })
                .error(function (err,status) {
                    sweetAlert("Error",err.message,"error");
                });
        },
        updateVehicle: function (id,vehicle,callback) {
            $log.debug("fetching vehicle data ...");
            $http.put('/api/v1/vehicle/'+id,vehicle)
                .success(function (data) {
                    callback(data);
                    sweetAlert("Great","Your vehicle has been successfully updated", "success");
                    $log.debug(" vehicle data is updated ...");
                })
                .error(function (err,status) {
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
                    .success(function (data) {
                        callback(data);
                        sweetAlert("Great", "Your Vehicle has been successfully deleted", "success");
                    })
                    .error(function (error) {
                        sweetAlert("Oops...", "Error finding vehicle data!", "error" + angular.toJson(error));
                    });
            });
        },
    }

})