"use strict";
/*global angular, _*/

angular.module('myBus.fullTripModule', ['ngTable', 'ui.bootstrap'])
    .controller("fullTripListController", function ($rootScope, $scope, $uibModal, NgTableParams, $location, paginationService, fullTripManager, $state, $stateParams) {

        $scope.init = function () {
            fullTripManager.count(function (count) {
                $scope.fullTripsTableParams = new NgTableParams({
                    page: 1, // show first page
                    count: 100,
                    sorting: {
                        createdAt: 'desc'
                    },
                }, {
                    counts: [100, 200, 500],
                    total: count,
                    getData: function (params) {
                        loadCargoBookings(params);
                    }
                });
            });
        };
        var loadCargoBookings = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = "";
            for (var prop in sortingProps) {
                sortProps += prop + "," + sortingProps[prop];
            }
            $scope.loading = true;
            var filter = {
                page: tableParams.page(),
                size: tableParams.count(),
                sort: sortProps
            };

            fullTripManager.getAllTripDetails(filter, function (fullTrips) {
                if (angular.isArray(fullTrips)) {
                    $scope.loading = false;
                    tableParams.data = fullTrips;
                    $scope.tableParams = tableParams;
                    $scope.fullTrips = fullTrips;
                }
            });
        };

        $scope.init();

        $scope.updateFullTrip = function (fullTripId) {
            $state.go('editFullTrip', {id: fullTripId});
        };

    })
    .controller("addOrEditFullTripController", function ($rootScope, $scope, $uibModal, NgTableParams, $location, paginationService, fullTripManager, $state, $stateParams) {

        $scope.fullTrip = {canceled: false, due: false};

        $scope.title = "Add Full Trip Details";

        if ($stateParams.id) {
            $scope.title = "Update Full Trip Details";
            fullTripManager.getTripDetails($stateParams.id, function (data) {
                $scope.fullTrip = data;
            })
        }

        $scope.fullTripDetails = function () {
            if (!$scope.fullTrip.tripDate) {
                swal("Error", "Please select the Trip Date", "error");
                return;
            }
            if (!$scope.fullTrip.from) {
                swal("Error", "Please enter the From", "error");
                return;
            }
            if (!$scope.fullTrip.to) {
                swal("Error", "Please enter the To", "error");
                return;
            }
            if (!$scope.fullTrip.contact || $scope.fullTrip.contact.toString().length < 10) {
                swal("Error", "Invalid contact number", "error");
                return;
            }
            if ($stateParams.id) {
                console.log($stateParams.fullTripid);
                fullTripManager.updateFullTripDetails($scope.fullTrip, function (response) {
                    console.log("response",response);
                    swal("success","Full Trip Details Updated Successfully", "success");
                    $state.go('fulltrips');
                })
            } else {
                fullTripManager.saveFullTripDetails($scope.fullTrip, function (response) {
                    swal("success","Full Trip Details Added Successfully", "success");
                    $state.go('fulltrips');
                })
            }
        };


    })
    .factory('fullTripManager', function ($rootScope, $q, $uibModal, $http, $log, $location) {
        return {
            saveFullTripDetails: function (details, successcallback) {
                $http.post("/api/v1/fullTrip", details)
                    .then(function (response) {
                        successcallback(response.data)
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    })
            },
            getAllTripDetails: function (filter, callback) {
                $http.post('/api/v1/fullTrips', filter)
                    .then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data.content);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching i Full Trip", err.message, "error");
                    });
            },
            getTripDetails: function (id, callback) {
                $http.get('/api/v1/fullTrip/'+id)
                    .then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                        }
                    }, function (err, status) {
                        sweetAlert("Error searching i Full Trip", err.message, "error");
                    });
            },
            count: function (callback) {
                $http.get('/api/v1/fullTrips/count')
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    });
            },
            updateFullTripDetails: function (details, successcallback) {
                $http.put("/api/v1/fullTrip", details)
                    .then(function (response) {
                        successcallback(response.data)
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    })
            },
            deleteFullTripRecord: function (id, callback) {
                $http.delete('/api/v1/fullTrip/' + id)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        swal("oops", error.data.message, "error");
                    });
            },
        }

    });