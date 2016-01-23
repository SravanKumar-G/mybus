/**
 * Created by svanik on 1/20/2016.
 */

var portalApp = angular.module('myBus');

portalApp.factory('routesManager', function ($rootScope, $http, $log, $window) {

    var routes = {};

    return{
        fetchAllRoutes: function () {
            $log.debug("fetching routes data ...");
            $http.get('/api/v1/routes')
                .success(function (data) {
                    routes = data;
                    $rootScope.$broadcast('RoutesInitComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving cities");
                });
        },

        getRoutes: function (callback) {
            $log.debug("fetching routes data ...");
            $http.get('/api/v1/routes')
                .success(function (data) {
                    callback(data);
                    $rootScope.$broadcast('FetchingRoutesComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving cities");
                });
        },

        getRoute: function(routeId,callback){
             $http.get('/api/v1/route/'+routeId).success(function(data){
                 callback(data);
             })
             .error(function (error) {
                  $log.debug("error retrieving cities");
             });
        },

        getAllRoutes: function () {
            return routes;
        },

        createRoute: function(route,callback){
            $http.post('/api/v1/route',route).success(function(data){
                callback(data);
                $rootScope.$broadcast('CreateRouteCompleted');
                //this.fetchAllRoutes();
            })
                .error(function (err) {
                    var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                    $log.error(errorMsg);
                    alert(errorMsg);
                });
        },

        deleteRoute: function(routeId){
            $http.delete('/api/v1/route/'+routeId).success(function(data){
               $rootScope.$broadcast('DeleteRouteCompleted');
            })
                .error(function(){
                    alert("Error deleting Route");
                });
        },

        updateRoute: function(route,callback){
            $http.put('/api/v1/route/'+route.id,route).success(function(data){
                $rootScope.$broadcast('UpdateRouteCompleted');
            })
                .error(function(){
                    alert("Error Updating Route");
                });
        }

    }

});