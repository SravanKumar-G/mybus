"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingListController",function($rootScope, $scope, NgTableParams,userManager, cargoBookingManager,$location, branchOfficeManager){
        $scope.headline = "Cargo Bookings";
        $scope.cargoBookings = null;
        $scope.cargoBookingsTable = null;
        $scope.filter = {startDate:new Date(), endDate: new Date()};
        $scope.tableParams = {};
        $scope.members = [];
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        userManager.getUserNames(function (data) {
            $scope.members = data;
        });

        cargoBookingManager.getShipmentTypes(function (types) {
            $scope.shipmentTypes = types;
        });
        var loadCargoBookings = function (tableParams, filter) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            if(!filter) {
                filter = {};
            }
            filter.page = tableParams.page();
            filter.size = tableParams.count();
            filter.sort =sortProps;

            cargoBookingManager.findCargoBookings(filter, function(cargoBookings){
                if(angular.isArray(cargoBookings)) {
                    $scope.loading = false;
                    tableParams.data = cargoBookings;
                    $scope.tableParams = tableParams;
                    $scope.cargoBookings = cargoBookings;
                }
            });
        };
        $scope.init = function(filter) {
            cargoBookingManager.count(filter, function(shipmentCount){
                console.log('shipmentCount  '+ shipmentCount);
                $scope.cargoBookingsTable = new NgTableParams({
                    page: 1, // show first page
                    count:20,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts:[20,50,100],
                    total:shipmentCount,
                    getData: function (params) {
                        loadCargoBookings(params, filter);
                    }
                });
            });
        };
        $scope.init();
        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/'+bookingId);
        }

        $scope.search = function() {
           $scope.init($scope.filter);
        }

        $scope.payCargoBooking = function(bookingId) {
            console.log('pay cargo booking '+ bookingId);
            cargoBookingManager.payCargoBooking(bookingId, function () {
               $location.url('cargobookings');
               //reload bookings now
               $scope.init();
            });
        }
        /**
         * This can be called when CargoBooking is paid from the details screen
         */
        $rootScope.$on('UpdateCargoBookingList',function (e,value) {
            $scope.init();
        });
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

    .controller("CargoBookingController",function($rootScope, $scope, $stateParams, $uibModal,cargoBookingManager, suppliersManager, userManager, branchOfficeManager, $location){
        $scope.headline = "Cargo Booking";
        $scope.shipmentTypes = [];
        $scope.users = [];
        $scope.shipment = {'paymentType':'Paid'};
        $scope.shipment.dispatchDate = new Date();
        $scope.filter;
        $scope.suppliers = [];
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        suppliersManager.getSuppliers(function (data) {
            $scope.suppliers = data;
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
            if($scope.shipment.paymentType  === 'OnAccount' && !$scope.shipment.supplierId ){
                swal("Error", "Please select the account name", "error");
                return;
            }
            cargoBookingManager.createShipment($scope.shipment, function (response) {
                $location.url('viewcargobooking/'+response.id);
                $rootScope.$broadcast('UpdateHeader');
            });
        }

        $scope.lookUpCargoBooking = function(){
            if(!this.filter) {
                swal("Error", "Search text is missing", "error");
            } else{
                cargoBookingManager.lookupCargoBooking(this.filter);
            }
        }
        $scope.payCargoBooking = function(bookingId) {
            cargoBookingManager.payCargoBooking(bookingId, function () {
                $location.url('cargobookings');
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }
        $scope.cancelCargoBooking = function(bookingId) {
            cargoBookingManager.cancelCargoBooking(bookingId, function () {
                $location.url('cargobookings');
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }


    }).factory('cargoBookingManager', function ($rootScope, $q, $http, $log, $location) {
        return {
            findCargoBookings: function (filter, callback) {
                console.log('filter ' + filter);
                $http.post('/api/v1/shipments', filter)
                    .then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching cargo booking", err.message, "error");
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
            count:function (filter, callback) {
                $http.post('/api/v1/shipments/count', filter)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving shipments count");
                    });
            },
            cancelCargoBooking:function (bookingId, callback) {
                swal({title: "Do you want to cancel this booking now?",   text: "Are you sure?",   type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, cancel!",
                    closeOnConfirm: true }, function() {
                    $http.put('/api/v1/shipment/cancel/'+bookingId)
                        .then(function (response) {
                            callback(response.data);
                        }, function (error) {
                            $log.debug("error canceling the booking " + error);
                        }),function (error) {
                            alert("Error paying booking:" + error.data.message);
                        }
                    });
            },

            payCargoBooking:function (bookingId, callback) {
                swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "Yes, pay now!",
                    closeOnConfirm: true }, function() {
                    $http.put('/api/v1/shipment/pay/'+bookingId)
                        .then(function (response) {
                            callback(response.data);
                        }, function (error) {
                            $log.debug("error retrieving shipments count");
                        }),function (error) {
                        alert("Error paying booking:" + error.data.message);
                    }
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