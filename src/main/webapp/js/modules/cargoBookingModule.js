"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingsController",function($rootScope, $scope, $uibModal,cargoBookingManager, userManager, branchOfficeManager){
        $scope.headline = "Cargo Bookings";

    })

    .controller("CargoBookingController",function($rootScope, $scope, $stateParams, $uibModal,cargoBookingManager, userManager, branchOfficeManager, $location){
        $scope.headline = "Cargo Booking";
        $scope.shipmentTypes = [];
        $scope.users = [];
        $scope.shipment = {'shipmentType':'Paid'};
        $scope.shipment.dispatchDate = new Date();
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });


        $scope.printArea = function() {
            var w=window.open();
            w.document.write(document.getElementsByClassName('report_left_inner')[0].innerHTML);
            w.print();
            w.close();
        }
        if($stateParams.id) {
            cargoBookingManager.getCargoBooking($stateParams.id,function (cargoBooking) {
                $scope.shipment =cargoBooking;
            });
        }
        userManager.getUserNames(function(users){
            $scope.users =users;
        });

        cargoBookingManager.getShipmentTypes(function (types) {
            $scope.shipmentTypes = types;
            //Set the shipment type to paid by default
            var paidType = _.find($scope.shipmentTypes, function(type){
                if(type.shipmentCode === 'P'){
                    return type.id;
                }
            });
            $scope.shipment.shipmentType = paidType.id;
        });

        //set the user to current user
        $scope.shipment.forUser = userManager.getUser().id;

        $scope.copyDetails = function () {
            $scope.shipment.receiverName = $scope.shipment.senderName;
            $scope.shipment.receiverNo = $scope.shipment.senderNo;
        }
        $scope.addItem = function(){
            if(!$scope.shipment.items) {
                $scope.shipment.items = [];
            }
            $scope.shipment.items.push({'index':$scope.shipment.items.length+1});
        }
        $scope.addItem();

        $scope.deleteItem = function(item){
            $scope.shipment.items.splice(item.index-1,1);
            for(var index=0;index<$scope.shipment.items.length; index++) {
                $scope.shipment.items[index].index = index+1;
            }
        }
        $scope.cancelShipmentForm = function() {
            $location.url();
        }
        $scope.dt = new Date();

        $scope.copySenderDetails = function(){
            if($scope.shipment.copySenderDetails){
                $scope.shipment.toContact = $scope.shipment.fromContact;
                $scope.shipment.toName = $scope.shipment.fromName;
                $scope.shipment.toEmail = $scope.shipment.fromEmail;
            }
        }

        $scope.saveCargoBooking = function(){
            cargoBookingManager.createShipment($scope.shipment, function (response) {
                $location.url('cargobooking/'+response.id);
            });
        }

    }).factory('cargoBookingManager', function ($rootScope, $q, $http, $log) {
        return {
            getCargoBooking: function (id, callback) {
                $http.get("/api/v1/shipment/"+id)
                    .then(function (response) {
                        callback(response.data)
                    }, function (error) {
                        swal("oops", error, "error");
                    })
            },
            getShipmentTypes: function ( callback) {
                $http.get("/api/v1/shipment/types")
                    .then(function (response) {
                        callback(response.data)
                    }, function (error) {
                        swal("oops", error, "error");
                    })
            },createShipment: function(cargoBooking, callback){
                $http.post("/api/v1/shipment", cargoBooking)
                    .then(function (response) {
                        callback(response.data)
                    }, function (error) {
                        swal("oops", error, "error");
                    })
            }
        }
    });