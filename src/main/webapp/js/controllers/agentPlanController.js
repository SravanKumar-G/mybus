/**
 * Created by svanik on 2/22/2016.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.agentPlanModule', ['ngTable', 'ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('AgentPlanController', function($scope, $http, $log, $modal, $filter, NgTableParams, $location, usSpinnerService, agentPlanManager, $rootScope) {
        console.log("in AgentPlanController");
        $scope.headline = "Agent Plan Type";
        $scope.currentPageOfPlans = [];

        var loadTableData = function (tableParams, $defer) {
            var data = agentPlanManager.getAllPlans();
            var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
            tableParams.total(data.length);
            if (angular.isDefined($defer)) {
                $defer.resolve(orderedData);
            }
            $scope.currentPageOfPlans = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
        };

        $scope.$on('PlansInitComplete', function (e, value) {
            loadTableData($scope.planContentTableParams);
        });

        $scope.$on('CreatePlanCompleted', function (e, value) {
            agentPlanManager.fetchAllPlans();
        });

        $scope.$on('updatePlanCompleteEvent', function (e, value) {
            agentPlanManager.fetchAllPlans();
        });

        $scope.$on('DeletePlanCompleted',function(e,value) {
            agentPlanManager.fetchAllPlans();
        });

        $scope.planContentTableParams = new NgTableParams({
            page: 1,
            count: 50,
            sorting: {
                name: 'asc',
                type: 'asc',
                settlementFrequency: 'asc'
            }
        }, {
            total: $scope.currentPageOfPlans.length,
            getData: function ($defer, params) {
                $scope.$on('PlansInitComplete', function (e, value) {
                    loadTableData(params);
                });
            }
        });

        $scope.goToAddNewPlan = function(){
            $location.url('/plan');
        };
        $scope.handleClickUpdatePlan = function(planId){
            var modalInstance = $modal.open({
                templateUrl : 'update-plan-modal.html',
                controller : 'UpdatePlanModalController',
                resolve : {
                    passId : function(){
                        return planId;
                    }
                }
            });
        };
        $scope.handleClickDeletePlan = function(passId){
            agentPlanManager.deletePlan(passId);
        };

        agentPlanManager.fetchAllPlans();

    })

    .controller('AddAgentPlanTypeController', function ($scope, $location, $http, $log, agentPlanManager,cancelManager) {

        $scope.headline = "Add New Plan";
        console.log("in AddAgentPlanType Controller");
        $scope.agentPlanType={};
        $scope.ok = function () {
            agentPlanManager.createPlan($scope.agentPlanType,function(data){
                console.log("Plan Saved");
                $location.url('/plans');
            })
        };

        $scope.cancelPlanType = function (theForm) {
            cancelManager.cancel(theForm);
        };

        $scope.isInputValid = function () {

        };

    })

    .controller('UpdatePlanModalController', function ($document, $scope, $location, $modalInstance, $http, $log, agentPlanManager, passId) {
        console.log("in UpdatePlanController");
        $scope.plan = {};

        $scope.displayPlan = function(data){
            $scope.plan = data;
        };

        $scope.setPlan = function(passId){
            agentPlanManager.getPlan(passId,$scope.displayPlan);

        };
        $scope.setPlan(passId);

        $scope.ok = function () {

            if ($scope.plan.id === null || $scope.plan.name === null || $scope.plan.deposit === null) {
                $log.error("null plan.  nothing was added.");
                $modalInstance.close(null);
            }
            agentPlanManager.updatePlan($scope.plan,function(data){
                console.log("Plan Updated");
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

        };

    })

