
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
        $scope.count = 0;
        $scope.showInvalid = false;
        $scope.query = "";


        var loadTableData = function (tableParams) {
                var sortingProps = tableParams.sorting();
                var sortProps = "";
                for(var prop in sortingProps) {
                    sortProps += prop+"," +sortingProps[prop];
                }
                $scope.loading = true;
                var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
                routesManager.getRoutes($scope.query,$scope.showInvalid,pageable, function(data){
                    if (angular.isArray(data.content)) {
                        $scope.allRoutes = data.content;
                        cityManager.getActiveCityNames(function (info) {
                            $scope.cities = info;
                            angular.forEach($scope.allRoutes, function (route) {
                                // for each route
                                angular.forEach($scope.cities, function (city) {
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

                    $scope.loading = false;
                    $scope.routes = data.content;
                    tableParams.total(data.totalElements);
                    $scope.count = data.totalElements;
                    tableParams.data = $scope.routes;
                    $scope.currentPageOfRoutes =  $scope.routes;
            })

        };

        $scope.init = function(){
            routesManager.count(function(routesCount){
                $scope.routeContentTableParams = new NgTableParams({
                page: 1,
                count: 10,
                sorting: {
                    name: 'asc'
                }
                }, {
                    counts: [],
                    total: routesCount,
                    getData: function (params) {
                               loadTableData(params);
                    }
                     });
                });
        };
        $scope.init();
        $scope.$on('RoutesInitComplete', function (e, value) {
                $scope.init();
        });

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
        var pageable;
        $scope.onMouseLeave = function (Name) {
            routesManager.getRoutes($scope.query,$scope.showInvalid,pageable, function (data) {
                $scope.routesFromManager = data;
            });
            angular.forEach($scope.routesFromManager, function (route) {
                if (route.name == Name) {
                    swal("Route already exist", "error");
                }
            })
        };
        $scope.loadCities = function () {
            cityManager.getActiveCityNames(function (data) {
                $scope.cities = data;
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
            getRoutes: function (query, showInvalid, pageable, callback) {
                $http({url:'/api/v1/routes?query='+query+"&showInvalid="+showInvalid,method: "GET",params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving Routes");
                    });
            },

            getActiveRouteNames: function() {
                return $http({
                    method:'GET',
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
                $http.get('/api/v1/routes/count')
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving route count");
                    });
            },
            createRoute: function(route,callback) {
                $http.post('/api/v1/route', route).then(function (response) {
                    callback(response.data);
                    swal("Added!", "Route was successfully Added!", "success");
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

                    $http.delete('/api/v1/route/' + routeId).then(function () {
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
                    swal("Updated!", "Route was successfully Updated!", "success");
                    $rootScope.$broadcast('RoutesInitComplete');
                },function(){
                    sweetAlert("Error","Error Updating Route");
                });
            }
        }
    });

