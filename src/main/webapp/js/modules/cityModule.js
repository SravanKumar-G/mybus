"use strict";
/*global angular, _*/

angular.module('myBus.cityModule', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    CitiesController   ================================================
  // ==================================================================================================================
    .controller('CitiesController', function ($scope, $uibModal, $http, $log, NgTableParams, $filter, cityManager, $location, $rootScope) {
        $log.debug('CitiesController loading');
        $scope.headline = "Cities";
        $scope.allCities = [];
        $scope.currentPageOfCities = [];

        var loadTableData = function (tableParams, $defer) {
            var data = cityManager.getAllCities();
            if(angular.isArray(data)) {
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                 $scope.allCities = orderedData;
                tableParams.total(data.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfCities = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            }
        };
        $scope.$on('updateCityCompleteEvent', function (e, value) {
            cityManager.fetchAllCities();
            //loadTableData($scope.cityContentTableParams);
        });

        $scope.$on('cityAndBoardingPointsInitComplete', function (e, value) {
            loadTableData($scope.cityContentTableParams);
        });

        $scope.goToBoardingPointsList = function (id) {
            $location.url('/city/' + id);
        };

        $scope.cityContentTableParams = new NgTableParams({
            page: 1,
            count:50,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfCities.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        cityManager.fetchAllCities();

//---------------------------------------------------------------------------------------------------------------------
    $scope.handleClickAddStateCity = function (size) {
        $rootScope.modalInstance = $uibModal.open({
            templateUrl: 'add-city-state-modal.html',
            controller: 'AddStateCityModalController',
            size: size,
            resolve: {
                neighborhoodId: function () {
                    return null;
                }
            }
        });
        $rootScope.modalInstance.result.then(function (data) {
            $log.debug("results from modal: " + angular.toJson(data));
            $scope.cityContentTableParams.reload();
        }, function () {
            $log.debug('Modal dismissed at: ' + new Date());
        });
    };
    $scope.handleClickUpdateStateCity = function(cityId){
        $rootScope.modalInstance = $uibModal.open({
            templateUrl : 'update-city-state-modal.html',
            controller : 'UpdateStateCityModalController',
            resolve : {
                passId : function(){
                    return cityId;
                }
            }
        });
    };

  })
    // ========================== Modal - Update City, State  =================================

    .controller('UpdateStateCityModalController', function ($scope, $state, $uibModal, $http, $log, cityManager, passId, $rootScope) {
        $scope.city = {};
        $scope.displayCity = function(data){
            $scope.city = data;
        };
        $scope.setCityIntoModal = function(passId){
            cityManager.getCity(passId,$scope.displayCity);
        };
        $scope.setCityIntoModal(passId);
        $scope.ok = function () {
            if ($scope.city.id === null || $scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager.updateCity($scope.city, function (data) {
                $state.transitionTo('cities');
                $rootScope.modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                ($scope.city.state || '') !== '';
        };
    })

//
    // ========================== Modal - Add City, State  =================================
    //
    .controller('AddStateCityModalController', function ($scope, $rootScope,$uibModal,$state, $http, $log, cityManager) {
        $scope.city = {
            name: null,
            state: null
        };
        $scope.ok = function () {
            if ($scope.city.name === null || $scope.city.state === null) {
                $log.error("null city or state.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager.createCity($scope.city, function(data){
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            });
        };
        
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {
            return ($scope.city.name || '') !== '' &&
                    ($scope.city.state || '') !== '';
        };

    }).factory('cityManager', function ($rootScope, $http, $log, $location) {
        var cities = {}
            , rawChildDataWithGeoMap = {};
        return {
            fetchAllCities: function () {
                $http.get('/api/v1/cities')
                    .then(function (response) {
                        cities = response.data.content;
                        $log.debug("fetching cities data ..." + response.data.content);

                        $rootScope.$broadcast('cityAndBoardingPointsInitComplete');
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },
            getCities: function (callback) {
                $http.get('/api/v1/cities')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },
            getActiveCityNames:function(callback) {
                $http.get('/api/v1/activeCityNames')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
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
            getCityName: function (id) {
                return _.first(_.select(cities, function (value) {
                    return value.id === id;
                })).name;
            },
            createCity : function (city,callback) {
                $http.post('/api/v1/city',city)
                    .then(function (response) {
                        callback(response.data);
                        swal("Great", "Your City has been successfully added", "success");
                    },function(err,status) {
                        sweetAlert("Error",err.data.message,"error");
                    });
            },
            getCity: function (id, callback) {
                $http.get('/api/v1/city/' + id)
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('BoardingPointsInitComplete');
                    },function (error) {
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
                        .then(function (response) {
                            //callback(response.data);
                            sweetAlert("Great", "Your City has been successfully deleted", "success");
                           $location.url("/cities");
                        },function (error) {
                            sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                        });
                });
            },
            updateCity: function(city,callback) {
                $http.put('/api/v1/city/'+city.id,city).then(function (response) {
                    callback(response.data);
                    sweetAlert("Great","Your City has been successfully updated", "success");
                    $rootScope.$broadcast('updateCityCompleteEvent');
                },function (error) {
                    console.log(JSON.stringify(error));
                    sweetAlert("Oops..", "Error updating City data!", "error" + angular.toJson(error));
                })
            },
            //----------------------------------------------------------------------
            createBordingPoint: function (cityId,boardingPoint,callback) {
                $http.post('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).then(function (response) {
                    callback(response.data);
                    sweetAlert("Great","Your BoardingPoint has been successfully added", "success");
                },function (err,status) {
                    sweetAlert("Error",err.data.message,"error");
                });
            },
            updateBp: function(cityId,boardingPoint,callback) {
                $http.put('/api/v1/city/'+cityId+'/boardingpoint',boardingPoint).then(function (response) {
                    callback(response.data);
                    sweetAlert("Great","Your BoardingPoint has been successfully updated", "success");
                    // $rootScope.$broadcast('updateBpCompleteEvent');
                },function () {
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
                        .then(function (response) {
                            callback(response.data);
                            sweetAlert("Great", "Your BoardingPoint has been successfully deleted", "success");
                            $rootScope.$broadcast('deleteBpCompleteEvent');
                        },function (error) {
                            sweetAlert("Oops...", "Error finding City data!", "error" + angular.toJson(error));
                        });
                });
            },
            getBp: function (id,BpId, callback) {
                $http.get('/api/v1/city/'+id+'/boardingpoint/'+BpId)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        sweetAlert("Oops...", "Error finding BoardingPoint data!", "error" + angular.toJson(error));
                    });
            },
            getBoardingPoints: function (cityId,callback) {
                $http.get('/api/v1/city/'+cityId+'/boardingpoint/')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        sweetAlert("Oops...", "Error loading BoardingPoint data!", "error" + angular.toJson(error));
                    });
            }
        }
    }).controller('DateRangePickerCtrl', function ($scope) {

        $scope.clearStart = function () {
            $scope.dateRangeStart = null;
        };

        $scope.clearEnd = function () {
            $scope.$parent.dateRangeEnd = null;
        };

        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };
        $scope.toggleMin();

        $scope.openStart = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.startOpened = true;
        };
        $scope.openEnd = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.endOpened = true;
        };

        $scope.minDate = new Date('2014-08-01');
        $scope.maxDate = new Date();
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[1];

        $scope.dateOptions = {
            initDate: new Date()
        }
    }).controller('BoardingPointsListController', function ($scope, $http,$uibModal, $log, NgTableParams,$state,$stateParams, $filter, cityManager, $rootScope) {
        $log.debug('BoardingPointsListController');
        $scope.headline = "Boarding Points";
        $scope.cityId = $stateParams.id;
        $scope.currentPageOfBoardingPoints = [];
        var loadTableData = function (tableParams, $defer) {
            cityManager.getCity($scope.cityId, function(data) {
                $scope.city = data;
                var orderedData = tableParams.sorting() ? $filter('orderBy')($scope.city.boardingPoints, tableParams.orderBy()) : $scope.city.boardingPoints;
                tableParams.total($scope.city.boardingPoints.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfBoardingPoints = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.boardingPointContentTableParams = new NgTableParams({
            page: 1,
            count: 25,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfBoardingPoints.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        //--------------------------------City deletion-----------------------------------------------------------
        $scope.handleDeleteButtonClicked = function(id) {
            cityManager.deleteCity(id);
        },
        //-----------------------------------------------------------------------------------------------------------
        $scope.handleClickAddBoardingPoint = function () {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'add-boardingpoint-to-city-state-modal.html',
                controller: 'AddBoardingPointController',
                resolve: {
                    cityId:function(){
                        return $scope.cityId;
                    },
                    city :function(){
                        return $scope.city;
                    }
                }
            })
        },
        $scope. updateBpOnClick = function(id) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'update-boardingPt.html',
                controller: 'UpdateBoardingPtController',
                resolve: {
                    cityId: function () {
                        return $scope.cityId;
                    },
                    BpId:function(){
                        return id;
                    }
                }
            })
        },
        $scope. deleteBpOnClick = function(id) {
            cityManager.deleteBp($scope.cityId,id,function(data){
                console.log("in deleteBP"+data.name);
                $state.go($state.$current, null, { reload: true });
            });
        }
    })

    // ========================== Modal - Boarding point controller =================================
    .controller('AddBoardingPointController', function ($scope, $uibModal,$state, $http,$log,city, cityManager, $rootScope) {
        $scope.boardingPoint = {};
        $scope.city = city;
        $scope.ok = function () {
            if ($scope.boardingPoint.name === null || $scope.boardingPoint.contact === null || $scope.boardingPoint.landmark === null) {
                $log.error("null name or contact or landmark.  nothing was added.");
                $rootScope.modalInstance.close(null);
            }
            cityManager. createBordingPoint(city.id,$scope.boardingPoint, function(data){
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            });
        };
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.boardingPoint.name || '') !== '' &&
                ($scope.boardingPoint.landmark || '') !== '' &&
                ($scope.boardingPoint.contact || '') !== '';
        };
    })
//======================Model - updateBpController=============================================
    .controller('UpdateBoardingPtController', function ($scope, $uibModal, $state,$http,BpId,cityId, $log,cityManager, $rootScope) {
        $scope.setBpIntoView = function(cityId,BpId){
            cityManager.getBp(cityId,BpId,function(data){
                $scope.boardingPoint=data;
            })
        };
        $scope.setBpIntoView (cityId,BpId);
        $scope.ok = function (BpId) {
            cityManager.updateBp(cityId,$scope.boardingPoint, function(data) {
                $state.go($state.$current, null, { reload: true });
                $rootScope.modalInstance.close(data);
            })
        }
        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    });



