/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');

portalApp.factory('b2cHomeManager', function ($rootScope, $http, $log, $window) {
	var busJourney = {};
	return {
		getAllcites:function(callback){
			$http.get("/api/v1/stations")
			.success(function(data){
				callback(data);
			}).
			error(function(error){
				
			});
		},
		getBusJourney:function(){
			return busJourney;
		},
		getSearchForBus:function(busJourney,callback){
			$http.get("/api/v1/searchForBus", {params : busJourney})
			.success(function(data){
				searchID = data;
				callback(data);
			}).
			error(function(error){
				
			});
		},
		getAvailableTrips:function(busJourney,callback){
			$http.get("/api/v1/availabletrip", {params : busJourney})
			.success(function(data){
				callback(data);
			}).
			error(function(error){
				
			});
		}
	}
	
});
