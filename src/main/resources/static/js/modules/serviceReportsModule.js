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
    .controller('ServiceReportController', function($rootScope, $scope,$state,$stateParams, $filter, NgTableParams, $location, serviceReportsManager, userManager, agentManager,staffManager) {
        $scope.headline = "Service Report";
        $scope.service = {};
        $scope.downloaded = false;
        $scope.serviceId = $stateParams.id;
        $scope.currentPageOfBookings = [];
        $scope.allBookings = [];
        $scope.agents = [];
        $scope.differenceAmountRatio = 99;
        $scope.allStaff = [];
        $scope.addedStaff = [];

        $scope.onlineBookingTypes = $rootScope.operatorAccount && $rootScope.operatorAccount.onlineBookingTypes?$rootScope.operatorAccount.onlineBookingTypes.split(","):[];

        agentManager.getNames(function(names){
            $scope.agents = names;
        });

        $scope.currentUser = userManager.getUser();
        var loadTableData = function (tableParams, $defer) {
            serviceReportsManager.getReport($scope.serviceId, function(data){
                $scope.service = data;
                if($scope.service.requiresVerification === undefined) {
                    $scope.service.requiresVerification = false;
                }
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
            return booking.paymentType === 'ONLINE' ||  booking.paymentType === 'REDBUS';
        }

        $scope.isCashBooking = function(booking) {
            return booking.paymentType === 'CASH';
        }
        $scope.rateToBeVerified=function(booking) {
            return booking.requireVerification || (booking.netAmt < (booking.originalCost*$scope.differenceAmountRatio/100));
        }
        $scope.calculateNet = function(changedBooking) {
            $scope.service.netCashIncome = 0;
            var expenseTotal = 0;
            //if the net amount is 13% less than original cost
            if(changedBooking){
                if( Math.abs(changedBooking.netAmt) < (changedBooking.originalCost*$scope.differenceAmountRatio/100)) {
                    changedBooking.requireVerification = true;
                } else {
                    changedBooking.requireVerification = false;
                }
                var bookingsToBeVerified = _.find($scope.service.bookings, function(booking) {
                    return booking.requireVerification ===true;
                });
                $scope.service.requiresVerification = bookingsToBeVerified != null;
            }

            for (var i =0; i< $scope.currentPageOfBookings.length;i++) {
                var booking = $scope.currentPageOfBookings[i];
                if ($scope.isCashBooking(booking) && booking.netAmt && booking.netAmt != "") {
                    $scope.service.netCashIncome += parseFloat(booking.netAmt);
                }
            }
            for (var i =0; i< $scope.service.expenses.length;i++) {
                var expense = $scope.service.expenses[i];
                if(expense.amount && expense.amount != "") {
                    expenseTotal += parseFloat(expense.amount);
                }
            }
            $scope.service.netCashIncome -= expenseTotal;
             if($scope.service.luggageIncome){
                $scope.service.netCashIncome += parseFloat($scope.service.luggageIncome);
            }
            if($scope.service.advance){
                $scope.service.netCashIncome += parseFloat($scope.service.advance);
            }
            if($scope.service.onRoadServiceIncome){
                $scope.service.netCashIncome += parseFloat($scope.service.onRoadServiceIncome);
            }
            if($scope.service.otherIncome){
                $scope.service.netCashIncome += parseFloat($scope.service.otherIncome);
            }
            $scope.service.netCashIncome = $scope.service.netCashIncome.toFixed(2);
            $scope.service.netIncome = (parseFloat($scope.service.netCashIncome) +
            parseFloat($scope.service.netRedbusIncome) +
            parseFloat($scope.service.netOnlineIncome)).toFixed(2);

            for (var i =0; i< $scope.currentPageOfBookings.length;i++) {
                var booking = $scope.currentPageOfBookings[i];
                if (booking.due) {
                    $scope.service.netCashIncome -= parseFloat(booking.netAmt);
                }
            }
        }
        $scope.getFuelCost=function() {
            if($scope.service && $scope.service.serviceExpense){
                return $scope.service.serviceExpense.fuelCost;
            }
        }
        $scope.getNetRealization=function() {
            if($scope.service){
                return  $scope.service.netRealization;
            }
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
            $scope.currentPageOfBookings.push({'index':$scope.currentPageOfBookings.length, 'paymentType':"CASH","ticketNo":"SERVICE"});
            $scope.service.bookings = $scope.currentPageOfBookings;
        }
        $scope.deleteBooking= function(booking) {
            $scope.currentPageOfBookings =  _.filter($scope.currentPageOfBookings, function(thisBooking) {
                return thisBooking.index !== booking.index;
            });
            $scope.service.bookings = $scope.currentPageOfBookings;
            $scope.calculateNet();
        }
        $scope.addExpense = function(){
            if(!$scope.service.expenses) {
                $scope.service.expenses = [];
            }
            $scope.service.expenses.push({'type':"EXPENSE",'index':$scope.service.expenses.length+1});
        }
        $scope.addStaff = function(){
            console.log($scope.newStaffId);
            var newStaffObj = _.find($scope.allStaff, function (s) { return s.id ==$scope.newStaffId; });
            if(!$scope.service.staff) {
                $scope.service.staff = [];
            }
            $scope.service.staff.push(newStaffObj);
        }
        $scope.deleteStaff = function(staff){
            console.log('deleting staff');
            $scope.service.staff  = _.without($scope.service.staff , _.findWhere($scope.service.staff , {
                id: staff.id
            }));
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
            //if the report is ready to be submitted i.e. !requiresVerification
            if(!$scope.service.status) {
                $scope.service.status = "SUBMITTED"
            }
            $scope.submitReport();
        }

        $scope.canSubmit = function() {
            if($rootScope.operatorAccount.skipAgentValidity === true){
                $scope.service.invalid = false;
                return true;
            }
            if($scope.service.status==='REQUIRE_VERIFICATION') {
                if($scope.currentUser.canVerifyRates){
                    return true;
                }
            } else if(!$scope.service.requiresVerification){
                return (!$scope.service.invalid && !$scope.service.status);
            }
            return false;
        }
        $scope.requireVerification = function(){
            return !$scope.service.invalid && $scope.service.requiresVerification && $scope.service.status !== "SUBMITTED";
        }
        $scope.submitReport = function() {
            serviceReportsManager.submitReport($scope.service, function (response) {
                sweetAlert("Great", "The report successfully submitted", "success");
                window.history.back();
                //$scope.goToServiceForm($scope.service);
            },function (error, message) {
                $scope.service.status = null;
                swal("Oops...", "Error submitting the report :"+ error.data.message, "error");
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
        staffManager.getStaffList(null, function(response){
           $scope.allStaff = response.content;
        });



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

        $scope.$watch('dt', function(newValue, oldValue) {
            $scope.serviceReportByDate($scope.dt);
        });

        $scope.checkStatus = function() {
            $scope.parseDate();
            loadServicesData();
            loadTableData($scope.serviceReportTableParams);
        }

        var loadTableData = function (tableParams, $defer) {
            $scope.loading = true;

            serviceReportsManager.loadReports($scope.date, function(data){
                $scope.loading = false;

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
            },function() {
                $scope.loading = false;
            })
        };

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


        $scope.nextDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
            $scope.dt.setTime(dt.getTime());//new Date(dt.getFullYear(), dt.getUTCMonth() ,dt.getDate());
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            } else{
                $scope.serviceReportByDate($scope.dt);
            }
        }
        $scope.previousDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
            $scope.serviceReportByDate(dt);
        }

        var loadServicesData = function (tableParams, $defer) {
            serviceReportsManager.getServices($scope.date, function(data){
                $scope.serviceList = _.sortBy(data.data, function(o) { return o.serviceName; });
             })
        };

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
        $scope.getPassengerReport = function(serviceId){
            serviceReportsManager.getPassengerReport($scope.date, serviceId, function () {
                console.log("Downloaded the report");
            });
        };
    })

    .controller('pendingReportController', function($scope, serviceReportsManager,NgTableParams,$filter,$location){
        $scope.headline = "Pending Reports";
        $scope.reports = [];
        var loadPendingData = function (tableParams, $defer) {
            serviceReportsManager.pendingReports(function(data){
                $scope.pendingReports = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total($scope.pendingReports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.pendingReports);
                }
                $scope.reports = $scope.pendingReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
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
                total: $scope.reports.length,
                getData: function (params) {
                    loadPendingData(params);
                }
            });
        };
        $scope.init();
        $scope.$on('ReportDownloaded',function(e,value){
            $scope.init();
        });
    }).controller('ReportsToBeReviewedController', function($scope, serviceReportsManager,NgTableParams,$filter,$location){
        $scope.headline = "Reports To Be Reviewed";
        $scope.reports = [];
        var loadPendingData = function (tableParams, $defer) {
            serviceReportsManager.reportsToBeReviewed(function(data){
                $scope.reports = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total($scope.reports.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.reports);
                }
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
                total: $scope.reports.length,
                getData: function (params) {
                    loadPendingData(params);
                }
            });
        };
        $scope.init();
        $scope.$on('ReportDownloaded',function(e,value){
            $scope.init();
        });
    }).controller('ServiceIncomeReportController', function($scope, serviceReportsManager,NgTableParams, $filter){
        $scope.headline = "Reports To Be Reviewed";
        $scope.cities = [];
        $scope.filter = {};
        $scope.serviceReports = [];
        serviceReportsManager.getDistinctSource(function (data) {
            $scope.cities = data;
        })
        var loadPendingData = function (tableParams, $defer) {
            serviceReportsManager.getServiceIncomeReports($scope.filter, function(data){
                tableParams.total( $scope.serviceReports.length);
                var orderedData= tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                $scope.serviceReports = orderedData;
                if (angular.isDefined($defer)) {
                    $defer.resolve($scope.serviceReports);
                }
            })
        };

        $scope.search = function() {
            $scope.submitted = 0;
            $scope.serviceReportParams = new NgTableParams({
                page: 1,
                size:9999,
                count:9999,
                sorting: {
                    source: 'desc'
                }
            }, {
                total:  $scope.serviceReports.length,
                getData: function (params) {
                    loadPendingData(params);
                }
            });
        };


    }).factory('serviceReportsManager', function ($http, $log, $rootScope) {
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
            loadReports:function(date,callback, errorcallback) {
                $http.get('/api/v1/serviceReport/loadReports?travelDate='+date)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        errorcallback();
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
            reportsToBeReviewed:function(callback) {
                $http.get('/api/v1/serviceReport/toBeReviewed')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error loading toBeReviewed service reports");
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
                        errorcallback(error);
                    });
            },getServices:function(date,callback) {
                $http.get('api/v1/serviceListings/activeListings?travelDate='+date)
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
            },
            getDistinctSource:function(callback) {
                $http.get('api/v1/serviceReport/getCities')
                .then(function (response) {
                    callback(response.data);
                },function (error) {
                    $log.debug("error loading unique city names");
                })
            },
            getServiceIncomeReports:function(query,successcallback, errorcallback) {
                $http.post('/api/v1/serviceReport/incomeReport', query)
                    .then(function (response) {
                        successcallback(response.data);
                    },function (error) {
                        $log.debug("error loading service reports");
                        errorcallback(error);
                    });
            }

        }
    });


