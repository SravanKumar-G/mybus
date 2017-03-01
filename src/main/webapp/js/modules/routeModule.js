/**
 * Created by svanik on 1/19/2016.
 */
"use strict";

angular.module('myBus.routeModule', ['ui.bootstrap'])

    // ==================================================================================================================
    // ====================================    RoutesController   ================================================
    // ==================================================================================================================

    .controller('RoutesController', function ($scope,$rootScope, $http,$uibModal, $log, routesManager,$filter,NgTableParams,$location,cityManager) {
        $log.debug('RoutesController loading');
        $scope.headline = "Routes";
        $scope.allRoutes = [];
        $scope.currentPageOfRoutes = [];

        var loadTableData = function (tableParams, $defer) {
            var data = routesManager.getAllRoutes();
            if(angular.isArray(data)) {
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                $scope.allRoutes = orderedData;
                cityManager.getCities(function (data) {
                    $scope.cities = data;
                    angular.forEach($scope.allRoutes, function (route) {
                        // each route
                        angular.forEach($scope.cities, function (city) {
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
            }
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfRoutes = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.routeContentTableParams = new NgTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfRoutes.length,
            getData: function (params) {
                $scope.$on('RoutesInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

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

        routesManager.fetchAllRoutes();
        $scope.handleClickDeleteRoute = function(passId){
            routesManager.deleteRoute(passId);
        };

        $scope.handleClickAddNewRoute = function(cityId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-route-modal.html',
                controller : 'AddRouteModalController',
                resolve : {
                    passId : function(){
                        return cityId;
                    }
                }
            });
        };

        $scope.handleClickUpdateRoute = function(routeId){
            $rootScope.modalInstance = $uibModal.open({
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

        $scope.cities= [];
        $scope.selectedViaCities = [];
        $scope.selectedViaCity = {};

        $scope.loadFromCities = function(){
            cityManager.getCities(function(data){
                $scope.cities = data;
                $scope.route = {};

                routesManager.getRoute(passId,function(data){
                    $scope.route = data;
                    angular.forEach($scope.cities,function(fromCityName){
                        if(fromCityName.id == $scope.route.fromCity){
                            $scope.routesFromCityName = fromCityName.name;
                        }
                    });
                    angular.forEach($scope.cities,function(toCityName){
                        if(toCityName.id == $scope.route.toCity){
                            $scope.routesToCityName = toCityName.name;
                        }
                    });
                    angular.forEach($scope.route.viaCities,function(existingCityId) {
                        angular.forEach($scope.cities,function(city){
                            if(existingCityId == city.id){
                                $scope.selectedViaCities.push(city);
                            }
                        });
                    });
                });
            });
        };
        $scope.loadFromCities();

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.ok = function () {
            routesManager.updateRoute($scope.route,function(data) {
                $rootScope.modalInstance.close(data);
            });
        };

        $scope.updateFromCity = function(item){
            $scope.routesFromCityName = item.name;
            $scope.route.fromCity= item.id;
        };

        $scope.updateToCity = function(item){
            $scope.routesToCityName = item.name;
            $scope.route.toCity= item.id;
        };

        $scope.addTheCity = function(selectedCity){
            if($scope.route.viaCities.indexOf(selectedCity) == -1){
                $scope.route.viaCities.push(selectedCity);
                cityManager.getCity(selectedCity,function(data){
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
                $scope.selectedViaCities.splice(index,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };

        $scope.moveCallback = function(event,index1,item){
            $scope.selectedViaCities.splice(index1,1);
            console.log($scope.route.viaCities.splice(index1,1));
            console.log("City moved" + angular.toJson($scope.selectedViaCities));
        };

        $scope.insertedCallback = function(index,item){
            $scope.route.viaCities.splice(index,0,item.id);
            console.log("index2:"+index);
            console.log("item2:"+item.id);
            return true;
        };
    })

    .controller('AddRouteModalController', function ($scope, $uibModal, $http, $log, $rootScope, cityManager,routesManager) {

        $scope.fromCitySelected = {};
        $scope.route = {
            name : null,
            viaCities : [],
            fromCity : null,
            toCity : null
        };
        $scope.cities = [];
        $scope.loadFromToCities = function(){
            cityManager.getCities(function(data){
                $scope.cities = data;
            });
        }();

        $scope.selectFromCity = function(item){
            $scope.fromCityName = item.name;
            $scope.route.fromCity= item.id;
            console.log("items:"+angular.toJson(item));
            console.log("id:"+$scope.route.fromCity);
        };

        $scope.selectToCity = function(item, model, label, event){
            $scope.toCityId = item.name;
            $scope.route.toCity= item.id;
        };

        $scope.selectedViaCityId = {};
        $scope.citiesFromService = [];

        $scope.addCityToViaCities = function(viaCityId){
            if($scope.route.viaCities.indexOf(viaCityId)== -1) {
                $scope.route.viaCities.push(viaCityId);
                cityManager.getCity(viaCityId, function (data) {
                    $scope.citiesFromService.push(data);
                });
            }else{
                console.log("city already exist");
            }
        };

        $scope.deleteViaCityFromList = function(cityId){
            var index = $scope.route.viaCities.indexOf(cityId);
            if(index != -1 ){
                $scope.route.viaCities.splice(cityId,1);
                $scope.citiesFromService.splice(index,1);
                console.log("city removed with Id"+cityId);
            }else{
                console.log("city already removed from list");
            }
        };

        $scope.moveCallback = function(event,index1,item){
            console.log("index:"+index1);
            console.log("Before slicing..    " +$scope.route.viaCities);
            $scope.citiesFromService.splice(index1,1);
            console.log($scope.route.viaCities.splice(index1,1));
            console.log("event:"+event);
            console.log("City moved" + angular.toJson($scope.citiesFromService));
            console.log("City id    " +$scope.route.viaCities);
        };

        $scope.insertedCallback = function(index,item){
            $scope.route.viaCities.splice(index,0,item.id);
            return true;
        };

        $scope.ok = function () {
            if ($scope.route.name === null || $scope.route.toCity === null  ) {
                $log.error("nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            console.log(angular.toJson($scope.fromCitySelected));
            routesManager.createRoute($scope.route, function (data) {
                $rootScope.modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

        };
        $scope.routesFromManager=[];
        $scope.onMouseLeave = function(routeName){
            routesManager.getRoutes(function(data){
                $scope.routesFromManager=data;
            });
            angular.forEach($scope.routesFromManager,function(route){
                if(route.name==routeName){
                    swal("oops!","Route already exist","error");
                }
            })
        }
    }).factory('routesManager', function ($rootScope, $http, $log) {

        var routes = {};

        return{
            fetchAllRoutes: function () {
                $log.debug("fetching routes data ...");
                $http.get('/api/v1/routes')
                    .then(function (response) {
                        routes = response.data;
                        $rootScope.$broadcast('RoutesInitComplete');
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },

            getRoutes: function (callback) {
                $log.debug("fetching routes data ...");
                $http.get('/api/v1/routes')
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('FetchingRoutesComplete');
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },

            getActiveRouteNames: function() {
                return $http({
                    method:'GET',
                    //url:'/api/v1/documents/route?fields=id,name'
                    url:'/api/v1/routes'
                });
            },

            getRoute: function(routeId,callback){
                $http.get('/api/v1/route/'+routeId)
                .then(function(response){
                    callback(response.data);
                },function (error) {
                    $log.debug("error retrieving cities");
                });
            },

            getAllRoutes: function () {
                return routes;
            },

            createRoute: function(route,callback){
                $http.post('/api/v1/route',route).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('CreateRouteCompleted');
                },function (err,status) {
                    /*var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                     $log.error(errorMsg);
                     alert(errorMsg);*/
                    sweetAlert("Error",err.message,"error");
                });
            },

            deleteRoute: function(routeId) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this route?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){

                    $http.delete('/api/v1/route/' + routeId).then(function (response) {
                        $rootScope.$broadcast('DeleteRouteCompleted');
                        swal("Deleted!", "Route was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            },
            updateRoute: function(route,callback){
                $http.put('/api/v1/route/'+route.id,route).then(function(response){
                    $rootScope.$broadcast('UpdateRouteCompleted');
                },function(){
                    alert("Error Updating Route");
                });
            }
        }
    });



