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
    createCity : function (city,callback) {
      $http.post('/api/v1/city',city)
          .success(function (data) {
            callback(data);
              swal("Great", "Your City has been successfully added", "success");
          }).error(function(err,status) {
              sweetAlert("Error",err.message,"error");

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
      deleteCity: function(id) {

          swal({   title: "Are you sure?",   text: "You will not be able to recover this City !",   type: "warning",
                  showCancelButton: true,
                  confirmButtonColor: "#DD6B55",
                  confirmButtonText: "Yes, delete it!",
                  closeOnConfirm: false }, function() {
              $http.delete('/api/v1/city/' + id)
                  .success(function (data) {
                      //callback(data);
                      sweetAlert("Great", "Your City has been successfully deleted", "success");
                      $window.location = "#/cities";
                  })
                  .error(function (error) {
                      sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                  });
          });
              },
    updateCity: function(city,callback) {
      $http.put('/api/v1/city/'+city.id,city).success(function (data) {
        callback(data);
          sweetAlert("Great","Your City has been successfully updated", "success");
        $rootScope.$broadcast('updateCityCompleteEvent');
      }).error(function (error) {
          sweetAlert("Oops..", "Error updating City data!", "error" + angular.toJson(error));
          })
    },
    //----------------------------------------------------------------------
    createBordingPoint: function (cityId,boardingPoint,callback) {
      $http.post('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
          sweetAlert("Great","Your BoardingPoint has been successfully added", "success");
      }).error(function (err,status) {
          sweetAlert("Error",err.message,"error");
      });
    },
    updateBp: function(cityId,boardingPoint,callback) {
      $http.put('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).success(function (data) {
        callback(data);
          sweetAlert("Great","Your BoardingPoint has been successfully updated", "success");
       // $rootScope.$broadcast('updateBpCompleteEvent');
      }).error(function () {
          sweetAlert("Oops...", "Error updating Bp data!", "error");
      });
    },
    deleteBp: function(cityId,BpId,callback) {
      swal({   title: "Are you sure?",   text: "You will not be able to recover this BoardingPoint !",   type: "warning",
          showCancelButton: true,
          confirmButtonColor: "#DD6B55",
          confirmButtonText: "Yes, delete it!",
          closeOnConfirm: false }, function() {
              $http.delete('/api/v1/city/'+cityId+'/boardingpoint/'+BpId)
              .success(function (data) {
                  callback(data);
                  sweetAlert("Great", "Your BoardingPoint has been successfully deleted", "success");
                      $rootScope.$broadcast('deleteBpCompleteEvent');
              })
              .error(function (error) {
                  sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
              });
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


