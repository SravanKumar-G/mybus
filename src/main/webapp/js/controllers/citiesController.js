"use strict";
/*global angular, _*/

angular.module('myBus.citiesModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    CitiesController   ================================================
  // ==================================================================================================================

  .controller('CitiesController', function ($scope, $http, $log, ngTableParams, $modal, $filter, cityManager, $location) {
    $log.debug('CitiesController loading');
    $scope.headline = "Cities";
    $scope.allCities = [];
    $scope.currentPageOfCities = [];

    var loadTableData = function (tableParams, $defer) {
      var data = cityManager.getAllCities();
      var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
      $scope.allCities = orderedData;
      tableParams.total(data.length);
      if (angular.isDefined($defer)) {
        $defer.resolve(orderedData);
      }
      $scope.currentPageOfCities = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };

    $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
      loadTableData($scope.cityContentTableParams);
    });

    $scope.goToBoardingPointsList = function (id) {
      $location.url('/city/' + id);
    };
        
    $scope.cityContentTableParams = new ngTableParams({
      page: 1,
      count: 50,
      sorting: {
        state: 'asc',
        name: 'asc'
      }
    }, {
      total: $scope.currentPageOfCities.length,
      getData: function ($defer, params) {
        $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
          loadTableData(params);
        });
      }
    });
    cityManager.fetchAllCities();

    $scope.handleClickAddStateCity = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            $scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
  })
//
    // ========================== Modal - Add City, State  =================================
    //
    .controller('AddStateCityModalController', function ($scope, $modalInstance, $http, $log, cityManager) {
        $scope.city = {
            name: null,
            state: null
        };
        $scope.ok = function () {
            if ($scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $modalInstance.close(null);
            }
            cityManager.createCity($scope.city, function(data){
                $modalInstance.close(data);
            });
        };
        
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                ($scope.city.state || '') !== '';
        };


    });

