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
    });
