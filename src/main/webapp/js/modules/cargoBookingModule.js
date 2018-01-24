"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingController",function($rootScope, $scope, $uibModal,cityManager,branchOfficeManager){
        $scope.headline = "Cargo Booking";


        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        cityManager.getActiveCityNames(function (data) {
            $scope.cities = data;
        });

        $scope.copyDetails = function () {
            $scope.cargo.receiverName = $scope.cargo.senderName;
            $scope.cargo.receiverNo = $scope.cargo.senderNo;
        }
        $scope.addCargo = function(){
            if(!$scope.cargos) {
                $scope.cargos = [];
            }
            $scope.cargos.push({'index':$scope.cargos.length+1});
        }

        $scope.deleteCargo = function(cargo){
            $scope.cargos.splice(cargo.index-1,1);
            for(var index=0;index<$scope.cargos.length; index++) {
                $scope.cargos[index].index = index+1;
            }
        }

        $scope.dt = new Date();
    });