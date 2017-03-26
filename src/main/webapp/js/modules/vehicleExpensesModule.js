/**
 * Created by chsru on 3/22/2017.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.vehicleExpensesModule', ['ngTable', 'ui.bootstrap'])
    .controller("VehicleExpensesController",function($rootScope, $scope, $filter,vehicleManager,paginationService, $location, $log,$uibModal, NgTableParams, paymentManager, userManager){
        $scope.payments = [];
        $scope.loading = false;
        $scope.query = {};
        $scope.user = userManager.getUser();
        $scope.currentPageOfPayments=[];
        $scope.canAddPayment = function() {
            var user = userManager.getUser();
            return user.admin || user.branchOfficeId;
        }
        var pageable ;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            paginationService.pagination(tableParams, function(response){
                pageable = {page:tableParams.page(), size:tableParams.count(), sort:response};
            });
            paymentManager.load($scope.query,pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.vehicleExpenses = response.content;
                    vehicleManager.getVehicles(pageable,function(data){
                        $scope.vehicles = data.content;
                        angular.forEach($scope.vehicleExpenses, function (payment) {
                            angular.forEach($scope.vehicles, function (vehicle) {
                                if(vehicle.id == payment.vehicleId){
                                    payment.attributes.vehicleRegNo = vehicle.regNo;
                                }
                            });
                        });
                    })
                }
                $scope.loading = false;
                $scope.payments = response.content;
                tableParams.total(response.totalElemets);
                $scope.count = response.totalElements;
                tableParams.data = $scope.payments;
                $scope.currentPageOfPayments = $scope.payments;
            });
        };

        $scope.init = function() {
            paymentManager.count($scope.query, function(paymentsCount){
                $scope.paymentTableParams = new NgTableParams({
                    page: 1, // show first page
                    size: 10,
                    count:10,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:paymentsCount,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            });
        };
        $scope.init();
        $rootScope.$on('UpdateHeader',function (e,value) {
            $scope.init();
        });
        $scope.handleClickAddPayment = function() {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-expense-modal.html',
                controller: 'EditVehicleExpensesController',
                resolve : {
                    paymentId : function(){
                        return null;
                    }
                }
            })
        };
        $scope.handleClickUpdatePayment = function(paymentId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'update-payment-modal.html',
                controller : 'EditVehicleExpensesController',
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
    })
    .controller("EditVehicleExpensesController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams,vehicleManager, paymentManager, userManager, branchOfficeManager, paymentId) {
        var pageable;
        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.user = userManager.getUser();
        $scope.loadVehicles = function () {
            vehicleManager.getVehicles(pageable, function (data) {
                $scope.vehicles = data.content;
            })
        };
        $scope.loadVehicles();

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
                paymentManager.save(paymentId, $scope.payment, function (data) {
                    swal("Great", "Saved successfully", "success");
                });
            }
            $scope.payment.date = $scope.dt;
            paymentManager.save($scope.payment, function (data) {
                swal("Great", "Saved successfully", "success");
                $location.url('/vehicleexpenses');
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
            return '';
        }
    });