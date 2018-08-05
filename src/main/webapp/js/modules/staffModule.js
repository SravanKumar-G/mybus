'use strict';
/*global angular,_*/

angular.module('myBus.staffModule', ['ngTable', 'ui.bootstrap'])
    .controller('StaffListController', function ($scope,$rootScope, $state,$http, $log,paginationService, $location, staffManager, NgTableParams) {

        var pageable ;
        $scope.loading = true;


        $scope.deleteStaff = function(id) {

        };
        $scope.addVehicleStaff = function(id) {
            $location.url('editstaff/'+id);
        }

        var loadStaffList = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            staffManager.getStaffList(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.staff = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.staff;
                }
            });
        };
        $scope.init = function() {
            staffManager.count(null, function(staffCount){
                $scope.staffTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        name: 'desc'
                    },
                }, {
                    counts:[10,50,100],
                    total:staffCount,
                    getData: function (params) {
                        loadStaffList(params);
                    }
                });
            });
        };
        $scope.init();
    })
    .controller('EditStaffController', function ($scope,$state,$stateParams, $rootScope, $http,$log, $location, staffManager) {
        $scope.headline = "Staff";
        $scope.staffId = $stateParams.id;
        $scope.staff = {};
        console.log('staffId ' + $scope.staffId );
        if($scope.staffId) {
            staffManager.getStaff($scope.staffId, function(response){
                $scope.staff = response;
            }, function(error){
                sweetAlert("Error finding staff",err.data.message,"error");
            });
        }
        $scope.save = function(){
            staffManager.createStaff($scope.staff,function(data){
                $location.url('staff');
                swal("Great", "Staff is created", "success");
            }, function (error) {
                sweetAlert("Error Saving Vehicle info",error.data.message,"error");
            });
        }
        $scope.cancel = function(){
            $location.url('staff');
        }
    }).factory('staffManager', function ($rootScope, $http) {
        return {
            createStaff: function (staff, callback, errorCallback) {
                $http.post('/api/v1/staff/create', staff).then(function (response) {
                    callback(response.data);
                }, function (err) {
                    errorCallback(err);
                });
            },
            getStaff: function (staffId, successCallback, errorCallback) {
                $http.get('/api/v1/staff/'+staffId).then(function (response) {
                    successCallback(response.data);
                }, function (err) {
                    errorCallback(err)
                });
            },
            getStaffList: function (filter, successCallback, errorCallback) {
                $http.get('/api/v1/staff?filter='+filter).then(function (response) {
                    successCallback(response.data);
                }, function (err) {
                    errorCallback(err)
                });
            },
            count:function (filter,successCallback,errorCallback ) {
                $http.get('/api/v1/staff/count?filter='+ filter).then(function (response) {
                    successCallback(response.data);
                }, function (err) {
                    errorCallback(err)
                });
            }
        }
    });

