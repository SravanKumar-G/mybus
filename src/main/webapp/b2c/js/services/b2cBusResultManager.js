/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');

portalApp.factory('b2cBusResultsManager', function ($rootScope, $http, $log, $window) {
	
	return {
		getAvailableTrips:function(callback){
			$http.get("/api/v1/availabletrip")
			.success(function(data){
				callback(data);
			}).
			error(function(error){
				
			});
		},
		seatLayout:function(layoutID,callback){
			$http.get("/api/v1/busLayout/"+layoutID)
			.success(function(data){
				$log.debug(data);
				callback(data);
			}).
			error(function(error){
				
			});
		}
	}
	
});