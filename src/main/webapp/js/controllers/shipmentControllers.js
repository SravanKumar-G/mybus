"use strict";
/*global angular, _*/

angular.module('myBus.shipmentModule', ['ngTable', 'ui.bootstrap'])

  //
  // ============================= List All ===================================
  //
    .controller('ShipmentsController', function($scope,$state, $http, $log, $filter, NgTableParams, $location, usSpinnerService,userManager) {
        $scope.headline = "Shipments";
        $scope.shipmentCount = 0;
        $scope.addShipment = function () {
            $state.go('editshipment');
        };
    })

    //
    // ============================= Add/Edit ========================================
    //
    .controller('EditShipmentController', function($scope,userManager,$window,$log,agentPlanManager, roleManager,cancelManager ) {
        $scope.headline = "Edit Shipment";

    });



