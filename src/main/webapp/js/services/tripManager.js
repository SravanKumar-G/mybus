
var portalApp = angular.module('myBus');
portalApp.factory('tripManager',function($rootScope,$http,$window,$log){
	
	return {
		getAllTrips : function(callback){
			$http.get("").success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		},
		addTrip: function(trip,callback){
			
			$http.post("",trip).success(function(data){
				callback(data);
			}).
			error(function(error){
				
			})
		},
		getTripByID : function(id,callback){
			$http.get("",id).success(function(data){
				calback(data)
			}).
			error(function(error){
				
			})
		}
	}
});