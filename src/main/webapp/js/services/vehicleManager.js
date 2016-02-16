/**
 * Created by SREEDHAR on 2/15/2016.
 */
'use strict';
/*global angular, _*/

/*var portalApp = angular.module('myBus');
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
        getAllvehicles: function () {
            return vehicles;
        },
    }
})*/

var portalApp = angular.module('myBus');
portalApp.factory('vehicleManager', function ($http, $log) {

    return {
        loadVehicles: function (callback) {
            $http.get('/api/v1/vehicles')
                .success( function(data){
                   callback(data);
console.log("after callback...........");
                }).error(function () {
                    alert("Error getting the data from the server");
                });

        },

        createVehicles: function (vehicle, callback) {
            $http.post('/api/v1/vehicle', vehicle).success(function (data) {
                callback(data);
            }).error(function () {
                alert("Error saving the data");

            });
        }
    }
});