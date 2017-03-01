/**
 * Created by srinikandula on 2/18/17.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.branchOfficeModule', ['ngTable', 'ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('BranchOfficesController', function($scope,$state, $http, $log, $filter, NgTableParams, $location, branchOfficeManager) {
        $scope.headline = "Branch Offices";
        $scope.count = 0;
        $scope.offices = {};
        $scope.addOffice = function () {
            $state.go('editbranchoffice');
        };

        $scope.edit = function(office){
            $location.url('branchoffice/'+office.id,{'idParam':office.id});
        }
        $scope.delete = function(office){
            branchOfficeManager.deleteOffice(office.id);
        }
        $scope.$on('DeleteOfficeCompleted',function(e,value){
            $scope.loadAll();
        });

        $scope.currentPageOfOffices = [];
        var loadTableData = function (tableParams) {
            branchOfficeManager.loadAll(function(data){
                if(angular.isArray(data)) {
                    $scope.offices =data;
                    $scope.count = data.length;
                    $scope.offices = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    tableParams.total(data.length);
                    $scope.currentPageOfOffices = $scope.offices.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };
        $scope.officeTableParams = new NgTableParams({
            page: 1,
            count: 100,
            sorting: {
                fullName: 'asc'
            }
        }, {
            total: $scope.currentPageOfOffices.length,
            getData: function (params) {
                loadTableData(params);
            }
        });


    })

    //
    // ============================= Add/Edit ========================================
    //
    .controller('EditBranchOfficeController', function($scope,$stateParams,userManager,$window,$log, cityManager, $location, cancelManager,branchOfficeManager ) {
        $scope.headline = "Edit Branch Office";
        $scope.id=$stateParams.id;
        cityManager.getActiveCityNames(function(data) {
            $scope.cities = data;
            userManager.getUserNames(function(users) {
                $scope.users= users;
            });
        });
        $scope.office ={};
        $scope.cityName = null;
        $scope.managerName = null;

        $scope.selectFromCity = function(item){
            $scope.cityName = item.name;
            $scope.office.cityId= item.id;
        };

        $scope.selectManager = function(item, model, label, event){
            $scope.managerName = item.firstName;
            $scope.office.managerId= item.id;
        };
        $scope.launchUserAdd = function() {
            $location.url('/user/');
        }
        $scope.save = function() {
            if($scope.thisForm.$dirty){
                $scope.thisForm.submitted = true;
                if($scope.thisForm.$invalid) {
                    swal("Error!","Please fix the errors in the user form","error");
                    return;
                }
                branchOfficeManager.save($scope.office, function(data){
                    if($scope.office.id){
                        swal("success","BranchOffice Updated","success");
                    } else {
                        swal("success","BranchOffice created","success");
                    }
                });
            }
            $location.url('/branchoffices');
        };
        $scope.cancel = function(theForm) {
            cancelManager.cancel(theForm);
        }
        if($scope.id) {
            branchOfficeManager.load($scope.id, function(data) {
                $scope.office = data;
                $scope.cityName = data.attributes.cityName
                $scope.managerName = data.attributes.managerName;
            });
        }

    }).factory('branchOfficeManager', function ($http, $log,$rootScope) {
        var branchOffices = {};
        return {
            loadAll: function (callback) {
                $http.get('/api/v1/branchOffices')
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('BranchOfficesLoadComplete');
                    },function (error) {
                        $log.debug("error retrieving branchOffices");
                    });
            },
            loadNames: function (callback) {
                $http.get('/api/v1/branchOffice/names')
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('BranchOfficesLoadComplete');
                    },function (error) {
                        $log.debug("error retrieving branchOffices");
                    });
            },
            save: function(branchOffice, callback) {
                if(branchOffice.id) {
                    $http.put('/api/v1/branchOffice/'+branchOffice.id,branchOffice).then(function(response){
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('BranchOfficeUpdated');
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
                } else {
                    $http.post('/api/v1/branchOffice',branchOffice).then(function(response){
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('BranchOfficeCreated');
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
                }

            },
            load: function(officeId,callback) {
                $http.get('/api/v1/branchOffice/'+officeId)
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('BranchOfficeLoadComplete');
                    },function (error) {
                        $log.debug("error retrieving branchOffice");
                    });
            },
            deleteOffice: function(id) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this office?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){
                    $http.delete('api/v1/branchOffice/'+id).then(function(response){
                        $rootScope.$broadcast('DeleteOfficeCompleted');
                        swal("Deleted!", "Office was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            }
        }
    });



