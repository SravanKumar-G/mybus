"use strict";
/*global angular, _*/

angular.module('myBus.fuelExpenseReportModule', ['ngTable','ui.bootstrap'])
        .controller("fuelExpenseReportsCtrl",function($scope,$rootScope,NgTableParams,$stateParams,$uibModal, $filter, $location,fillingStationsManager, fuelExpensesServiceManager,paginationService){
            $scope.loading = false;
            $rootScope.urlDate = $stateParams.date;
            $scope.date = $stateParams.date;
            $scope.fillingStations = [];
            $scope.dayTotalBill = 0;

            $scope.parseDate = function(){
                $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getUTCMonth()+1))).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
            };

            $scope.reportsByDate = function(date){
                var dateObj = date;
                var month = dateObj.getMonth() + 1;
                var day = dateObj.getDate();
                var year = dateObj.getFullYear();
                var newdate = year + "-" + month + "-" + day;
                $location.url('fuelexpensereports/' + newdate);
            }
            $scope.today = function() {
                var yesterday = new Date();
                yesterday.setDate(yesterday.getDate() -1);
                $scope.dt = yesterday;
                $scope.dt2 = new Date();
                $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
                $scope.parseDate();
                $scope.reportsByDate($scope.dt)
            };

            if(!$scope.urlDate) {
                $scope.today();
            } else {
                $scope.dt = new Date($scope.urlDate);
                $scope.dt2 = new Date($scope.urlDate);
                $scope.todayDate = new Date();
                $scope.tomorrow = new Date($scope.todayDate.getTime() + (24 * 60 * 60 * 1000));
            }
            $scope.nextDay = function() {
                var dt = $scope.dt;
                dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
                $scope.dt.setTime(dt.getTime());
                if ($scope.dt >= $scope.tomorrow) {
                    swal("Oops...", "U've checked for future, Check Later", "error");
                }
                else {
                    $scope.reportsByDate($scope.dt)
                    $scope.init();
                }
            }
            $scope.previousDay = function() {
                var dt = $scope.dt;
                dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
                $scope.dt = dt;
                if ($scope.dt >= $scope.tomorrow) {
                    swal("Oops...", "U've checked for future, Check Later", "error");
                }
                else {
                    $scope.reportsByDate($scope.dt)
                    $scope.init();
                }
            }
            var loadTableData = function (tableParams) {
                var dateObj = $scope.dt;
                var month = dateObj.getMonth() + 1;
                var day = dateObj.getDate();
                var year = dateObj.getFullYear();
                var newDate = year + "-" + month + "-" + day;
                fuelExpensesServiceManager.getFuelExpenseReports(newDate, function(response){
                    $scope.allReports = response;
                    if(tableParams.sorting()){
                        if(tableParams.orderBy()[0]){
                            var orderBy = tableParams.orderBy()[0].slice(1);
                            var orderDir = tableParams.orderBy()[0].slice(0, 1) === '+' ? 1 : -1;
                            $scope.allReports = _.sortBy($scope.allReports, function(report){
                                return report[orderBy];
                            });
                            if(orderDir === -1){
                                $scope.allReports = $scope.allReports.reverse();
                            }
                        }
                    }
                    $scope.dayTotalBill = _.reduce($scope.allReports, function(memo, bill) { return memo + bill.fuelCost}, 0);
                    console.log('total '+ $scope.dayTotalBill );
                });
            };
            $scope.init = function() {
                $scope.fuelExpensesParams = new NgTableParams({
                    page: 1,
                    count:9999,
                }, {
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            };
            $scope.init();

            $scope.$watch('dt', function(newValue, oldValue) {
                $scope.init();
            });

            $scope.$on('reloadFuelExpenses', function (e, value) {
                $scope.init();
            });

            $scope.addOrUpdateFuelExpense = function(id) {
                $rootScope.modalInstance = $uibModal.open({
                    templateUrl: 'update-fuelExpense-modal.html',
                    controller: 'editFuelExpenseReportController',
                    resolve : {
                        serviceExpenseId : function(){
                            return id;
                        },
                        date:function () {
                            return $scope.date;
                        },
                        addedServiceExpenseIds: function() {
                            return $scope.allReports.map(a => a.serviceListingId);
                        }
                    }
                })
            }
            $scope.deleteFuelExpense = function (expenseId) {
                fuelExpensesServiceManager.deleteFuelExpense(expenseId,function(){
                    $scope.init();
                });
            }

            $scope.search = function(){
                $scope.query = {
                    "startDate" : $scope.dt.getFullYear()+"-"+[$scope.dt.getMonth()+1]+"-"+$scope.dt.getDate(),
                    "endDate" :  $scope.dt2.getFullYear()+"-"+[$scope.dt2.getMonth()+1]+"-"+$scope.dt2.getDate(),
                    "fillingStationId" : $scope.fillingStationId
                }
                $scope.searchInit();
            }

            $scope.query = {};
            $scope.totalBill = 0;
            var searchFuelBills = function (tableParams) {
                $scope.loading = true;
                fuelExpensesServiceManager.search($scope.query, function(response){
                    $scope.loading = false;
                    $scope.fuelBills = response;
                    tableParams.data = $scope.fuelBills;
                    $scope.totalBill = _.reduce($scope.fuelBills, function(memo, bill) { return memo + bill.fuelCost}, 0);
                    if(tableParams.sorting()){
                        if(tableParams.orderBy()[0]){
                            var orderBy = tableParams.orderBy()[0].slice(1);
                            var orderDir = tableParams.orderBy()[0].slice(0, 1) === '+' ? 1 : -1;
                            $scope.fuelBills = _.sortBy($scope.fuelBills, function(report){
                                return report[orderBy];
                            });
                            if(orderDir === -1){
                                $scope.fuelBills = $scope.fuelBills.reverse();
                            }
                        }
                    }
                });
            };

            $scope.searchInit = function (){
                $scope.searchTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:15,
                    sorting: {
                        date: 'asc'
                    },
                }, {
                    counts:[],
                    getData: function (params) {
                        searchFuelBills(params);
                    }
                });
            }

            $scope.exportToExcel = function (tableId, fileName) {
                paginationService.exportToExcel(tableId, fileName);
            }

        })
        .controller("editFuelExpenseReportController",function ($scope,$rootScope,date, addedServiceExpenseIds, fuelExpensesServiceManager,serviceExpenseId, fillingStationsManager,serviceReportsManager ) {
            $scope.serviceExpense = {};
            $scope.serviceExpense.journeyDate = date;
            $scope.fillingStations = [];
            $scope.selectedListing = {};
            $scope.serviceList = [];
            serviceReportsManager.getServices(date, function(data){
                $scope.serviceList = _.sortBy(data.data, function(o) { return o.serviceName; });
                //filter the services for which the expense has been already added.
                $scope.serviceList = _.filter($scope.serviceList, function(listing) {
                    return addedServiceExpenseIds.indexOf(listing.id) === -1;
                });
                if(serviceExpenseId) {
                    fuelExpensesServiceManager.getFuelExpense(serviceExpenseId, function (data) {
                        $scope.serviceExpense = data;
                        $scope.selectedListing = _.find($scope.serviceList, function(listing) {
                            return listing.id == $scope.serviceExpense.serviceListingId;
                        });
                    });
                }
            });

            fillingStationsManager.getFillingStations(function(fillingStations){
                $scope.fillingStations = fillingStations;
            });

            $scope.listingChanged = function() {
                $scope.serviceExpense.serviceListingId =  $scope.selectedListing.id;
                $scope.serviceExpense.vehicleNumber = $scope.selectedListing.vehicleRegNumber;
            };
            $scope.ok = function () {
                if ($scope.updateFuelExpenseForm.$invalid) {
                    swal("Error!", "Please fix the errors in the form", "error");
                    return;
                }
                fuelExpensesServiceManager.saveFuelExpense($scope.serviceExpense, function (data) {
                    $rootScope.modalInstance.close(data);
                });
            };

            $scope.getFuelCost = function() {
                if($scope.serviceExpense) {
                    $scope.serviceExpense.fuelCost = $scope.serviceExpense.fuelQuantity * $scope.serviceExpense.fuelRate;
                }
                return $scope.serviceExpense.fuelCost;
            }
            $scope.cancel = function () {
                $rootScope.modalInstance.dismiss('cancel');
            };
        })
        .factory('fuelExpensesServiceManager',function ($http,$log,$rootScope) {

            return{
                search: function (query,callback) {
                    $http.post('/api/v1/serviceExpense/search', query).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching expenses", err.message, "error");
                    });
                },
                getFuelExpenseReports: function (date,callback) {
                    $http.get('/api/v1/serviceExpense/byDate?travelDate='+date)
                        .then(function (response) {
                            callback(response.data);
                        },function (error) {
                            $log.debug("error retrieving Fuel Expense reports");
                        });
                },
                getFuelExpense: function (id,callback) {
                    $http.get('/api/v1/serviceExpense/'+id)
                        .then(function (response) {
                            callback(response.data);
                        },function (error) {
                            $log.debug("error retrieving Fuel Expense reports");
                        });
                },
                deleteFuelExpense: function (id,callback) {
                    $http.delete('/api/v1/serviceExpense/'+id)
                        .then(function (response) {
                            callback(response.data);
                        },function (error) {
                            $log.debug("error deleting Fuel Expense");
                        });
                },
                saveFuelExpense: function (serviceExpense) {
                    $http.put('/api/v1/serviceExpense/',serviceExpense)
                        .then(function (response) {
                            $rootScope.$broadcast('reloadFuelExpenses');
                            $rootScope.modalInstance.close();
                            sweetAlert("Great","Your Fuel Consumption successfully updated", "success");
                        },function (err,status) {
                            sweetAlert("Error",err.message,"error");
                        });
                }
            }
        })