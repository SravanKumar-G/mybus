/**
 * Created by yks-srinu.
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
	$scope.seatLayout = function(layoutId,serviceNumber){
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
		$scope.seatlayout = '';
		b2cBusResultsManager.seatLayout(layoutId,function(data){
			$scope.seatlayout = data;
			var layout = '<div class="col-sm-11"><table ng-if="!issleeper"  class="holder">'+
				 '<tr ng-repeat="seats in seatlayout.rows">'+
			'<td ng-class="{\'seat\': seat.number}" ng-repeat="seat in seats.seats"><input ng-disabled="true" type="text" style="width:35px" ng-if="seat.number"  data-ng-model="seat.number" /></td>'+
		 '</tr>'+
	'</table></div>' +
			
			
		'<div class="col-sm-1 pull-right">'+
    		'<md-button>'+
        		'<md-icon type="button" class="btn-sm" ng-click="seatLayoutHide('+serviceNumber+')">'+
           			'<span class="glyphicon glyphicon-remove"></span>'+
				'</md-icon>'+
       		'</md-button>'+
    	'</div>';
			/*angular.forEach($scope.rows,function(row){
				layout = layout+'<tr>';
				angular.forEach(row.seats,function(){
					layout = layout+'<td><span class="seat"></span></td>';
				});
				layout = layout+'<tr>';
			});*/
			angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).append($compile(layout)($scope));
		});
	}
	$scope.seatLayoutHide = function(serviceNumber){
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
	}
	$scope.showDropingPoints = function(dps,serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		angular.element(document.getElementById(dropingPoint)).append('<table><tr><td>Droping Point</td><td>Time</td></tr>'+
				'<tr><td>kphp</td><td>12:10 PM</td></tr></table>')
		$log.debug(dropingPoints)
	}
	$scope.hideDropingPoints = function(serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		angular.element(document.getElementById(dropingPoint)).empty();
	}
	
	$scope.showBoardingPoints = function(bps,serviceNumber){
		var boardingPoint = 'boardingPoint-'+serviceNumber;
		angular.element(document.getElementById(boardingPoint)).append('<table><tr><td>Boarding Point</td><td>Time</td></tr>'+
				'<tr><td>kphp</td><td>12:10 PM</td></tr></table>');
	}
	$scope.hideBoardingPoints = function(serviceName){
		var boardingPoint = 'boardingPoint-'+serviceNumber
		angular.element(document.getElementById(boardingPoint)).empty();
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