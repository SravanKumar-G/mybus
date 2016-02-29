/**
 * Created by svanik on 2/22/2016.
 */

var portalApp = angular.module('myBus');

portalApp.factory('agentPlanManager', function ($rootScope, $http, $log, $window) {

    var plans = {};

    return{
        fetchAllPlans: function () {
            $log.debug("fetching plans data ...");
            $http.get('/api/v1/plans')
                .success(function (data) {
                    plans = data;
                    $rootScope.$broadcast('PlansInitComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving plans");
                });
        },

        getPlans: function (callback) {
            $log.debug("fetching routes data ...");
            $http.get('/api/v1/plans')
                .success(function (data) {
                    callback(data);
                    $rootScope.$broadcast('FetchingPlansComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving plans");
                });
        },

        getPlan: function(planId,callback){
            $http.get('/api/v1/planId/'+planId).success(function(data){
                callback(data);
            })
                .error(function (error) {
                    $log.debug("error retrieving cities");
                });
        },

        getAllPlans: function () {
            return plans;
        },

        createPlan: function(agentPlanType,callback){
            $http.post('/api/v1/plan',agentPlanType).success(function(data){
                callback(data);
                $rootScope.$broadcast('CreatePlanCompleted');
            })
                .error(function (err,status) {
                    /*var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
                     $log.error(errorMsg);
                     alert(errorMsg);*/
                    sweetAlert("Error",err.message,"error");
                });
        }


    }

});
