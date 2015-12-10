'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');

portalApp.factory('citiesManager', function ($rootScope, $http, $log) {

  var cities = {}
    , rawChildDataWithGeoMap = {};

  return {
    fetchAllCities: function () {
        $log.debug("fetching cities data ...");
          $http.get('/api/v1/cities')
          .success(function (data) {
                  cities = data;
                 $rootScope.$broadcast('cityAndBoardingPointsInitComplete');
          })
          .error(function (error) {
            $log.debug("error retrieving cities");
          });
    },

    getAllData: function () {
      return cities;
    },

    getAllCities: function () {
        return cities;
    },

    countChildrenById: function (parentId) {
      if (rawChildDataWithGeoMap[parentId]) {
        return rawChildDataWithGeoMap[parentId].length;
      }
      return _.reduce(rawDataWithGeo, function (sum, val) {
        if (val) {
          if (val && val.parentId === parentId) {
            return sum + 1;
          }
          return sum;
        }
      }, 0);
    },

    getChildrenByParentId: function (parentId) {
      if (!parentId) {
        return [];
      }
      if (rawChildDataWithGeoMap[parentId]) {
        return rawChildDataWithGeoMap[parentId];
      }
      return _.select(rawDataWithGeo, function (value) {
        return value && value.parentId === parentId;
      });
    },

    getOneById: function (id) {
      return _.first(_.select(cities, function (value) {
        return value.id === id;
      }));
    },
    getCity: function (id, callback) {
      $http.get('/api/v1/city/' + id)
       .success(function (data) {
            callback(data);
       })
       .error(function (error) {
              alert("error finding city. " + angular.toJson(error));
       });
    }
  };
});


