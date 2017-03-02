
"use strict";
/*global angular, _*/

angular.module('myBus.agentModule', ['ngTable', 'ui.bootstrap'])
    .controller('AgentController', function($scope,$rootScope, $state, $http,$uibModal, $log, $filter, NgTableParams, $location, agentManager) {
        $scope.headline = "Agents";
        $scope.count = 0;
        $scope.agents = {};
        $scope.loading =false;
        $scope.currentPageOfAgents = [];
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            agentManager.loadAll(function(data){
                if(angular.isArray(data)) {
                    $scope.loading = false;
                    $scope.count = data.length;
                    $scope.agents = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    tableParams.total(data.length);
                    $scope.currentPageOfAgents = $scope.agents.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                    $scope.count = $scope.currentPageOfAgents.length;
                }
            });
        };
        $scope.agentTableParams = new NgTableParams({
            page: 1,
            count: 5000,
            sorting: {
                fullName: 'asc'
            }
        }, {
            total: $scope.currentPageOfAgents.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
        $scope.refreshAgents = function() {
            $scope.loading = true;
            agentManager.download(function(data) {
                $scope.loading = false;
                loadTableData($scope.agentTableParams);
            })
        };

        $scope.editAgent = function(agentId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl: 'edit-agent-modal.html',
                controller: 'EditAgentController',
                resolve: {
                    agentId: function () {
                        return agentId;
                    }
                }
            });
        }
        $scope.$on('AgentUpdated', function (e, value) {
            loadTableData($scope.agentTableParams);
        });
    })
    .controller('EditAgentController', function($scope,$rootScope, $location, $stateParams,agentId,agentManager, branchOfficeManager ) {
        $scope.headline = "Edit Agent";
        $scope.agent = {};
        $scope.offices = [];
        agentManager.load(agentId, function(data){
            $scope.agent = data;
            branchOfficeManager.loadNames(function(data) {
                $scope.offices = data;
            });
        });
        $scope.saveAgent = function(){
            agentManager.save($scope.agent,function(data){
                $scope.agent = data;
                $rootScope.modalInstance.close(data);
                swal("Great", "Agent has been updated successfully", "success");
            });
        };

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
        $scope.launchAddBranchOffice = function() {
            $scope.cancel();
            $location.url('/branchoffice/');
        }


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
                $http.get('/api/v1/agent/download')
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error downloading agents");
                        sweetAlert("Error",error.data.message,"error");
                    });
            },
            save: function(agent, callback) {
                if(agent.id) {
                    $http.put('/api/v1/agent/',agent).then(function(response){
                        if(angular.isFunction(callback)){
                            callback(response.data);
                        }
                        $rootScope.$broadcast('AgentUpdated');
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
                        sweetAlert("Error",error.data.message,"error");
                    });
            }
        }
    });



