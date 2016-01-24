'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('busLayoutManager', function ($rootScope, $http, $log, $window, $cacheFactory) {

  var layouts = {};

  return {
    fetchAllBusLayouts: function () {
        $log.debug("fetching layouts data ...");
          $http.get('/api/v1/layouts')
          .success(function (data) {
                layouts = data;
                $rootScope.$broadcast('layoutsInitComplete');
                var cache = $cacheFactory($rootScope.id);
                angular.forEach(layouts, function(layout, key) {
                  cache.put(layout.id, layout);
                })

          })
          .error(function (error) {
            $log.debug("error retrieving layouts");
          });
    },

    getAllData: function () {
      return layouts;
    },

    refreshCache: function() {

    },

    getLayouts: function (callback) {
          $log.debug("fetching layouts data ...");
          $http.get('/api/v1/layouts')
              .success(function (data) {
                callback(data);
              })
              .error(function (error) {
                $log.debug("error retrieving cities");
              });
    },

    getAllLayouts: function () {
        return layouts;
    },

    createLayout : function (layout, callback) {
        $http.post('/api/v1/layout', layout)
          .success(function (data) {
            callback(data);
          })
          .error(function (err) {
            var errorMsg = "error adding new layout info. " + (err && err.error ? err.error : '');
            $log.error(errorMsg);
            alert(errorMsg);
          });
    },
    updateLayout: function(layout,callback) {
     $http.put('/api/v1/layout',layout).success(function (data) {
       callback(data);
       $rootScope.$broadcast('updateLayoutComplete');
     });
   }
  };
});


