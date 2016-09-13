"use strict";
/*global angular, _*/

angular.module('myBus.tripModule', ['ngTable', 'ui.bootstrap'])
.controller("TripController",function($rootScope,$scope, $log, $filter,ngTableParams,tripManager,cityManager){

	var tripCtrl = this;
	tripCtrl.trips = [];
	
	tripCtrl.trip = {};
	
	tripCtrl.currentPageTrips = [];
	
	tripCtrl.searchFields = {
			fromCity : '',
			toCity : '',
			tripDate : ''
	};
	
	tripCtrl.getAllTrips = tripManager.getAllTrips(function(data){
		tripCtrl.trips =data;
	});

	tripCtrl.addTrip = tripManager.addTrip($scope.trip,function(data){
	});
	
	tripCtrl.getTripById = tripManager.getTripByID($scope.id,function(data){
		tripCtrl.trip = data;
	});
	
	tripCtrl.searchTrips = function(){
		 var searchFields = angular.copy(tripCtrl.searchFields);
		 searchFields.tripDate = $filter('date')(searchFields.tripDate,'yyyy-MM-dd');
		 tripCtrl.trips = tripManager.searchBuses(searchFields,function(data){
			 tripCtrl.trips = data;
		 })
	};
	
	$scope.loadFromCities = cityManager.getCities(function(data){
		tripCtrl.cities = data;
	})
	
	tripCtrl.resetTrip = function(){
		tripCtrl.trip= {};
	};
	
	
	tripCtrl.tripsTableParams = new ngTableParams({
         page: 1,
         count: 25,
         sorting: {
             tripDate: 'asc'
         }
     }, {
         total: tripCtrl.currentPageTrips.length,
         getData: function ($defer, params) {
//             $scope.$on('servicesInitComplete', function (e, value) {
//                 loadTableData(params);
//           });
         }
     });
	
	return tripCtrl;
});