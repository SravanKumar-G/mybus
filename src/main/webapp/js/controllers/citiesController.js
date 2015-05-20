"use strict";
/*global angular, _*/

angular.module('myBus.citiesModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    CitiesController   ================================================
  // ==================================================================================================================

  .controller('CitiesController', function ($scope, $http, $log, ngTableParams, $modal, $filter, citiesManager, $location) {
    $log.debug('CitiesController loading');

    $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

    $scope.headline = "Cities";

    $scope.allCities = [];
    $scope.currentPageOfCities = [];
    $scope.citiesManager = citiesManager;

    var loadTableData = function (tableParams, $defer) {
      var data = citiesManager.getAllCities()
        , orderedData = null;

      angular.forEach(data, function (val) {
        val.childrenCount = 0;
      });

      orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
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


    $scope.goToNeighborhoodsList = function (id) {
      $location.url('/cities/' + id);
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
    citiesManager.fetchAllCities();

    $scope.handleClickAddStateCity = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'neighborhood-add-city-state-modal.html',
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
            citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
            //$scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
  })
//
    // ========================== Modal - Add City, State  =================================
    //
    .controller('AddStateCityModalController', function ($scope, $modalInstance, $http, $log, citiesManager) {

        $scope.neighborhood = {
            name: null,
            state: null
        };

        $scope.ok = function () {
            if ($scope.neighborhood.name === null || $scope.neighborhood.state === null) {
                $log.error("null city or state.  nothing was added.");
                $modalInstance.close(null);
            }

            $http.post('/api/v1/city', $scope.neighborhood)
                .success(function (data) {
                    $log.info("added new city info: " + angular.toJson($scope.neighborhood));
                    $modalInstance.close(data);
                    citiesManager.fetchAllCities();
                })
                .error(function (err) {
                    var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                    $log.error(errorMsg);
                    alert(errorMsg);
                });
        };
        
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.neighborhood.name || '') !== '' &&
                ($scope.neighborhood.state || '') !== '';
        };


    })

    // ==================================================================================================================
    // =================================     NeighborhoodsListController    =============================================
    // ==================================================================================================================

    .controller('NeighborhoodsListController', function ($scope, $routeParams, $http, $location, $log, ngTableParams, $modal, $filter, citiesManager) {

        //$log.debug('NeighborhoodsListController loading');

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.allNeighborhoods = [];
        $scope.currentPageOfNeighborhoods = [];
        $scope.cityId = $routeParams.id;
        $scope.currentNeighborhood = null;
        $scope.breadCrumbs = [];

        $scope.headline = "";

        $scope.citiesAndNeighborhoodsManager = citiesAndNeighborhoodsManager;

        $scope.poiCountEnabled = $location.search()['poiCount'];
        $scope.recalculatePOICount = function () {
            $log.debug("recalculatePOICount");
            var updateOperations = [];
            $scope.citiesAndNeighborhoodsManager.getAllData().forEach(function (nei) {
                if (nei.name === '(PENDING)') {
                    return;
                }
                updateOperations.push(function (cb) {
                    $log.debug("getting POI count for " + nei.name);
                    $http.get('/api/v1/npc?neighGeoId=' + nei.id + '&isPortal=1')
                        .success(function (data) {
                            $log.debug("poi count response: " + angular.toJson(data));
                            nei.poiCount = data.poi_count;
                            cb(null, data);
                        })
                        .error(function (err) {
                            nei.poiCount = err;
                            cb("error. " + err, err);
                        });
                });
            });
            async.series(updateOperations, function () {
                $log.debug("all done with POI count update.");
            });
        };

        var updateHeadline = function () {
            if ($scope.currentNeighborhood) {
                $scope.headline = "Neighborhoods for " + $scope.currentNeighborhood.name;
            } else {
                $scope.headline = "Neighborhoods...";
            }
        };

        var updateBreadcrumbModel = function () {
            var cityAndNeighborhoodChain = citiesAndNeighborhoodsManager.getBreadcrumbDescendants($scope.currentNeighborhood, []);
            cityAndNeighborhoodChain.pop();
            $scope.breadCrumbs = cityAndNeighborhoodChain;
        };

        var loadTableData = function (tableParams, $defer) {
            var data = citiesAndNeighborhoodsManager.getChildrenByParentId($scope.currentNeighborhoodId)
                , orderedData = null;

            angular.forEach(data, function (val) {
                val.childrenCount = citiesAndNeighborhoodsManager.countChildrenById(val.id);
            });

            orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            $scope.allNeighborhoods = orderedData;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfNeighborhoods = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
            $scope.currentNeighborhood = citiesAndNeighborhoodsManager.getOneById($scope.currentNeighborhoodId);
            updateHeadline();
            loadTableData($scope.neighborhoodContentTableParams);
            updateBreadcrumbModel();
        });


        $scope.handleDeleteButtonClicked = function (id) {
            if (!id) {
                var errorMsg = "no id was specified.  city will not be deleted.";
                $log.error(errorMsg);
                alert(errorMsg);
                return;
            }
            $http.delete('/api/v1/neighborhoodGeo/' + id)
                .success(function (data) {
                    $location.url('/cities/' + ($scope.currentNeighborhood.parentId || ''));
                })
                .error(function (error) {
                    alert("error deleting neighborhood.  " + angular.toJson(error));
                });
        };

        $scope.formatGeoJSON = function (neighborhood) {
            if (neighborhood && neighborhood.geometry && neighborhood.geometry.type === "Polygon" && _.isArray(neighborhood.geometry.coordinates)) {
                return (neighborhood.geometry.coordinates[0].length - 1) + " sided polygon";
            }
            return '---';
        };

        $scope.goToNeighborhoodsList = function (neighborhood) {
            $location.url('/cities/' + neighborhood.id);
        };


        $scope.neighborhoodContentTableParams = new ngTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfNeighborhoods.length,
            getData: function ($defer, params) {
                $scope.$on('cityAndNeighborhoodInitComplete', function (e, value) {
                    updateHeadline();
                    loadTableData(params, $defer);
                });
            }
        });

        citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();

        $scope.handleClickAddNeighborhood = function () {
            var modalInstance = $modal.open({
                templateUrl: 'add-neighborhood-to-city-state-modal.html',
                controller: 'NeighborhoodAddToExistingStateCityModalController',
                size: 'sm',
                resolve: {
                    neighborhood: function () {
                        return $scope.currentNeighborhood;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $log.debug("results from modal: " + angular.toJson(data));
                citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData($scope.currentNeighborhoodId);
            }, function () {
                $log.debug('Modal dismissed at: ' + new Date());
            });
        };


    });
