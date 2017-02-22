/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.serviceReportsModule', ['ngTable', 'ngAnimate', 'ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('ServiceReportsController', function($scope,$state, $http, $log, $filter, NgTableParams, $location, serviceReportsManager) {
        $scope.headline = "Service Reports";
        $scope.count = 0;
        $scope.offices = {};

    })
    .controller('ServiceReportController', function($scope,$state,$stateParams, $filter, NgTableParams, $location, serviceReportsManager, expensesManager) {
        $scope.headline = "Service Report";
        $scope.service = {};
        $scope.downloaded = false;
        $scope.serviceId = $stateParams.id;
        $scope.currentPageOfBookings = [];
        $scope.allBookings = [];
        var loadTableData = function (tableParams, $defer) {
            serviceReportsManager.getReport($scope.serviceId, function(data){
                $scope.service = data;
                $scope.downloaded = true;
                $scope.allBookings = tableParams.sorting() ? $filter('orderBy')($scope.service.bookings, tableParams.orderBy()) : $scope.service.bookings;
                tableParams.total($scope.allBookings.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.allBookings);
                }
                $scope.currentPageOfBookings = $scope.allBookings.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.bookingsTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                ticketNo: 'asc'
            }
        }, {
            total: $scope.currentPageOfBookings.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        loadTableData($scope.bookingsTableParams);
        $scope.isOnlineBooking = function(booking) {
            return booking.bookedBy =='ONLINE' || booking.bookedBy =='REDBUS-API' || booking.bookedBy =='PAYTM-API';
        }
        $scope.isNotOnlineBooking = function(booking) {
            return booking.bookedBy !='ONLINE' && booking.bookedBy !='REDBUS-API' && booking.bookedBy !='PAYTM-API';
        }

        $scope.calculateNet = function() {

            var netCashIncome = 0;
            var expenseTotal = 0;
            for (var i =0; i< $scope.currentPageOfBookings.length;i++) {
                var booking = $scope.currentPageOfBookings[i];
                if (booking.paymentType == "CASH" && booking.netAmt && booking.netAmt != "") {
                    netCashIncome += parseFloat(booking.netAmt);
                }
            }
            for (var i =0; i< $scope.service.expenses.length;i++) {
                var expense = $scope.service.expenses[i];
                if(expense.amount && expense.amount != "") {
                    expenseTotal += parseFloat(expense.amount);
                }
            }
            netCashIncome-=expenseTotal;
            $scope.service.netCashIncome = netCashIncome.toFixed(2);
            $scope.service.netIncome = parseFloat($scope.service.netCashIncome) +
                                        parseFloat($scope.service.netRedbusIncome) +
                                        parseFloat($scope.service.netOnlineIncome);
        }
        $scope.addBooking= function() {
            $scope.currentPageOfBookings.push({'index':$scope.currentPageOfBookings.length,'paymentType':"CASH",'name':"service"});
        }
        $scope.deleteBooking= function(booking) {
            $scope.currentPageOfBookings.splice(booking.index,1);
            $scope.calculateNet();
        }
        $scope.addExpense = function(){
            if(!$scope.service.expenses) {
                $scope.service.expenses = [];
            }
            $scope.service.expenses.push({'type':"OTHER",'serviceId':$scope.serviceId,'index':$scope.service.expenses.length+1});
        }
        $scope.deleteExpense = function(expense){
            console.log('deleting expense..' + expense.index);
            $scope.service.expenses.splice(expense.index-1,1);
            //reshiffle the index
            for(var index=0;index<$scope.service.expenses.length; index++) {
                $scope.service.expenses[index].index = index+1;
            }
            $scope.calculateNet();
        }
        $scope.submitReport = function() {
            swal({   title: "Please make sure all the data is accurate?",   text: "You will not be able to edit this form later !",   type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes, submit it!",
                closeOnConfirm: true }, function() {
                serviceReportsManager.submitReport($scope.service, function (response) {
                    //callback(response.data);
                    sweetAlert("Great", "The report successfully submitted", "success");
                    $location.url('serviceReports');
                },function (error) {
                    sweetAlert("Oops...", "Error submitting the report", "error" + angular.toJson(error));
                });
            });
        }
    }).controller('DatepickerPopupCtrl', function ($scope,NgTableParams, $filter,serviceReportsManager, $location) {
        $scope.parseDate = function(){
            $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getUTCMonth())+1)).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
        }
        $scope.today = function() {
            var date = new Date();
            date.setDate(date.getDate() -1);
            $scope.dt = date;
            $scope.parseDate();
        };
        $scope.today();
        $scope.date = null;
        $scope.downloadedOn = null;
        $scope.downloaded = false;
        $scope.loading = false;
        $scope.currentPageOfReports = [];
        $scope.submitted = 0;
        $scope.verified = 0;
        $scope.loading = true;
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.clear = function() {
            $scope.dt = null;
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function() {
            console.log("date changed to "+ $scope.dt);
            $scope.parseDate();
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

        $scope.toggleMin = function() {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function(year, month, day) {
            $scope.dt = new Date(year, month, day);
        };
        $scope.popup1 = {
            opened: false
        };
        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0,0,0,0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
            return '';
        }
        $scope.checkStatus = function() {
            $scope.parseDate();
            serviceReportsManager.getServiceReportStatus($scope.date, function(data){
                $scope.downloaded = data.downloaded;
                $scope.loading = false;
                $scope.downloadedOn=data.downloadedOn;
                if($scope.downloaded) {
                    loadTableData($scope.serviceReportTableParams);
                }
            });
        }
        $scope.checkStatus();
        $scope.downloadReports = function() {
            $scope.loading = true;
            $scope.parseDate();
            serviceReportsManager.downloadReports($scope.date, function(data){
                $scope.downloaded = data.downloaded;
                $scope.loading = false;
                $scope.downloadedOn=data.downloadedOn;
            });
        }
        $scope.nextDay = function() {
            var dt = $scope.dt;
            dt.setDate(dt.getDate()+1);
            $scope.dt = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate());
            $scope.checkStatus();
        }
        $scope.previousDay = function() {
            var dt = $scope.dt;
            dt.setDate(dt.getDate()-1);
            $scope.dt = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate());
            $scope.checkStatus();
        }
        var loadTableData = function (tableParams, $defer) {
           serviceReportsManager.loadReports($scope.date, function(data){
                $scope.allReports = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total($scope.allReports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.allReports);
                }
                $scope.currentPageOfReports = $scope.allReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.serviceReportTableParams = new NgTableParams({
            page: 1,
            count:150,
            sorting: {
                source: 'asc'
            }
        }, {
            total: $scope.currentPageOfReports.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $scope.goToServiceReport = function(id) {
            $location.url('servicereport/' + id, {'idParam':id});
        }
    }).factory('serviceReportsManager', function ($http, $log,$rootScope) {
        var serviceReports = {};
        return {
            getServiceReportStatus:function(date, callback) {
                $http.get('/api/v1/serviceReport/downloadStatus?travelDate='+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving service reports");
                    });
            },
            downloadReports: function (date, callback) {
                $http.get('/api/v1/serviceReport/download?travelDate='+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving service reports");
                    });
            },
            loadReports:function(date,callback) {
                $http.get('/api/v1/serviceReport/load?travelDate='+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading service reports");
                    });
            },
            getReport:function(id,callback) {
                $http.get('/api/v1/serviceReport/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading service reports");
                    });
            },
            submitReport:function(serviceReport,successcallback, errorcallback) {
                $http.post('/api/v1/serviceReport', serviceReport)
                    .then(function (response) {
                        successcallback(response.data);
                    },function (error) {
                        $log.debug("error loading service reports");
                        errorcallback();
                    });
            }
        }
    });



