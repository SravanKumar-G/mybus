/**
 * Created by srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,$compile,b2cBusResultsManager) {
	$scope.trips=[];
	$scope.busJourney = {
			dateOfJourney: new Date(),
			rdateOfJourney: new Date(),
			fromCity: "hyderabad",
			toCity: "bangalore",
			journeyType:'TWO_WAY'
		};
	$scope.modifySearch = false;
	$scope.modifySearchEvent=function(){
			$scope.modifySearch = $scope.modifySearch?false:true;
	}
	$scope.getAvailableTrips = function(){
		b2cBusResultsManager.getAvailableTrips(function(data){
			$scope.trips = data;
		})
	}
	$scope.getAvailableTrips();
	$scope.seatLayout = function(layoutId){
		$scope.seatlayout = '';
		b2cBusResultsManager.seatLayout(layoutId,function(data){
			$scope.seatlayout = data;
		});
	}
	/*		angular.forEach($scope.rows,function(row){
				Layout = Layout+'<tr>';
				angular.forEach(row.seats,function(){
					Layout = Layout+'<td><span class="seat"></span></td>';
				});
				Layout = Layout+'<tr>';
			});
			angular.element(document.getElementById('seatLayout-'+serviceId)).append($compile(Layout)($scope));
	}*/
	
	
});