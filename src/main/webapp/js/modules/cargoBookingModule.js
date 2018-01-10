"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
.controller("CargoBookingController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,paginationService){
    $scope.headline = "Cargo Booking"
});