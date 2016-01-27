/**
 * Created by svanik on 1/19/2016.
 */
"use strict";

angular.module('myBus.routesModules', ['ui.bootstrap'])

    // ==================================================================================================================
    // ====================================    RoutesController   ================================================
    // ==================================================================================================================

    .controller('RoutesController', function ($scope, $http,$modal, $log, routesManager,$filter,ngTableParams,$location,cityManager) {

        $log.debug('RoutesController loading');
        $scope.headline = "Routes";
        $scope.allRoutes = [];
        $scope.currentPageOfRoutes = [];

        var loadTableData = function (tableParams, $defer) {
            var data = routesManager.getAllRoutes();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.allRoutes = orderedData;

            cityManager.getCities(function(data){
                $scope.cities = data;

                angular.forEach($scope.allRoutes,function(route) {
                    // each route
                    angular.forEach($scope.cities,function(city){
                        // for each city
                        if (city.id == route.fromCity) {
                            route.fromCity = city.name;
                        }

                        if (city.id == route.toCity) {
                            route.toCity = city.name;
                        }
                    });

                });

            });
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfRoutes = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('RoutesInitComplete', function (e, value) {
            loadTableData($scope.routeContentTableParams);
        });

        $scope.$on('CreateRouteCompleted',function(e,value){
            routesManager.fetchAllRoutes();
        });

        $scope.$on('FetchingRoutesComplete',function(e,value){
            routesManager.fetchAllRoutes();
        });

        $scope.$on('DeleteRouteCompleted',function(e,value) {
            routesManager.fetchAllRoutes();
        });

        $scope.$on('UpdateRouteCompleted',function(e,value) {
            routesManager.fetchAllRoutes();
        });

        $scope.routeContentTableParams = new ngTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfRoutes.length,
            getData: function ($defer, params) {
                $scope.$on('RoutesInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        routesManager.fetchAllRoutes();

       $scope.handleClickAddNewRoute = function(cityId){
            var modalInstance = $modal.open({
                templateUrl : 'add-route-modal.html',
                controller : 'AddRouteModalController',
                resolve : {
                    passId : function(){
                        return cityId;
                    }
                }
            });
        };

        $scope.handleClickDeleteRoute = function(routeId){
            var modalInstance = $modal.open({
                templateUrl : 'delete-route-modal.html',
                controller : 'DeleteRouteModalController',
                resolve : {
                    passId : function(){
                        return routeId;
                    }
                }
            });
        };

        $scope.handleClickUpdateRoute = function(routeId){
            var modalInstance = $modal.open({
                templateUrl : 'update-route-modal.html',
                controller : 'UpdateRouteModalController',
                resolve : {
                    passId : function(){
                        return routeId;
                    }
                }
            });
        };
    })

    .controller('UpdateRouteModalController', function ($document,$scope, $modalInstance, $http, $log,cityManager, routesManager, passId,$rootScope) {

        console.log("in UpdateStateCityModalController");
        $scope.cities= [];
        $scope.selectedViaCities = [];
        $scope.selectedViaCity = {};

        $scope.loadFromCities = function(){
            cityManager.getCities(function(data){
                $scope.cities = data;
                $scope.route = {};
                routesManager.getRoute(passId,function(data){
                    $scope.route = data;
                    console.log("loading route and viacities = " +angular.toJson($scope.route));
                    console.log(angular.toJson($scope.cities));
                    angular.forEach($scope.route.viaCities,function(existingCityId) {
                        console.log("in route loop");
                        angular.forEach($scope.cities,function(city){
                            console.log("in cities loop");
                            if(existingCityId == city.id){
                                $scope.selectedViaCities.push(city);
                                //$scope.existingCityNames = city.name;
                                console.log("assigned city name");
                            }
                        });
                    });
                });
            });
        };
        $scope.loadFromCities();

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.ok = function () {
            routesManager.updateRoute($scope.route,function(data) {
               $modalInstance.close(data);
            });
        };

        $scope.addTheCity = function(selectedCity){
            console.log("Before adding: current cities" + $scope.route.viaCities);
            if($scope.route.viaCities.indexOf(selectedCity) == -1){
                $scope.route.viaCities.push(selectedCity);
                cityManager.getCity(selectedCity,function(data){
                    console.log("got city with Id");
                    $scope.selectedViaCities.push(data);
                });
            }else{
                console.log("city already added");
            }
        };

        $scope.deleteCityFromList = function(cityId){
            var index = $scope.route.viaCities.indexOf(cityId);
            if(index != -1 ){
                $scope.route.viaCities.splice(index,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };
    })

    .controller('AddRouteModalController', function ($scope, $modalInstance, $http, $log, cityManager,routesManager) {
        $scope.route = {
            name : null,
            viaCities : [],
            fromCity : null,
            toCity : null
        };
        $scope.cities = [];
        $scope.loadFromToCities = function(){
            console.log("test");
            cityManager.getCities(function(data){
                $scope.cities = data;
            });
        }();

         //$scope.loadFromToCities();
        $scope.selectedViaCityId = {};
        $scope.cityGotFromId = [];

        $scope.addCityToViaCities = function(viaCityId){
            if($scope.route.viaCities.indexOf(viaCityId)== -1) {
                $scope.route.viaCities.push(viaCityId);
                cityManager.getCity(viaCityId, function (data) {
                    $scope.cityGotFromId.push(data);
                });
            }else{
                console.log("city already exist");
            }
        };

        $scope.deleteViaCityFromList = function(cityId){
            var index = $scope.route.viaCities.indexOf(cityId);
            if(index != -1 ){
                $scope.route.viaCities.splice(cityId,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };

        $scope.ok = function () {
            if ($scope.route.name === null || $scope.route.toCity === null  ) {
                $log.error("nothing was added.");
                $modalInstance.close(null);
            }
            routesManager.createRoute($scope.route, function (data) {
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

        };
    })

    .controller('DeleteRouteModalController',function($scope,$http,routesManager,$modalInstance,$log,passId) {
                $scope.routeId = passId;

                $scope.ok = function (passId) {
                    routesManager.deleteRoute(passId);
                    $modalInstance.close();
                    console.log("Route deleted");
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };

    });


