"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingListController",function($rootScope, $scope, NgTableParams, cargoBookingManager,$location){
        $scope.headline = "Cargo Bookings";
        $scope.cargoBookings = null;
        $scope.cargoBookingsTable = null;

        var loadCargoBookings = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            cargoBookingManager.findCargoBookings(pageable, function(cargoBookings){
                if(angular.isArray(cargoBookings)) {
                    $scope.loading = false;
                    tableParams.data = cargoBookings;
                    $scope.cargoBookings = cargoBookings;
                }
            });
        };
        $scope.init = function() {
            cargoBookingManager.count(function(shipmentCount){
                $scope.cargoBookingsTable = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[10,50,100],
                    total:shipmentCount,
                    getData: function (params) {
                        loadCargoBookings(params);
                    }
                });
            });
        };
        $scope.init();
        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/'+bookingId);
        }

    }).controller("CargoBookingLookupController",function($rootScope, $scope, $uibModal,cargoBookingManager, userManager, bookings){
        $scope.headline = "Cargo Bookings";
        $scope.bookings = bookings;
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .controller("CargoBookingHeaderController",function($rootScope, $scope,cargoBookingManager,$location){
        $scope.headline = "Cargo Bookings";
        $scope.bookingId;
        $scope.search = function() {
            if(!$scope.bookingId) {
                sweetAlert("Error","Enter the bookingId for search","error");
                return;
            }
            cargoBookingManager.lookupCargoBooking($scope.bookingId);
        }

        $scope.newBooking = function(){
            $location.url('newbooking');
        }
    })

    .controller("CargoBookingController",function($rootScope, $scope, $stateParams, $uibModal,cargoBookingManager, userManager, branchOfficeManager, $location){
        console.log('CargoBookingController ....');
        $scope.headline = "Cargo Booking";
        $scope.shipmentTypes = [];
        $scope.users = [];
        $scope.shipment = {'shipmentType':'Paid'};
        $scope.shipment.dispatchDate = new Date();
        $scope.filter;
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        $scope.getTotalPrice = function () {
            $scope.shipment.totalCharge = 0;
            for(var index=0;index<$scope.shipment.items.length; index++) {
                if($scope.shipment.items[index].charge){
                    $scope.shipment.totalCharge +=  parseFloat($scope.shipment.items[index].charge);
                }
            }
            if($scope.shipment.loadingCharge) {
                $scope.shipment.totalCharge +=  parseFloat($scope.shipment.loadingCharge);
            }
            if($scope.shipment.unloadingCharge) {
                $scope.shipment.totalCharge +=  parseFloat($scope.shipment.unloadingCharge);
            }
            if($scope.shipment.otherCharge) {
                $scope.shipment.totalCharge +=  parseFloat($scope.shipment.otherCharge);
            }
            return $scope.shipment.totalCharge;
        }
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
                $location.url('viewcargobooking/'+response.id);
            });
        }

        $scope.lookUpCargoBooking = function(){
            if(!this.filter) {
                swal("Error", "Search text is missing", "error");
            } else{
                cargoBookingManager.lookupCargoBooking(this.filter);
                /*cargoBookingManager.findCargoBookings(this.filter, function (response) {
                    if(response.length == 0) {
                        swal("oops", "Nothing found!!", "error");
                    } else {
                        $rootScope.modalInstance = $uibModal.open({
                            templateUrl : 'cargobookings-modal.html',
                            controller : 'CargoBookingLookupController',
                            resolve : {
                                bookings : function(){
                                    return response;
                                }
                            }
                        });
                    }
                });*/
            }
        }

    }).factory('cargoBookingManager', function ($rootScope, $q, $http, $log, $location) {
        return {
            findCargoBookings: function (pageable, callback) {
                $http({url:'/api/v1/shipments',method:"POST", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
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
            },createShipment: function(cargoBooking, successcallback){
                $http.post("/api/v1/shipment", cargoBooking)
                    .then(function (response) {
                        successcallback(response.data)
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    })
            },
            count:function (callback) {
                $http({url:'/api/v1/shipments/count',method:"POST"})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving shipments count");
                    });
            },
            lookupCargoBooking : function(LRNumber) {
                $http.get("/api/v1/shipment/search/byLR/"+LRNumber)
                    .then(function (response) {
                        $location.url('viewcargobooking/'+response.data);
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    });


            }
        }
    });