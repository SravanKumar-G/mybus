'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('busLayoutManager', function ($rootScope, $http, $log, $window) {

  var layouts = {};

  return {
    fetchAllBusLayouts: function () {
        $log.debug("fetching layouts data ...");
          $http.get('/api/v1/buslayouts')
          .success(function (data) {
                  layouts = data;
          })
          .error(function (error) {
            $log.debug("error retrieving layouts");
          });
    },

    getAllData: function () {
      return layouts;
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
    }
  };
});


