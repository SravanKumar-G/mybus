
"use strict";

 angular.module('myBus.routeModule', ['ui.bootstrap'])

// ============================================================================================================= //
// ====================================    Routes  Controller    =============================================== //
// ============================================================================================================= //

    .controller('RoutesController', function ($scope,$rootScope, $http,$uibModal, $log, routesManager,$filter,NgTableParams,$location,cityManager) {
        $log.debug('RoutesController loading');
        $scope.headline = "Routes";
        $scope.route = {};
        $scope.currentPageOfRoutes = [];
        var loadTableData = function (tableParams) {
            var data = routesManager.getRoutes(function (data) {
                    if (angular.isArray(data)) {
                        $scope.allRoutes = data;
                        cityManager.getCities(function (info) {
                            $scope.cities = info;
                            angular.forEach($scope.allRoutes, function (route) {
                                // for each route
                                angular.forEach($scope.cities.content, function (city) {
                                    // for each city
                                    if (city.id == route.fromCityId) {
                                        route.attributes.fromCity = city.name;
                                    }
                                    if (city.id == route.toCityId) {
                                        route.attributes.toCity = city.name;
                                    }
                                });

                            });
                        });
                    }
                $scope.route = tableParams.sorting () ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                tableParams.total(data.length);
                tableParams.data = $scope.route;
                $scope.currentPageOfRoutes = $scope.route.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());

            })

        };

        $scope.init = function(){
            //routesManager.count(function(routesCount){
                $scope.routeContentTableParams = new NgTableParams({
                page: 1,
                count: 10,
                sorting: {
                    name: 'asc'
                }
                }, {
                    counts: [],
                    total: $scope.currentPageOfRoutes.length,
                    getData: function (params) {
                               loadTableData(params);
                    }
                    //  });
                });
        };
        $scope.init();
        $scope.$on('RoutesInitComplete', function (e, value) {
           loadTableData($scope.routeContentTableParams);
        });

        routesManager.fetchAllRoutes();
        $scope.handleClickDeleteRoute = function(passId){
            routesManager.deleteRoute(passId);
        };

        $scope.handleClickAddNewRoute = function(cityId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-route-modal.html',
                controller : 'AddUpdateRouteModalController',
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
                controller : 'AddUpdateRouteModalController',
                resolve : {
                    passId : function(){
                        return routeId;
                    }
                }
            });
        };
    })
    // ============================================================================================================= //
    // ================================    Routes  Add and Update Controller   ===================================== //
    // ============================================================================================================= //
    .controller('AddUpdateRouteModalController', function ($document,$scope, $uibModalInstance, $http, $log,cityManager, routesManager, passId,$rootScope) {

        $scope.cities = [];
        $scope.selectedViaCities = [];
        $scope.selectedViaCity = {};
        $scope.route = {
            name: null,
            viaCities: [],
            fromCityId: null,
            toCityId: null
        };

        if (passId){
            $scope.loadFromCities = function () {
                $scope.route = {};
                routesManager.getRoute(passId, function (data) {
                    $scope.route = data;
                    angular.forEach($scope.cities, function (fromCityName) {
                        if (fromCityName.id == $scope.route.fromCityId) {
                            $scope.routesFromCityName = fromCityName.name;
                        }
                    });
                    angular.forEach($scope.cities, function (toCityName) {
                        if (toCityName.id == $scope.route.toCityId) {
                            $scope.routesToCityName = toCityName.name;
                        }
                    });
                    angular.forEach($scope.route.viaCities, function (existingCityId) {
                        angular.forEach($scope.cities, function (city) {
                            if (existingCityId == city.id) {
                                $scope.selectedViaCities.push(city);
                            }
                        });
                    });
                });
            };
            $scope.loadFromCities();
        }
        else {
            console.log("Adding new route initiation");
        }
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.addTheCity = function (selectedCity) {
            if ($scope.route.viaCities.indexOf(selectedCity) == -1) {
                $scope.route.viaCities.push(selectedCity);
                cityManager.getCity(selectedCity, function (data) {
                    $scope.selectedViaCities.push(data);
                });
            }
            else {
                swal("Oops", "city already Added", "error");
            }
        };

        $scope.deleteCityFromList = function (cityId) {
            var index = $scope.route.viaCities.indexOf(cityId);
            if (index != -1) {
                $scope.route.viaCities.splice(index, 1);
                $scope.selectedViaCities.splice(index, 1);
                console.log("city removed with Id" + cityId);
            }
            else {
                console.log("city already removed from list");
                swal("Oops", "city already removed from list", "error");
            }
        };
        $scope.routesFromManager = [];
        $scope.onMouseLeave = function (Name) {
            routesManager.getRoutes(function (data) {
                $scope.routesFromManager = data;
            });
            angular.forEach($scope.routesFromManager, function (route) {
                if (route.name == Name) {
                    swal("oops!", "Route already exist", "error");
                }
            })
        };
        $scope.loadCities = function () {
            cityManager.getCities(function (data) {
                $scope.cities = data.content;
            });
        };
        $scope.loadCities();

        $scope.ok = function () {
            if (passId) {
                routesManager.updateRoute($scope.route, function (data) {
                    $rootScope.modalInstance.close(data);
                });

            }

            else
            {
                if ($scope.route.name == null || $scope.route.toCityId == null  ) {
                    $log.error("nothing was added.");
                    $rootScope.modalInstance.close(null);
                }
                else{
                    routesManager.createRoute($scope.route, function (data) {
                        $rootScope.modalInstance.close(data);
                    });
                }

            }
        }

    })

    // ============================================================================================================= //
    // ====================================    Routes  Manager (Service)   ========================================= //
    // ============================================================================================================= //

    .factory('routesManager', function ($rootScope, $http, $log,cityManager) {

        var routes = {};

        return{
            fetchAllRoutes: function () {
                $log.debug("fetching routes data no callback...");
                $http.get('/api/v1/routes')
                    .then(function (response) {
                        routes = response.data;
                        $rootScope.$broadcast('RoutesInitComplete');
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },

            getRoutes: function (callback) {
                $log.debug("fetching routes data  withh callback...");
                $http.get('/api/v1/routes')
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('RoutesComplete');
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

            count: function (callback) {
                $http.get('/api/v1/route/count')
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving route count");
                    });
            },
            createRoute: function(route,callback) {
                $http.post('/api/v1/route', route).then(function (response) {
                    callback(response.data);
                    $rootScope.$broadcast('RoutesInitComplete');
                }, function (err, status) {
                    sweetAlert("Error", err.message, "error");
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
                        $rootScope.$broadcast('RoutesInitComplete');
                        swal("Deleted!", "Route was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            },
            updateRoute: function(route,callback){
                $http.put('/api/v1/route/'+route.id,route).then(function(response){
                    callback(response.data);
                    $rootScope.$broadcast('RoutesInitComplete');
                },function(){
                    sweetAlert("Error","Error Updating Route");
                });
            }
        }
    });

