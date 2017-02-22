"use strict";
/*global angular, _*/

angular.module('myBus.expensesModule', ['ngTable', 'ui.bootstrap'])
    .controller("ExpensesController",function($rootScope, $scope, $filter, $log,NgTableParams, expensesManager){
        $scope.expenses = [];

        $scope.currentPageOfExpenses=[];

        $scope.expense = {};
        var loadTableData = function (tableParams, $defer) {
            var data = expensesManager.getExpenses();
            if(tableParams) {
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                $scope.expenses = orderedData;
                tableParams.total(data.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfExpenses = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            }
        };
        $scope.$on('amenitiesInitComplete', function (e, value) {
            loadTableData($scope.expensesTableParams);
        });
        $scope.$on('amenitiesinitStart', function (e, value) {
            expensesManager.fechExpenses();
        });
        $scope.expensesTableParams = new NgTableParams(
            {
                page: 1,
                count:50,
                sorting: {
                    name: 'asc'
                }
            },
            {
                total: $scope.currentPageOfExpenses.length,
                getData: function (params) {
                    console.log('sort..');
                    loadTableData(params);
                }
            }
        );
        amenitiesManager.fechAmenities();
        $scope.getAllAmenities = function(){
            amenitiesManager.getAllAmenities(function(data){
                $scope.amenities =data;
            });
        };

        $scope.getAmenityById = function(){
            amenitiesManager.getAmenityByID($scope.amenity.id,function(data){
                $scope.amenity = data;
            });
        }

        $scope.deleteAmenityById = function(amenityID){
            amenitiesManager.deleteAmenity(amenityID,function(data){
                $scope.amenity = data;
            });
        }

        $scope.handleClickAddAmenity = function (size) {
            var modalInstance = $modal.open({
                templateUrl: 'add-Amenity-modal.html',
                controller: 'AddAmenityModalController',
                size: size,
                resolve: {
                    neighborhoodId: function () {
                        return null;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $log.debug("results from modal: " + angular.toJson(data));
                //$scope.cityContentTableParams.reload();
            }, function () {
                $log.debug('Modal dismissed at: ' + new Date());
            });
        };

        $scope.handleClickUpdateAmenity = function(amenityID){
            var modalInstance = $modal.open({
                templateUrl : 'update-amenity-modal.html',
                controller : 'UpdateAmenityModalController',
                resolve : {
                    amenityId : function(){
                        return amenityID;
                    }
                }
            });
        };

        //$scope.getAllAmenities();
    })
// ========================== Modal - Update Amenity  =================================

    .controller('UpdateAmenityModalController', function ($scope, $modalInstance, $http, $log, expensesManager, amenityId) {
        $log.debug("in UpdateAmenityModalController");
        $scope.amenity = {};

        $scope.updateAmenity =function(){
            amenitiesManager.updateAmenity($scope.amenity,function(data){
                $scope.amenity = data;
                $modalInstance.close(data);
            });
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.amenity.name || '') !== '';

        };
        amenitiesManager.getAmenityByID(amenityId,function(data){
            $scope.amenity = data;
        });


        $scope.resetAmenity = function(){
            $scope.amenity= {};
        };
    })

//
// ========================== Modal - Add Amenity =================================
//
    .controller('AddAmenityModalController', function ($scope, $modalInstance,$state, $http, $log, expensesManager) {
        $log.debug("in AddAmenityModalController");

        $scope.amenity = {
            name: null,
            active: false
        };
        $scope.addAmenity = function(){
            amenitiesManager.addAmenity($scope.amenity,function(data){
                $scope.amenity = data;
                $modalInstance.close(data);
            });
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.amenity.name || '') !== '';
        };
    })
    .factory("expensesManager",function($rootScope,$http){
        var expenses = [];
        return {
            fetchExpenses : function(){
                $http.get("/api/v1/expenses").then(function(response){
                    expenses= response.data;
                    $rootScope.$broadcast('expensesLoadComplete');
                },function(error){
                    swal("oops", error, "error");
                });
            },

            getExpenses :function(){
                return expenses;
            },
            getAllExpenses : function(callback){
                $http.get("/api/v1/expenses").then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('expensesLoadComplete');
                },function(error){
                    swal("oops", error, "error");
                });
            },

            addExpense: function(expense,callback) {
                $http.post("/api/v1/expense",expense).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('expensesinitStart');
                    swal("Great", "Exepense has been successfully added", "success");
                },function(error){
                    swal("oops", error, "error");
                })
            },

            getExpenseByID : function(expenseId,callback){
                $http.get("/api/v1/expense/"+expenseId).then(function(response){
                    callback(response.data);
                },function(error){
                    swal("oops", error, "error");
                })
            },

            updateExpense : function(expense,callback){
                $http.put("/api/v1/expense",expense).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('expensesinitStart');
                    swal("Great", "Expense has been updated successfully", "success");
                },function(error){
                    swal("oops", error, "error");
                })
            },
            deleteExpense : function(expenseID,callback){
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this Expense?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){

                    $http.delete("/api/v1/expense/"+expenseID).then(function(data){
                        callback(data);
                        $rootScope.$broadcast('expensesinitStart');
                        swal("Deleted!", "Expense has been deleted successfully!", "success");
                    },function(error){
                        swal("Oops", "We couldn't connect to the server!", "error");
                    })
                })
            }
        }
    });;