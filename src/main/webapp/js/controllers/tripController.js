"use strict";
/*global angular, _*/

angular.module('myBus.tripModule', ['ngTable', 'ui.bootstrap'])
.controller("TripController",function($rootScope,$scope, tripManager){
	
	$scope.trips = [];
	
	$scope.trip = {};
	
	$scope.getAllTrips = tripManager.getAllTrips(function(data){
		$scope.trips =data;
	});

	$scope.addTrip = tripManager.addTrip($scope.trip,function(data){
	});
	
	$scope.getTripById = tripManager.getTripByID(id,function(data){
		$scope.trip = data;
	})
	
	$scope.resetTrip = function(){
		$scope.trip= {};
	};
	
});