"use strict";
/*global angular, _*/

angular.module('myBus.cargoBooking', ['ngTable', 'ui.bootstrap'])
    .controller("CargoBookingListController", function ($rootScope, $scope, $uibModal, NgTableParams, userManager, cargoBookingManager, $location, branchOfficeManager, paginationService) {
        $scope.headline = "Cargo Bookings";
        $scope.cargoBookings = null;
        $scope.cargoBookingsTable = null;
        $scope.filter = {startDate: new Date(), endDate: new Date()};
        $scope.tableParams = {};
        $scope.members = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
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
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            $scope.loading = true;
            if (!filter) {
                filter = {};
            }
            filter.page = tableParams.page();
            filter.size = tableParams.count();
            filter.sort = sortProps;

            cargoBookingManager.findCargoBookings(filter, function (cargoBookings) {
                if (angular.isArray(cargoBookings)) {
                    $scope.loading = false;
                    tableParams.data = cargoBookings;
                    $scope.tableParams = tableParams;
                    $scope.cargoBookings = cargoBookings;
                }
            });
        };
        $scope.init = function (filter) {
            cargoBookingManager.count(filter, function (shipmentCount) {
                $scope.cargoBookingsTable = new NgTableParams({
                    page: 1, // show first page
                    count: 100,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts: [100, 200, 500],
                    total: shipmentCount,
                    getData: function (params) {
                        loadCargoBookings(params, filter);
                    }
                });
            });
        };
        $scope.init();
        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/' + bookingId);
        }
        $scope.search = function () {
            $scope.init($scope.filter);
        }

        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.init();
            });
        }

        $scope.initiateServiceAllotment = function (bookingId) {
            swal({
                title: "Assign Service",
                text: "Please provide provide a service number",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "Collecting person name or identification"
            }, function (serviceNumber) {
                if (serviceNumber === false) return false;
                if (serviceNumber === "") {
                    swal.showInputError("ServiceNumber is required");
                    return false
                }
                cargoBookingManager.allotService(bookingId, serviceNumber, function (data) {
                    swal("Nice!", data.shipmentNumber + " has been alloted to service" + data.vehicleId, "success");
                    $scope.init();
                });
            });
        }

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

        /**
         * This can be called when CargoBooking is paid from the details screen
         */
        $rootScope.$on('UpdateCargoBookingList', function (e, value) {
            $scope.init();
        });
    }).controller("CargoBookingLookupController", function ($rootScope, $scope, $location, $uibModal, cargoBookingManager, userManager, bookings) {
        $scope.headline = "Cargo Bookings";
        $scope.bookings = bookings;
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.gotoBooking = function (bookingId) {
            $rootScope.modalInstance.dismiss('cancel');
            $location.url('viewcargobooking/' + bookingId);
        }
    })
    .controller("CargoBookingHeaderController", function ($rootScope, $scope, cargoBookingManager, $location) {
        $scope.headline = "Cargo Bookings";
        $scope.bookingId;
        $scope.search = function () {
            if (!$scope.bookingId) {
                sweetAlert("Error", "Enter the bookingId for search", "error");
                return;
            }
            cargoBookingManager.lookupCargoBooking($scope.bookingId);
        }
        $scope.newBooking = function () {
            $location.url('newbooking');
        }
    }).controller("DeliverBookingController", function ($rootScope, $scope, cargoBookingManager, $location, bookingId) {
        $scope.deliveryComment = null;
        $scope.showError = false;
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.cargoBookingId = bookingId;

        $scope.deliverCargoBooking = function () {
            if (!$scope.deliveryComment) {
                $scope.showError = true;
                return;
            }
            cargoBookingManager.deliverCargoBooking($scope.cargoBookingId, $scope.deliveryComment, function () {
                $location.url('cargobookings');
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }
    })
    .controller("CargoUnloadingSheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, paginationService) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.filter.toBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        $scope.unload = function () {
            cargoBookingManager.unloadBookings($scope.selectedBookings, function (success) {
                //reload the bookings for unload
                $scope.searchBookingForUnload();
            })
        }
        $scope.searchBookingForUnload = function () {
            cargoBookingManager.getBookingsForUnloading($scope.filter, function (response) {
                $scope.cargoBookings = response;
                console.log("$scope.cargoBookings", $scope.cargoBookings);
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
            })
        }
        $scope.searchBookingForUnload();
        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/' + bookingId);
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

    })

    .controller("CargoLoadingSheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, vehicleManager, paginationService) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.filter.toBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });
        $scope.loadToVehicle = function () {
            console.log('loading to vehicle ' + $scope.selected);
            if(!$scope.selected) {
                swal("Error", "Please select a vehicle number to load", "error");
                return;
            }
            if($scope.selectedBookings.length === 0) {
                swal("Error", "Please select a bookings to load", "error");
                return;
            }

            cargoBookingManager.loadBookings($scope.vehicle, $scope.selectedBookings, function (success) {
                //reload the bookings for unload
                $scope.searchBookingForUnload();
            })
        }
        $scope.searchBookingForUnload = function () {
            cargoBookingManager.getBookingsForUnloading($scope.filter, function (response) {
                $scope.cargoBookings = response;
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
            })
        };
        $scope.searchBookingForUnload();

        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/' + bookingId);
        };

        $scope.getAllVehicles = function () {
            vehicleManager.getVehicles({}, function (response) {
                $scope.vehicles = response.content;
            })
        }
        $scope.getAllVehicles();

        $scope.vehicleMy = function (vehicle) {
            console.log("vehice", vehicle);
        }
        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }
    })

    .controller("CargoDeliverySheetController", function ($rootScope, $scope, branchOfficeManager, userManager, cargoBookingManager, $location, paginationService) {
        $scope.selectedBookings = [];
        $scope.offices = [];
        $scope.filter = {};
        $scope.filterString = '';
        $scope.cargoBookings = [];
        $scope.filter.toBranchId = userManager.getUser().branchOfficeId;
        $scope.toggleBookingSelection = function (bookingId) {
            var idx = $scope.selectedBookings.indexOf(bookingId);
            if (idx > -1) {
                $scope.selectedBookings.splice(idx, 1);
            } else {
                $scope.selectedBookings.push(bookingId);
            }
        }
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.offices.unshift({"name": "All"});
        });

        $scope.searchBookingForDelivery = function () {
            cargoBookingManager.getBookingsForDelivery($scope.filter, function (response) {
                $scope.cargoBookings = response;
                $scope.total = 0;
                $scope.paidCargoBooking = 0;
                $scope.toPayCargoBooking = 0;
                for (var i = 0; i < $scope.cargoBookings.length; i++) {
                    $scope.total += $scope.cargoBookings[i].totalCharge;
                    if ($scope.cargoBookings[i].paymentType === "Paid") {
                        $scope.paidCargoBooking += $scope.cargoBookings[i].totalCharge;
                    } else {
                        $scope.toPayCargoBooking += $scope.cargoBookings[i].totalCharge;
                    }
                }
            })
        };

        $scope.searchBookingForDelivery();
        $scope.gotoBooking = function (bookingId) {
            $location.url('viewcargobooking/' + bookingId);
        };
        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.searchBookingForDelivery();
            });
        };

        $scope.exportToExcel = function (tableId, fileName) {
            paginationService.exportToExcel(tableId, fileName);
        }

    })

    .controller("CargoBookingController", function ($rootScope, $scope, $stateParams, $uibModal, cargoBookingManager, suppliersManager, userManager, branchOfficeManager, $location) {
        $scope.headline = "Cargo Booking";
        $scope.shipmentTypes = [];
        $scope.users = [];
        $scope.shipment = {'paymentType': 'Paid'};
        $scope.shipment.dispatchDate = new Date();
        $scope.filter;
        $scope.suppliers = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
            $scope.shipment.fromBranchId = userManager.getUser().branchOfficeId;
        });

        suppliersManager.getSuppliers(function (data) {
            $scope.suppliers = data;
        });

        $scope.getTotalPrice = function () {
            $scope.shipment.totalCharge = 0;
            for (var index = 0; index < $scope.shipment.items.length; index++) {
                if ($scope.shipment.items[index].charge) {
                    $scope.shipment.totalCharge += parseFloat($scope.shipment.items[index].charge);
                }
            }
            if ($scope.shipment.loadingCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.loadingCharge);
            }
            if ($scope.shipment.unloadingCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.unloadingCharge);
            }
            if ($scope.shipment.otherCharge) {
                $scope.shipment.totalCharge += parseFloat($scope.shipment.otherCharge);
            }
            return $scope.shipment.totalCharge;
        }
        $scope.printArea = function () {
            var w = window.open();
            w.document.write(document.getElementsByClassName('report_left_inner')[0].innerHTML);
            w.print();
            w.close();
        }
        if ($stateParams.id) {
            cargoBookingManager.getCargoBooking($stateParams.id, function (cargoBooking) {
                $scope.shipment = cargoBooking;
            });
        }
        userManager.getUserNames(function (users) {
            $scope.users = users;
        });

        cargoBookingManager.getShipmentTypes(function (types) {
            $scope.shipmentTypes = types;
            var paidType = _.find($scope.shipmentTypes, function (type) {
                if (type.shipmentCode === 'P') {
                    return type.id;
                }
            });
        });

        //set the user to current user
        $scope.shipment.forUser = userManager.getUser().id;
        $scope.currentUser = userManager.getUser();

        $scope.copyDetails = function () {
            $scope.shipment.receiverName = $scope.shipment.senderName;
            $scope.shipment.receiverNo = $scope.shipment.senderNo;
        }
        $scope.addItem = function () {
            if (!$scope.shipment.items) {
                $scope.shipment.items = [];
            }
            $scope.shipment.items.push({'index': $scope.shipment.items.length + 1});
        }
        $scope.addItem();

        $scope.deleteItem = function (item) {
            $scope.shipment.items.splice(item.index - 1, 1);
            for (var index = 0; index < $scope.shipment.items.length; index++) {
                $scope.shipment.items[index].index = index + 1;
            }
        }
        $scope.cancelShipmentForm = function () {
            $location.url();
        }
        $scope.dt = new Date();
        $scope.copySenderDetails = function () {
            if ($scope.shipment.copySenderDetails) {
                $scope.shipment.toContact = $scope.shipment.fromContact;
                $scope.shipment.toName = $scope.shipment.fromName;
                $scope.shipment.toEmail = $scope.shipment.fromEmail;
            }
        }
        $scope.saveCargoBooking = function () {
            if ($scope.shipment.paymentType === 'OnAccount' && !$scope.shipment.supplierId) {
                swal("Error", "Please select the account name", "error");
                return;
            }
            if (!$scope.shipment.fromContact || $scope.shipment.fromContact.toString().length < 10) {
                swal("Error", "Invalid contact number for From", "error");
                return;
            }

            if (!$scope.shipment.toContact || $scope.shipment.toContact.toString().length < 10) {
                swal("Error", "Invalid contact number for To", "error");
                return;
            }
            cargoBookingManager.createShipment($scope.shipment, function (response) {
                $location.url('viewcargobooking/' + response.id);
                $rootScope.$broadcast('UpdateHeader');
            });
        }

        $scope.lookUpCargoBooking = function () {
            if (!this.filter) {
                swal("Error", "Search text is missing", "error");
            } else {
                cargoBookingManager.lookupCargoBooking(this.filter);
            }
        }
        $scope.initiateDeliverCargoBooking = function (bookingId) {
            cargoBookingManager.initiateDeliverCargoBooking(bookingId, function (data) {
                swal("Great!", data.shipmentNumber + " has been delivered", "success");
                $scope.init();
            });
        }

        $scope.cancelCargoBooking = function (bookingId) {
            cargoBookingManager.cancelCargoBooking(bookingId, function () {
                $location.url('cargobookings');
                $rootScope.$broadcast('UpdateCargoBookingList');
            });
        }
        /**
         * Module to find the contact details from the previous booking using the contact number
         * @param contactType -- 'from' or 'to'
         *
         */
        $scope.getDetailsForContact = function (contactType) {
            if (contactType === 'from') {
                cargoBookingManager.findContactInfoFromPreviousBookings(contactType, $scope.shipment.fromContact, function (data) {
                    $scope.shipment.fromName = data.name;
                    $scope.shipment.fromEmail = data.email;
                });
            } else if (contactType === 'to') {
                cargoBookingManager.findContactInfoFromPreviousBookings(contactType, $scope.shipment.toContact, function (data) {
                    $scope.shipment.toName = data.name;
                    $scope.shipment.toEmail = data.email;
                });
            }
        }
        $scope.sendSMS = function (shipmentId) {
            cargoBookingManager.sendSMSForCargoBooking(shipmentId);
        }

    }).factory('cargoBookingManager', function ($rootScope, $q, $uibModal, $http, $log, $location) {
    return {
        findContactInfoFromPreviousBookings: function (contactType, contact, callback) {
            $http.get('/api/v1/shipment/findContactInfo?contactType=' + contactType + "&contact=" + contact)
                .then(function (response) {
                    if (angular.isFunction(callback)) {
                        callback(response.data);
                    }
                }, function (err, status) {
                    sweetAlert("Error searching cargo contact info", err.message, "error");
                });
        },
        findCargoBookings: function (filter, callback) {
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
            $http.get("/api/v1/shipment/" + id)
                .then(function (response) {
                    callback(response.data)
                }, function (error) {
                    swal("oops", error, "error");
                })
        },
        getShipmentTypes: function (callback) {
            $http.get("/api/v1/shipment/types")
                .then(function (response) {
                    callback(response.data)
                }, function (error) {
                    swal("oops", error, "error");
                })
        }, createShipment: function (cargoBooking, successcallback) {
            $http.post("/api/v1/shipment", cargoBooking)
                .then(function (response) {
                    successcallback(response.data)
                }, function (error) {
                    swal("oops", error.data.message, "error");
                })
        },
        count: function (filter, callback) {
            $http.post('/api/v1/shipments/count', filter)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error retrieving shipments count");
                });
        },
        cancelCargoBooking: function (bookingId, callback) {
            swal({
                title: "Do you want to cancel this booking now?", text: "Are you sure?", type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, cancel!",
                closeOnConfirm: true
            }, function () {
                $http.put('/api/v1/shipment/cancel/' + bookingId)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error canceling the booking " + error);
                    });
            });
        },
        initiateDeliverCargoBooking: function (bookingId, callback) {
            swal({
                title: "Delivery comment",
                text: "Please provide delivery comment:",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                inputPlaceholder: "Collecting person name or identification"
            }, function (deliveryComment) {
                if (deliveryComment === false) return false;
                if (deliveryComment === "") {
                    swal.showInputError("Identification is required");
                    return false
                }
                $http.put('/api/v1/shipment/deliver/' + bookingId, deliveryComment)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving shipments count");
                    });
            });
        },
        lookupCargoBooking: function (LRNumber) {
            $http.get("/api/v1/shipment/search/byLR?LRNumber=" + LRNumber)
                .then(function (response) {
                    if (response.data.length === 0) {
                        swal("Error", "No Cargo Bookings found", "error");
                    } else if (response.data.length === 1) {
                        $location.url('viewcargobooking/' + response.data[0].id);
                    } else {
                        //console.log("found more than one booking " + JSON.stringify(response.data.length));
                        $rootScope.modalInstance = $uibModal.open({
                            templateUrl: 'cargobookings-modal.html',
                            controller: 'CargoBookingLookupController',
                            resolve: {
                                bookings: function () {
                                    return response.data;
                                }
                            }
                        });
                    }
                }, function (error) {
                    swal("oops", error.data.message, "error");
                });
        },
        sendSMSForCargoBooking: function (shipmentId) {
            $http.post("/api/v1/shipment/sendSMS/" + shipmentId)
                .then(function (response) {
                    console.log('sent SMS');
                }, function (error) {
                    swal("oops", error.data.message, "error");
                });
        },
        getBranchSummary: function (filter, callback) {
            $http.post('/api/v1/shipment/branchSummary', filter)
                .then(function (response) {
                    if (angular.isFunction(callback)) {
                        callback(response.data);
                    }
                }, function (err, status) {
                    sweetAlert("Error searching branch summary", err.message, "error");
                });
        },
        getBookingsForUnloading: function (filter, callback) {
            $http.post('/api/v1/shipment/search/unloading', filter)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    sweetAlert("Error searching for unloading bookings", error.message, "error");
                });
        },
        loadBookings: function (vehicleId, bookingIds, callback) {
            $http.post('/api/v1/shipment/assignVehicle/'+vehicleId, bookingIds)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    sweetAlert("Error unloading bookings", error.message, "error");
                });
        },
        unloadBookings: function (bookingIds, callback) {
            $http.post('/api/v1/shipment/unload', bookingIds)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    sweetAlert("Error unloading bookings", error.message, "error");
                });
        }, getBookingsForDelivery: function (filter, callback) {
            $http.post('/api/v1/shipment/search/undelivered', filter)
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    sweetAlert("Error searching for undelivered bookings", error.message, "error");
                });
        }
    }
});