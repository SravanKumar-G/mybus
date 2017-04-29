"use strict";
/*global angular, _*/

angular.module('myBus.paymentModule', ['ngTable', 'ui.bootstrap'])
    .controller("OfficeExpensesController",function($rootScope, $scope, $filter, $location, $log,$uibModal, NgTableParams, officeExpensesManager, userManager){
        $scope.loading = false;
        $scope.query = {"status":null};
        $scope.user = userManager.getUser();
        $scope.approvedExpenses=[];
        $scope.pendingExpenses=[];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        $scope.canAddPayment = function() {
            var user = userManager.getUser();
            return user.admin || user.branchOfficeId;
        }
        var loadPendingExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            officeExpensesManager.pendingOfficeExpenses(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.pendingExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.pendingTotal = response.totalElements;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.pendingExpenses;
                }
            });
        };

        var loadApprovedExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            officeExpensesManager.approvedOfficeExpenses(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.approvedExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.approvedExpenses;
                }
            });
        };
        $scope.init = function() {
            officeExpensesManager.count(true, function(expensesCount){
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:expensesCount,
                    getData: function (params) {
                        loadPendingExpenses(params);
                    }
                });
            });
            officeExpensesManager.count(false, function(count){
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
                        loadApprovedExpenses(params);
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
                templateUrl : 'add-expense-modal.html',
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
                controller : 'EditExpenseController',
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
        $scope.popUp = function (formId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'service-form-modal.html',
                controller:'popUpController',
                resolve : {
                    formId : function(){
                        return formId;
                    }
                }
            })
        }
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
    })
    .controller("popUpController", function($scope,$rootScope, serviceReportsManager , formId){
        $scope.service = {};
        serviceReportsManager.getForm(formId,function (data) {
            $scope.service = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })

    .controller("EditExpenseController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams, paymentManager, userManager, branchOfficeManager,paymentId) {
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
                if ($scope.addNewExpenseForm.$invalid) {
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
    }).factory('officeExpensesManager', function ($rootScope, $http, $log) {
        return {
            pendingOfficeExpenses: function (pageable, callback) {
                $http({url:'/api/v1/officeExpenses/pending',method:"GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
            approvedOfficeExpenses: function (pageable, callback) {
                $http({url:'/api/v1/officeExpenses/approved',method:"GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('paymentsInitComplete');
                    }, function (error) {
                        $log.debug("error retrieving payments");
                    });
            },
            count: function (pending, callback) {
                $http.get('/api/v1/officeExpenses/count?pending='+pending)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving payments count");
                    });
            },
            delete: function (paymentId, callback) {
                $http.delete('/api/v1/officeExpense/' + paymentId)
                    .then(function (response) {
                        callback(response.data);
                        swal("Great", "Saved Deleted", "success");
                    }, function (error) {
                        $log.debug("error deleting payment");
                        sweetAlert("Error",err.message,"error");
                    });
            },
            getExpenseById: function (id,callback) {
                $log.debug("fetching payment data ...");
                $http.get('/api/v1/officeExpense/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            save: function (officeExpense, callback) {
                if (!officeExpense.id) {
                    $http.post('/api/v1/officeExpense/', officeExpense).then(function (response) {
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
                    $http.put('/api/v1/officeExpense/', officeExpense).then(function (response) {
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
            }
        }
    });