
"use strict";
/*global angular, _*/

angular.module('myBus.agentModule', ['ngTable', 'ui.bootstrap'])
    .controller('AgentController', function($scope,$state, $http, $log, $filter, NgTableParams, $location, agentManager) {
        $scope.headline = "Agents";
        $scope.count = 0;
        $scope.agents = {};
        $scope.currentPageOfAgents = [];
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
    .controller('EditAgentController', function($scope,$stateParams,userManager,$window,$log, cityManager, $location, cancelManager,branchOfficeManager ) {
        $scope.headline = "Edit Agent";


    }).factory('agentManager', function ($http, $log,$rootScope) {
        var agents = {};
        return {
            loadAll: function (callback) {
                $http.get('/api/v1/agent/all')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving agents");
                    });
            },
            download: function (callback) {
                $http.get('/api/v1/agent/downloadAll')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error downloading agents");
                        sweetAlert("Error",error.data.message,"error");
                    });
            },
            save: function(agent, callback) {
                if(agent.id) {
                    $http.put('/api/v1/agent/'+agent.id,agent).then(function(response){
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('AgentUpdated');
                    },function (err,status) {
                        sweetAlert("Error",err.data.message,"error");
                    });
                } else {
                    $http.post('/api/v1/branchOffice',branchOffice).then(function(response){
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('BranchOfficeCreated');
                    },function (err,status) {
                        sweetAlert("Error",err.data.message,"error");
                    });
                }

            },
            load: function(agentId,callback) {
                $http.get('/api/v1/agent/'+agentId)
                    .then(function (response) {
                        callback(response.data);
                        $rootScope.$broadcast('AgentLoadComplete');
                    },function (error) {
                        $log.debug("error retrieving agent info");
                        sweetAlert("Error",err.data.message,"error");
                    });
            }
        }
    });



