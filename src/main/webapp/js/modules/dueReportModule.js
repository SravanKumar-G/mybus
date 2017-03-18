/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.dueReportModule', ['ngTable', 'ngAnimate', 'ui.bootstrap'])
    .controller('DueReportController', function($scope, dueReportManager, NgTableParams, $filter, $location, userManager) {
        $scope.headline = "Due Report";
        $scope.currentPageOfDues = [];
        $scope.loading = false;
        $scope.user = userManager.getUser();
        if(!$scope.user.admin) {
            $location.url('officeduereport/'+$scope.user.branchOfficeId);
        } else{
            var loadTableData = function (tableParams) {
                $scope.loading = true;
                dueReportManager.loadReports(function (data) {
                    $scope.loading = false;
                    if(angular.isArray(data)) {
                        $scope.allDues = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                        tableParams.total($scope.allDues.length);
                        $scope.currentPageOfDues = $scope.allDues.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    }
                });
            }
            $scope.duesTableParams = new NgTableParams({
                page: 1,
                count:99999,
                sorting: {
                    name: 'asc'
                }
            }, {
                total: $scope.currentPageOfDues.length,
                getData: function (params) {
                    loadTableData(params);
                }
            });
        }

        $scope.goToDueReport = function(officeId) {
            console.log('relaod report..');
            $location.url('officeduereport/'+officeId);
        }
        $scope.gotoPayments = function(){
            $location.url('payments');
        }
    })
    .controller('OfficeDueReportController', function($scope, $rootScope, $stateParams, dueReportManager, userManager, NgTableParams, $filter, $location) {
        $scope.headline = "Office Due Report";
        $scope.currentPageOfDues = [];
        $scope.officeId = $stateParams.id;
        $scope.loading = false;
        $scope.officeDue = {};
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getBranchReport($scope.officeId,function (data) {
                $scope.loading = false;
                $scope.allDues = {};
                $scope.officeDue = data;
                if($scope.officeDue.duesByDate) {
                    $scope.officeDue.duesByDate = tableParams.sorting() ? $filter('orderBy')($scope.officeDue.duesByDate, tableParams.orderBy()) : $scope.officeDue.duesByDate;
                    tableParams.total($scope.officeDue.duesByDate.length);
                    $scope.currentPageOfDues = $scope.officeDue.duesByDate.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        }
        $scope.duesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableData($scope.duesTableParams);
        });
        $scope.showDueReportByDate = function(dueDate) {
            $location.url('officeduereport/'+$scope.officeId+'/'+dueDate);
        }
    })
    .controller('OfficeDueByDateReportController', function($scope, $rootScope, $stateParams, dueReportManager, userManager, NgTableParams, $filter, $location) {
        $scope.headline = "Office Due Report";
        $scope.currentPageOfDues = [];
        $scope.officeId = $stateParams.id;
        $scope.date = $stateParams.date;
        $scope.loading = false;
        $scope.officeDue = {};
        $scope.currentPageOfDues = [];
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            dueReportManager.getBranchReportByDate($scope.officeId,$scope.date,function (data) {
                $scope.loading = false;
                $scope.officeDue = data;
                if($scope.officeDue.bookings) {
                    $scope.officeDue.bookings = tableParams.sorting() ? $filter('orderBy')($scope.officeDue.bookings, tableParams.orderBy()) : $scope.officeDue.bookings;
                    tableParams.total($scope.officeDue.bookings.length);
                    $scope.currentPageOfDues = $scope.officeDue.bookings.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        }
        $scope.duesTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfDues.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $rootScope.$on("ReloadOfficeDueReport", function(){
            loadTableData($scope.duesTableParams);
        });
        $scope.payBooking = function(bookingId, officeId) {
            swal({title: "Pay for this booking now?",   text: "Are you sure?",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, pay now!",
                closeOnConfirm: true }, function() {
                dueReportManager.payBooking(bookingId, function(data) {
                    $rootScope.$broadcast('UpdateHeader');
                    $location.url('/officeduereport/'+officeId);
                },function (error) {
                    sweetAlert("Oops...", "Error submitting the report", "error");
                });
            });
        }
    }).factory('dueReportManager', function ($http, $rootScope, $log) {
        var dueReports = {};
        return {
            loadReports:function(callback) {
                $http.get('/api/v1/dueReports')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due reports");
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            getBranchReport:function(id,callback) {
                $http.get('/api/v1/dueReport/office/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            getBranchReportByDate:function(id,date,callback) {
                $http.get('/api/v1/dueReport/office/'+id+'/'+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading due report");
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            payBooking:function(id, callback) {

                $http.put('/api/v1/dueReport/payBookingDue/'+id)
                    .then(function (response) {
                        $rootScope.$broadcast('ReloadOfficeDueReport');
                        callback(response.data);
                    },function (error) {
                        $log.debug("error paying a booking");
                        sweetAlert("Error",err.data.message,"error");
                    });

            }
        }
    });



