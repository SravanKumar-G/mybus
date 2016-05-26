/**
 * Created by yks_srinu.
 */

var portalApp = angular.module('myBusB2c');

portalApp.factory('b2cHomeManager', function ($rootScope, $http, $log, $window) {
	
	return {
		getAllcites:function(callback){
			$http.get("/api/v1/cities")
			.success(function(data){
				callback(data);
			}).
			error(function(error){
				
			});
		}
	}
	
});
