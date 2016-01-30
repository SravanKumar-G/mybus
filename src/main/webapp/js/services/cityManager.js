'use strict';
/*global angular, _*/

var portalApp = angular.module('myBus');
portalApp.factory('cityManager', function ($rootScope, $http, $log, $window) {
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
    getCities: function (callback) {
      $log.debug("fetching cities data ...");
      $http.get('/api/v1/cities')
          .success(function (data) {
            callback(data);
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
    createCity : function (city, callback) {
      $http.post('/api/v1/city', city)
          .success(function (data) {
            callback(data);
              sweetAlert('Your City has been successfully added', 'success');
            this.fetchAllCities();
          })
          .error(function (err) {
            var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
            $log.error(errorMsg);
              sweetAlert("Error Message","error"+errorMsg);
          });
    },
    getCity: function (id, callback) {
      $http.get('/api/v1/city/' + id)
          .success(function (data) {
              callback(data);
              $rootScope.$broadcast('BoardingPointsInitComplete');
          })
          .error(function (error) {
            alert("error finding city. " + angular.toJson(error));
          });
    },
    deleteCity: function(id, callback) {
      $http.delete('/api/v1/city/' + id)
          .success(function (data) {
            callback(data);
              sweetAlert('Your City has been successfully deleted', 'success');
            $window.location = "#/cities";
          })
          .error(function (error) {
              sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
          });
    },
    updateCity: function(city,callback) {
      $http.put('/api/v1/city/'+city.id,city).success(function (data) {
        callback(data);
          sweetAlert('Your City has been successfully updated', 'success');
        $rootScope.$broadcast('updateCityCompleteEvent');
      }).error(function (error) {
          sweetAlert("Oops...", "Error updating City data!", "error" + angular.toJson(error));
          })
    },
    //----------------------------------------------------------------------
    createBordingPoint: function (cityId,boardingPoint,callback) {
      $http.post('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
          sweetAlert('Your BoardingPoint has been successfully added', 'success');
      }).error(function () {
          sweetAlert("Oops...", "Error creating Bp data!", "error");
      });
    },
    updateBp: function(cityId,boardingPoint,callback) {
      $http.put('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
          sweetAlert('Your BoardingPoint has been successfully updated', 'success');
       // $rootScope.$broadcast('updateBpCompleteEvent');
      }).error(function () {
          sweetAlert("Oops...", "Error updating Bp data!", "error");
      });
    },
    deleteBp: function(cityId,BpId,callback) {
      $http.delete('/api/v1/city/'+cityId+'/boardingpoint/'+BpId).success(function (data) {
        callback(data);
          sweetAlert('Your BoardingPoint has been successfully deleted', 'success');
        //$rootScope.$broadcast('deleteBpCompleteEvent');
      }).error(function () {
          sweetAlert("Oops...", "Error deleting Bp data!", "error");
      });
    },
    getBp: function (id,BpId, callback) {
      $http.get('/api/v1/city/'+id+'/boardingpoint/'+BpId)
          .success(function (data) {
            callback(data);
          })
          .error(function (error) {
              sweetAlert("Oops...", "Error finding BoardingPoint data!", "error" + angular.toJson(error));
          });
    },
  }
});


