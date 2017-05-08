"use strict";
/*global angular, _*/

angular.module('myBus.returnTicketsModule', ['ngTable', 'ui.bootstrap'])
    .controller("returnTicketsController",function($rootScope, $scope, $filter,$stateParams,returnTicketsManager, $location, $log,$uibModal, NgTableParams){
        $scope.headline = "Return Tickets Details";
        $scope.currentPageOfTickets = [];
        $scope.loading = false;
        $scope.returnTickets = {};
        var loadTableDataReturnTicketsByDate = function (tableParams) {
            $scope.loading = true;
            returnTicketsManager.returnTickets(function (data) {
                $scope.loading = false;
                $scope.returnTickets = data;
                $scope.currentPageOfReturnTickets = data.allDuesMappedByDate;
            });
        };
        var loadTableDataReturnTicketsByAgent = function (tableParams) {
            $scope.downloaded = true;
            returnTicketsManager.returnTickets(function (data) {
                $scope.downloaded = false;
                $scope.currentPageOfAgentReturnTickets = data.allDuesMappedByAgent;
            });
        };
        var loadTableDataAllReturnTickets = function (tableParams) {
            $scope.returned = true;
            returnTicketsManager.returnTickets(function (data) {
                $scope.returned = false;
                $scope.returnTickets = data.allDues;
                if(angular.isArray($scope.returnTickets)) {
                    $scope.allReturnTickets = tableParams.sorting() ? $filter('orderBy')($scope.returnTickets, tableParams.orderBy()) : $scope.returnTickets;
                    tableParams.total($scope.allReturnTickets.length);
                    $scope.currentPageOfAllReturnTickets = $scope.allReturnTickets.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
                }
            });
        };

        $scope.returnTicketsByDateTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableDataReturnTicketsByDate(params);
            }
        });
        $scope.returnTicketsByAgentTableParams = new NgTableParams({
            page: 1,
            count:99999,
            sorting: {
                Id: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableDataReturnTicketsByAgent(params);
            }
        });
        $scope.allReturnTicketsTableParams = new NgTableParams({
            page: 1,
            count:999999999,
            sorting: {
                Id: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableDataAllReturnTickets(params);
            }
        });

        $scope.showReturnTicketsByDate = function(date) {
            $location.url('returnTicketsByDate/'+date);
        };
        $scope.showReturnTicketsByAgent = function(agent) {
            $location.url('returnTicketsByAgent/'+agent);
        }

    })
    .controller('returnTicketsByDateController', function($scope, $rootScope, $stateParams,returnTicketsManager, NgTableParams) {
        $scope.headline = "Return Tickets by Date";
        $scope.currentPageOfTickets = [];
        var date = $stateParams.date;
        $scope.loading = false;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            returnTicketsManager.getDateData(function (data) {
                $scope.loading = false;
                $scope.currentPageOfTickets = data[date];
            })
        };

        $scope.byDateTableParams = new NgTableParams({
            page: 1,
            count:9999999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
    })

    .controller('returnTicketsByAgentController', function($scope,returnTicketsManager, $rootScope, $stateParams,NgTableParams) {
        $scope.headline = "Return Tickets by Agent";
        $scope.loading = false;
        $scope.currentPageOfTickets = [];
        var agentName = $stateParams.agent;
        var loadTableData = function (tableParams) {
            $scope.loading = true;
            returnTicketsManager.getAgentsData(function (data) {
                $scope.loading = false;
                $scope.currentPageOfTickets = data[agentName];
            })
        };
        $scope.byAgentTableParams = new NgTableParams({
            page: 1,
            count:9999999,
            sorting: {
                name: 'asc'
            }
        }, {
            total: $scope.currentPageOfTickets.length,
            getData: function (params) {
                loadTableData(params);
            }
        });
    })

    .factory('returnTicketsManager', function ($rootScope, $http, $log) {
        var returnTickets ;
        var returnTicketsByDate ;
        var returnTicketsByAgent ;
    return {
        returnTickets: function (callback) {
            $http.get('/api/v1/dueReport/returnTickets')
                .then(function (response) {
                    callback(response.data);
                     returnTickets = response.data;
                     returnTicketsByDate = returnTickets.allDuesMappedByDate;
                     returnTicketsByAgent = returnTickets.allDuesMappedByAgent;
                }, function (error) {
                    $log.debug("error retrieving the details");
                });
        },
        getDateData : function(callback){
            callback(returnTicketsByDate);
        },

        getAgentsData: function (callback) {
            callback(returnTicketsByAgent);
        }
    }
});