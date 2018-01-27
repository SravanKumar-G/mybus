"use strict";
/*global angular, _*/

angular.module('myBus.fuelExpenseReportModule', ['ngTable','ui.bootstrap'])
        .controller("fuelExpenseReportsCtrl",function($scope,$rootScope,NgTableParams,$stateParams,$uibModal, $filter, $location,fuelExpensesServiceManager){
            $scope.loading = false;
            $rootScope.urlDate = $stateParams.date;
            $scope.date = $stateParams.date;
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
                $scope.dt = new Date();
                $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
                $scope.parseDate();
                $scope.reportsByDate($scope.dt)
            };

            if(!$scope.urlDate) {
                $scope.today();
            } else {
                $scope.dt = new Date($scope.urlDate);
                $scope.todayDate = new Date();
                $scope.tomorrow = new Date($scope.todayDate.getTime() + (24 * 60 * 60 * 1000));
            }
            $scope.currentPageOfFuelExpenseReports = [];

            $scope.clear = function() {
                $scope.dt = null;
            };


            $scope.$watch('dt', function(newValue, oldValue) {
                $scope.reportsByDate($scope.dt);
            });


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
            var loadTableData = function (tableParams, $defer) {
                var dateObj = $scope.dt;
                var month = dateObj.getMonth() + 1;
                var day = dateObj.getDate();
                var year = dateObj.getFullYear();
                var newDate = year + "-" + month + "-" + day;

                fuelExpensesServiceManager.getFuelExpenseReports(newDate, function(response){
                    $scope.allReports = response;
                    tableParams.total($scope.allReports.length);
                    if (angular.isDefined($defer)) {
                        $defer.resolve($scope.allReports);
                    }
                    $scope.currentPageOfFuelExpenseReports = $scope.allReports.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                })
            };
            $scope.init = function() {
                $scope.serviceReportTableParams = new NgTableParams({
                    page: 1,
                    count:9999,
                }, {
                    total: $scope.currentPageOfFuelExpenseReports.length,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            };
            $scope.init();

            $scope.$on('reloadFuelExpenses',function(e,value){
                $scope.init();
            });

            $scope.addOrUpdateFuelExpense = function(id) {
                $rootScope.modalInstance = $uibModal.open({
                    templateUrl: 'update-fuelExpense-modal.html',
                    controller: 'editFuelExpenseReportController',
                    resolve : {
                        serviceId : function(){
                            return id;
                        },
                        date:function () {
                            return $scope.date;
                        }
                    }
                })
            }
        })
        .controller("editFuelExpenseReportController",function ($scope,$rootScope,date, fuelExpensesServiceManager,serviceId, fillingStationsManager,serviceReportsManager ) {
            $scope.serviceExpense = {};
            $scope.serviceExpense.journeyDate = new Date();
            $scope.fillingStations = [];

            $scope.serviceList = [];

            serviceReportsManager.getServices(date, function(data){
                $scope.serviceList = _.sortBy(data.data, function(o) { return o.serviceName; });
            });

            if(serviceId) {
                $scope.setFuelExpense = function (serviceId) {
                    fuelExpensesServiceManager.getFuelExpense(serviceId, function (data) {
                        $scope.serviceExpense = data;
                    });
                };
                $scope.setFuelExpense(serviceId);
            }
            fillingStationsManager.getFillingStations(function(fillingStations){
                $scope.fillingStations = fillingStations;
            });

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
                saveFuelExpense: function (serviceExpense) {
                    $http.put('/api/v1/serviceExpense/',serviceExpense)
                        .then(function (response) {
                            $rootScope.modalInstance.close();
                            sweetAlert("Great","Your Fuel Consumption successfully updated", "success");
                            $rootScope.$broadcast('reloadFuelExpenses');
                        },function (err,status) {
                            sweetAlert("Error",err.message,"error");
                        });
                }
            }
        })