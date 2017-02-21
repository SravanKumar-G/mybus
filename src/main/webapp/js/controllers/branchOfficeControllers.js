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
        $scope.loadAll = function () {
            branchOfficeManager.loadAll(function(data){
                $scope.offices =data;
                $scope.count = data.length;
            });
        };
        $scope.addOffice = function () {
            $state.go('editbranchoffice');
        };
        $scope.loadAll();

        $scope.edit = function(office){
            $location.url('branchoffice/'+office.id,{'idParam':office.id});
        }
        $scope.delete = function(office){
            branchOfficeManager.deleteOffice(office.id);
        }
        $scope.$on('DeleteOfficeCompleted',function(e,value){
            $scope.loadAll();
        });

    })

    //
    // ============================= Add/Edit ========================================
    //
    .controller('EditBranchOfficeController', function($scope,$stateParams,userManager,$window,$log, cityManager, $location, cancelManager,branchOfficeManager ) {
        $scope.headline = "Edit Branch Office";
        $scope.id=$stateParams.id;
        cityManager.getCities(function(data) {
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
                $log.debug("fetching branch offices data ...");
                $http.get('/api/v1/branchOffices')
                    .success(function (data) {
                        branchOffices = data;
                        callback(data);
                        $rootScope.$broadcast('BranchOfficesLoadComplete');
                    })
                    .error(function (error) {
                        $log.debug("error retrieving branchOffices");
                    });
            },
            save: function(branchOffice, callback) {
                if(branchOffice.id) {
                    $http.put('/api/v1/branchOffice/'+branchOffice.id,branchOffice).success(function(data){
                        if(angular.isFunction(callback)){
                            callback(data);
                        }
                        $rootScope.$broadcast('BranchOfficeUpdated');
                    }).error(function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
                } else {
                    $http.post('/api/v1/branchOffice',branchOffice).success(function(data){
                        if(angular.isFunction(callback)){
                            callback(data);
                        }
                        $rootScope.$broadcast('BranchOfficeCreated');
                    }).error(function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
                }

            },
            load: function(officeId,callback) {
                $http.get('/api/v1/branchOffice/'+officeId)
                    .success(function (data) {
                        callback(data);
                        $rootScope.$broadcast('BranchOfficeLoadComplete');
                    })
                    .error(function (error) {
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
                    $http.delete('api/v1/branchOffice/'+id).success(function(data){
                        $rootScope.$broadcast('DeleteOfficeCompleted');
                        swal("Deleted!", "Office was successfully deleted!", "success");
                    }).error(function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            }
        }
    });



