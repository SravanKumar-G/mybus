
"use strict";
/*global angular, _*/

angular.module('myBus.agentModule', ['ngTable', 'ui.bootstrap'])
    .controller('AgentController', function($scope,$rootScope, $state, $http,$uibModal, $log, $filter, NgTableParams, $location, agentManager, userManager) {
        $scope.headline = "Agents";
        $scope.count = 0;
        $scope.agents = {};
        $scope.invalidCount = 0;
        $scope.loading =false;
        $scope.currentPageOfAgents = [];
        $scope.agentsCount = 0;
        $scope.agentTableParams = {};
        $scope.showInvalid = false;
        $scope.query = "";
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            agentManager.loadAll($scope.query,$scope.showInvalid, function(data){
                $scope.count = data.length;
                $scope.invalidCount = 0;
                if(angular.isArray(data)) {
                    $scope.loading = false;
                    _.each(data, function(agent, index){
                        if(!agent.branchOfficeId){
                            $scope.invalidCount++;
                        }
                    });
                    $scope.agents = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                    tableParams.total(data.length);
                    tableParams.data = $scope.agents;
                    $scope.currentPageOfAgents = $scope.agents.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };

        $scope.init = function() {
            agentManager.count($scope.query,$scope.showInvalid, function(agentsCount){
                $scope.agentTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:agentsCount,
                    getData: function (params) {
                        loadTableData(params);
                    }
                });
            });
        };

        $scope.init();
        $scope.searchFilter = function(){
            $scope.init();
        }

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
        $scope.isAdmin = function(){
            return userManager.getUser().admin;
        };
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
            loadAll: function (query, showInvalid,callback) {
                $http.get('/api/v1/agent/all?query='+query+"&showInvalid="+showInvalid)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving agents");
                    });
            },
            count: function (query, showInvalid, callback) {
                $http.get('/api/v1/agent/count?query='+query+"&showInvalid="+showInvalid)
                    .then(function (response) {
                        callback(response.data);
                    },function (error) {
                        $log.debug("error retrieving agents count");
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



