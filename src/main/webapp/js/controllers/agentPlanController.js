/**
 * Created by svanik on 2/22/2016.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.agentPlanModule', ['ngTable', 'ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('AgentPlanController', function($scope, $http, $log, $filter, ngTableParams, $location, usSpinnerService,agentPlanManager,$rootScope) {
        console.log("in AgentPlanController");
        $scope.headline = "Agent Plan Type";

        $scope.currentPageOfUsers = [];
        var loadTableData = function (tableParams, $defer) {
            agentPlanManager.getPlans(function (data) {
                $scope.users = data;
                var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
                //$scope.allUsers = orderedData;
                tableParams.total(data.length);
                if (angular.isDefined($defer)) {
                    $defer.resolve(orderedData);
                }
                $scope.currentPageOfUsers = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
            });
        };
        $scope.planContentTableParams = new ngTableParams({
            page: 1,
            count: 50,
            sorting: {
                state: 'asc',
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfUsers.length,
            getData: function ($defer, params) {
                $scope.$on('UsersInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        $scope.goToAddNewPlan = function(){
            $location.url('/plan');
        }
    })

    .controller('AddAgentPlanTypeController', function ($scope, $route, $http, $log, agentPlanManager) {

        $scope.headline = "Add New Plan";
        console.log("in AddAgentPlanType Controller");
        $scope.agentPlanType={};
        $scope.ok = function () {
            agentPlanManager.createPlan($scope.agentPlanType,function(data){
                console.log("Plan Saved");
            })
        };

        $scope.cancel = function () {

        };

        $scope.isInputValid = function () {

        };

    });

