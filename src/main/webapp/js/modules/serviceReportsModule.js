/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.serviceReportsModule', ['ngTable', 'ngAnimate', 'ui.bootstrap'])

//
// ============================= List All ===================================
//
    .controller('ServiceReportsController', function($scope,$rootScope,$stateParams,$location) {
        $scope.headline = "Service Reports";
        $rootScope.urlDate = $stateParams.date;

    })
    .controller('ServiceFormController', function($scope,$state, $http, $log, $stateParams,$filter, NgTableParams, $location, serviceReportsManager) {
        $scope.headline = "Service Form";
        $scope.service = {};
        $scope.formId = $stateParams.id;
        $scope.allBookings = [];
        $scope.currentPageOfBookings = [];
        var loadTableData = function (tableParams, $defer) {
            serviceReportsManager.getForm($scope.formId, function (data) {
                $scope.service = data;
                $scope.downloaded = true;
                $scope.allBookings = tableParams.sorting() ? $filter('orderBy')($scope.service.bookings, tableParams.orderBy()) : $scope.service.bookings;
                tableParams.total($scope.allBookings.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.allBookings);
                }
                $scope.currentPageOfBookings = $scope.allBookings.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());

            });
        }
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

    })
    .controller('ServiceReportController', function($scope,$state,$stateParams, $filter, NgTableParams, $location, serviceReportsManager, userManager, agentManager) {
        $scope.headline = "Service Report";
        $scope.service = {};
        $scope.downloaded = false;
        $scope.serviceId = $stateParams.id;
        $scope.currentPageOfBookings = [];
        $scope.allBookings = [];
        $scope.agents = [];
        agentManager.getNames(function(names){
            $scope.agents = names;
        });
        $scope.currentUser = userManager.getUser();
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
                $scope.countSeats();
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
        //loadTableData($scope.bookingsTableParams);
        $scope.isOnlineBooking = function(booking) {
            return !$scope.isNotOnlineBooking(booking);
        }
        $scope.isNotOnlineBooking = function(booking) {
            return booking.bookedBy !='ONLINE' && booking.bookedBy !='REDBUS-API'
                && booking.bookedBy !='PAYTM-API'
                && booking.bookedBy !='YATRA-API'
                && booking.bookedBy !='abhibus-mantis'
                && booking.bookedBy !='ABHIBUS';
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
            $scope.service.netIncome = (parseFloat($scope.service.netCashIncome) +
            parseFloat($scope.service.netRedbusIncome) +
            parseFloat($scope.service.netOnlineIncome)).toFixed(2);
            for (var i =0; i< $scope.currentPageOfBookings.length;i++) {
                var booking = $scope.currentPageOfBookings[i];
                if (booking.due) {
                    netCashIncome -= parseFloat(booking.netAmt);
                }
            }
            $scope.service.netCashIncome = netCashIncome.toFixed(2);
        }
        $scope.countSeats = function(){
            var seatsCount = 0;
            for (var i =0; i< $scope.currentPageOfBookings.length;i++) {
                if($scope.currentPageOfBookings[i].seats){
                    seatsCount +=$scope.currentPageOfBookings[i].seats.split(",").length;
                }
            }
            $scope.service.totalSeats = seatsCount;
        }
        $scope.addBooking= function() {
            $scope.currentPageOfBookings.push({'index':$scope.currentPageOfBookings.length, 'paymentType':"CASH"});
            $scope.service.bookings = $scope.currentPageOfBookings;
        }
        $scope.deleteBooking= function(booking) {
            $scope.currentPageOfBookings.splice(booking.index,1);
            $scope.service.bookings = $scope.currentPageOfBookings;
            $scope.calculateNet();
        }
        $scope.addExpense = function(){
            if(!$scope.service.expenses) {
                $scope.service.expenses = [];
            }
            $scope.service.expenses.push({'type':"EXPENSE",'index':$scope.service.expenses.length+1});
        }
        $scope.deleteExpense = function(expense){
            $scope.service.expenses.splice(expense.index-1,1);
            //reshiffle the index
            for(var index=0;index<$scope.service.expenses.length; index++) {
                $scope.service.expenses[index].index = index+1;
            }
            $scope.calculateNet();
        }
        $scope.submit = function() {
            $scope.service.status = "SUBMITTED";
            $scope.submitReport();

        }
        $scope.submitReport = function() {
            serviceReportsManager.submitReport($scope.service, function (response) {
                sweetAlert("Great", "The report successfully submitted", "success");
                window.history.back();
                //$scope.goToServiceForm($scope.service);
            },function (error) {
                swal("Oops...", "Error submitting the report", "error");
            });

        }
        $scope.goToServiceForm = function(service) {
            if(service.attributes.formId) {
                $location.url('serviceform/' + service.attributes.formId);
            } else {
                $location.url('servicereport/' + service.id);
            }
        }
        $scope.haltService = function() {
            $scope.service.status = "HALT";
            $scope.submitReport();
        }
        $scope.launchAgents = function(){
            $location.url('/agents');
        }
    })
    .controller('DatepickerPopupCtrl', function ($scope,$stateParams,NgTableParams,$rootScope, $filter,serviceReportsManager, $location ,userManager) {
        $scope.headline = "Service Reports";
        $scope.urlDate = $stateParams.date;


        $scope.parseDate = function(){
            $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getMonth()+1))).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
        }
        $scope.serviceReportByDate = function(date){
            var dateObj = date;
            var month = dateObj.getMonth() + 1;
            var day = dateObj.getDate();
            var year = dateObj.getFullYear();
            var newdate = year + "-" + month + "-" + day;
            $location.url('serviceReports/' + newdate);
        }

        $scope.today = function() {
            var date = new Date();
            date.setDate(date.getDate() -1);
            $scope.dt = date;
            $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
            $scope.parseDate();
            $scope.serviceReportByDate($scope.dt);
        };

        if(!$scope.urlDate) {
            $scope.today();
        } else {
            $scope.dt = new Date($scope.urlDate);
            $scope.todayDate = new Date();
            $scope.tomorrow = new Date($scope.todayDate.getTime() + (24 * 60 * 60 * 1000));
        }
        $scope.date = null;
        $scope.downloadedOn = null;
        $scope.downloaded = false;
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
            if ($scope.dt >= $scope.tomorrow) {
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else {
                $scope.serviceReportByDate($scope.dt);
                $scope.checkStatus();
            }
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
            loadServicesData();
            serviceReportsManager.getServiceReportStatus($scope.date, function(data){
                $scope.downloaded = data.downloaded;
                $scope.loading = false;
                $scope.downloadedOn=data.downloadedOn;
                if($scope.downloaded) {
                    loadTableData($scope.serviceReportTableParams);
                }
            });
        }

        $scope.downloadReports = function() {
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else{
                $scope.loading = true;
                $scope.parseDate();
                serviceReportsManager.downloadReports($scope.date, function(data){
                    $scope.downloaded = data.downloaded;
                    $scope.loading = false;
                    $scope.downloadedOn=data.downloadedOn;
                });
            }
        }

        $scope.refreshReports = function() {
            $scope.loading = true;
            $scope.parseDate();
            serviceReportsManager.refreshReports($scope.date, function(data){
                $scope.downloaded = data.downloaded;
                $scope.loading = false;
                $scope.downloadedOn=data.downloadedOn;
            });
        }
        $scope.isAdmin = function() {
            var currentUser = userManager.getUser();
            return currentUser.admin;
        }
        $scope.monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];

        $scope.nextDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
            $scope.dt.setTime(dt.getTime());//new Date(dt.getFullYear(), dt.getUTCMonth() ,dt.getDate());
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else{
                $scope.serviceReportByDate($scope.dt);
                $scope.init();
            }
        }
        $scope.previousDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
            $scope.dt = dt;// new Date(dt.getFullYear(), dt.getUTCMonth() ,dt.getDate());
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else{
                $scope.serviceReportByDate($scope.dt);
                $scope.init();
            }
        }
        var loadTableData = function (tableParams, $defer) {
            serviceReportsManager.loadReports($scope.date, function(data){
                $scope.allReports = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total($scope.allReports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.allReports);
                }
                $scope.currentPageOfReports = $scope.allReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                if( $scope.submitted == 0) {
                    angular.forEach($scope.currentPageOfReports, function (service) {
                        if (service.status == "SUBMITTED") {
                            $scope.submitted = $scope.submitted + 1;
                        }
                    });
                }
            })
        };

        var loadServicesData = function (tableParams, $defer) {
            serviceReportsManager.getServices($scope.date, function(data){
                $scope.serviceList = _.sortBy(data.data, function(o) { return o.serviceName; });

             })
        };

        var loadPassengerData = function (tableParams, $defer) {
            serviceReportsManager.getPassengerReport($scope.date, $scope.serviceIds, function(data){
                $scope.downloaded = true;
                $scope.allReports = tableParams.sorting() ? $filter('orderBy')(data.data, tableParams.orderBy()) : data.data;
                tableParams.total($scope.allReports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.allReports);
                }
                $scope.currentPageOfReports = $scope.allReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                if( $scope.submitted == 0) {
                    angular.forEach($scope.currentPageOfReports, function (service) {
                        if (service.status == "SUBMITTED") {
                            $scope.submitted = $scope.submitted + 1;
                        }
                    });
                }
                
                $scope.currentPageOfReports = data.data;
             })
        };
        
        $scope.getPassengerReport = function(serviceIds) {
        	$scope.serviceIds = serviceIds;
        	loadPassengerData($scope.serviceReportTableParams);
        }
        


        $scope.init = function() {
            $scope.submitted = 0;
            $scope.serviceReportTableParams = new NgTableParams({
                page: 1,
                count:9999,
                sorting: {
                    source: 'asc'
                }
            }, {
                total: $scope.currentPageOfReports.length,
                getData: function (params) {
                    $scope.checkStatus();
                }
            });
        };


        $scope.init();
        $scope.$on('ReportDownloaded',function(e,value){
            $scope.init();
        });

        $scope.goToServiceReport = function(service) {
            if(service.attributes.formId) {
                $location.url('serviceform/' + service.attributes.formId);
            } else {
                $location.url('servicereport/' + service.id);
            }
        }
    })

    .controller('pendingReportController', function($scope, serviceReportsManager,NgTableParams,$filter,$location){
        $scope.headline = "Pending Reports";
        $scope.currentPageOfPendingReports = [];
        var loadPendingData = function (tableParams, $defer) {
            serviceReportsManager.pendingReports(function(data){
                $scope.pendingReports = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total($scope.pendingReports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.pendingReports);
                }
                $scope.currentPageOfPendingReports = $scope.pendingReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            })
        };

        $scope.goToServiceReport = function(service) {
            if(service.attributes.formId) {
                $location.url('serviceform/' + service.attributes.formId);
            } else {
                $location.url('servicereport/' + service.id);
            }
        }
        $scope.init = function() {
            $scope.submitted = 0;
            $scope.pendingTableParams = new NgTableParams({
                page: 1,
                count:9999,
                sorting: {
                    jdate: 'desc'
                }
            }, {
                total: $scope.currentPageOfPendingReports.length,
                getData: function (params) {
                    loadPendingData(params);
                }
            });
        };
        $scope.init();
        $scope.$on('ReportDownloaded',function(e,value){
            $scope.init();
        });
    })
    .factory('serviceReportsManager', function ($http, $log, $rootScope) {
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
                        $rootScope.$broadcast('ReportDownloaded');
                    },function (error) {
                        $log.debug("error retrieving service reports");
                    });
            },
            refreshReports: function (date, callback) {
                $http.get('/api/v1/serviceReport/refresh?travelDate='+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error refreshing service reports");
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
            pendingReports:function(callback) {
                $http.get('/api/v1/serviceReport/pending')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading pending service reports");
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
            getBooking:function(id,callback) {
                $http.get('api/v1/serviceReport/booking/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading booking form");
                    });
            },
            getForm:function(id,callback) {
                $http.get('/api/v1/serviceForm/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading service form");
                    });
            },
            submitReport:function(serviceReport,successcallback, errorcallback) {
                $http.post('/api/v1/serviceReport', serviceReport)
                    .then(function (response) {
                        $rootScope.$broadcast('UpdateHeader');
                        successcallback(response.data);
                    },function (error) {
                        $log.debug("error loading service reports");
                        errorcallback();
                    });
            },getServices:function(date,callback) {
                $http.get('api/v1/services/active?travelDate='+date)
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error loading services for date");
                });
            },getPassengerReport:function(date, serviceIds,callback) {
                $http.get('api/v1/serviceReport/downloadServices?travelDate='+date+'&serviceNum='+serviceIds)
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error loading services for date");
                });
            }
        }
    });


