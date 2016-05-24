/**
 * Created by svanik on 1/20/2016.
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
