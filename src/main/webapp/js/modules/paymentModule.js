"use strict";
/*global angular, _*/

angular.module('myBus.paymentModule', ['ngTable', 'ui.bootstrap'])
    .controller("PaymentController",function($rootScope, $scope, $filter, $location, $log,$uibModal, NgTableParams, paymentManager, userManager){
        $scope.loading = false;
        $scope.query = {"status":null};
        $scope.user = userManager.getUser();
        $scope.approvedPayments=[];
        $scope.pendingPayments=[];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        $scope.canAddPayment = function() {
            var user = userManager.getUser();
            return user.admin || user.branchOfficeId;
        }
        var loadPendingPayments = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentManager.pendingPayments(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.pendingPayments = response.content;
                    tableParams.total(response.totalElements);
                    $scope.pendingTotal = response.totalElements;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.pendingPayments;
                }
            });
        };

        var loadApprovedPayments = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentManager.approvedPayments(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.approvedPayments = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.approvedPayments;
                }
            });
        };
        $scope.init = function() {
            paymentManager.count(true, function(paymentsCount){
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:paymentsCount,
                    getData: function (params) {
                        loadPendingPayments(params);
                    }
                });
            });
            paymentManager.count(false, function(count){
                $scope.approvedTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:15,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:count,
                    getData: function (params) {
                        loadApprovedPayments(params);
                    }
                });
            });
        };

        $scope.init();
        $scope.searchFilter = function(){
            $scope.init();
        }
        $scope.handleClickAddPayment = function() {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-payment-modal.html',
                controller : 'EditPaymentController',
                resolve : {
                    paymentId : function(){
                        return null;
                    }
                }
            });
        };

        $rootScope.$on('UpdateHeader',function (e,value) {
            $scope.init();
        });

        $scope.handleClickUpdatePayment = function(paymentId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-payment-modal.html',
                controller : 'EditPaymentController',
                resolve : {
                    paymentId : function(){
                        return paymentId;
                    }
                }
            });
        };
        $scope.delete = function(paymentId) {
            paymentManager.delete(paymentId, function(data){
                $scope.init();
            });
        };
        $scope.approveOrRejectPayment = function(payment, status){
            if(status == "Approve"){
                payment.status ="Approved";
            } else {
                payment.status ="Rejected";
            }
            paymentManager.save(payment, function(data){
                $rootScope.$broadcast('UpdateHeader');
                swal("Great", "Payment is updated", "success");
            });
        }
    }).controller("EditPaymentController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams, paymentManager, userManager, branchOfficeManager,paymentId) {
        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.user = userManager.getUser();

        $scope.payment = {'type': 'EXPENSE', 'branchOfficeId': $scope.user.branchOfficeId};
        $scope.today();
        $scope.date = null;
        $scope.format = 'dd-MMMM-yyyy';

        $scope.offices = [];
        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.showType = function () {
            console.log($scope.payment);
        };

        if (paymentId) {
            $scope.setPaymentIntoModal = function (paymentId) {
                paymentManager.getPaymentById(paymentId, function (data) {
                    $scope.payment = data;
                });
            };
            $scope.setPaymentIntoModal(paymentId);
        }

        $scope.add = function () {
            if (paymentId) {
                if ($scope.updatePaymentForm.$invalid) {
                    swal("Error!", "Please fix the errors in the form", "error");
                    return;
                }
                paymentManager.save(paymentId, $scope.payment, function (data) {
                    swal("Great", "Saved successfully", "success");
                });
            }

            $scope.payment.date = $scope.dt;
            paymentManager.save($scope.payment, function (data) {
                swal("Great", "Saved successfully", "success");
                $location.url('/payments');
            });
        }


        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function () {

        }
        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };
        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
        }

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function () {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function (year, month, day) {
            $scope.dt = new Date(year, month, day);
        };
        $scope.popup1 = {
            opened: false
        };
        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
        };
    }).factory('paymentManager', function ($rootScope, $http, $log) {
        var payments = {};
        return {
            pendingPayments: function (pageable, callback) {
                $http({url:'/api/v1/payments/pending',method:"GET", params: pageable})
                    .then(function (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
            approvedPayments: function (pageable, callback) {
                $http({url:'/api/v1/payments/approved',method:"GET", params: pageable})
                    .then(function (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
            count: function (pendingPayments, callback) {
                $http.get('/api/v1/payments/count?pending='+pendingPayments)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving payments count");
                    });
            },
            countVehicleExpenses: function (query, callback) {
                $http.get('/api/v1/vehicleExpenses/count')
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving payments count");
                    });
            },
            getVehicleExpenses: function (pageable, callback) {
                $http({url:'/api/v1/vehicleExpenses',method:"GET", params: pageable})
                    .then(function (response) {
                        payments = response.data;
                        callback(payments);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
            delete: function (paymentId, callback) {
                $http.delete('/api/v1/payment/' + paymentId)
                    .then(function (response) {
                        callback(response.data);
                        swal("Great", "Saved Deleted", "success");
                    }, function (error) {
                        $log.debug("error deleting payment");
                        sweetAlert("Error",err.message,"error");
                    });
            },
            getPaymentById: function (id,callback) {
                $log.debug("fetching payment data ...");
                $http.get('/api/v1/payment/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            save: function (payment, callback) {
                if (!payment.id) {
                    $http.post('/api/v1/payment/', payment).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    });
                } else {
                    $http.put('/api/v1/payment/', payment).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    });
                }
            },
            getAllData: function () {
                return payments;
            },
            getOneById: function (id) {
                return _.first(_.select(expenses, function (value) {
                    return value.id === id;
                }));
            }
        }
    });