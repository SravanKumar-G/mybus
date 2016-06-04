/**
 * Created by yks-srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,$compile,b2cBusResultsManager) {
	$scope.trips=[];
	$scope.selectedSeat = [];
	$scope.busJourney = {
			dateOfJourney: new Date(),
			rdateOfJourney: new Date(),
			fromCity: "hyderabad",
			toCity: "bangalore",
			journeyType:'TWO_WAY'
		};
	$scope.selectedSeates = [];
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
			
			$scope.upper=[];
			$scope.lower=[];
			$scope.lowRows = [];
			$scope.upRows = [];
			$scope.sleeper = false;
			angular.forEach($scope.seatlayout.rows,function(row){
				angular.forEach(row.seats,function(seat){
					if(seat.sleeper){
						if(seat.upperDeck){
							$scope.upRows.push(seat);
						}else{
							$scope.lowRows.push(seat);
						}
						$scope.sleeper = seat.sleeper;
					}else{
					}
					
				})
				if($scope.upRows.length>0)
					$scope.upper.push($scope.upRows);
				if($scope.lowRows.length>0)
					$scope.lower.push($scope.lowRows);
				$scope.upRows=[];
				$scope.lowRows=[];
			});
			
			var layout = '';
			
			layout += '<div class="col-sm-1 pull-right">'+
    		'<md-button>'+
        		'<md-icon type="button" class="btn-sm" ng-click="seatLayoutHide('+serviceNumber+')">'+
           			'<span class="glyphicon glyphicon-remove"></span>'+
				'</md-icon>'+
       		'</md-button>'+
    	'</div>';
			
			if($scope.sleeper){
				layout += '<div class="col-sm-8"><span>Upper</span>'+
				'<table class="holder">'+
				 	'<tr ng-repeat="seats in upper">'+
				 	'<td ng-class="{\'sleeper\': seat.number}" ng-repeat="seat in seats"><input ng-disabled="true" type="text" ng-if="seat.number" data-ng-model="seat.number" style="width:35px" /></td>'+
				 	'</tr>'+
				'</table>'+
				'<span>Lower</span>'+
				'<table class="holder">'+
				 '   <tr ng-repeat="seats in lower">'+
				  ' <td ng-class="{\'sleeper\' : seat.seatStatus==\'AVAILABLE\',\'selectedSleeper\' : seat.seatStatus==\'BOOKING_INPROGRSS\',\'bookedSleeper\' : seat.seatStatus==\'BOOKED\'}" ng-repeat="seat in seats"><input ng-disabled="true" type="text" ng-if="seat.number" data-ng-model="seat.number" style="width:35px" /></td>'+
				   ' </tr>'+
				'</table>'+
				   '</div>';
				
			}else{
				layout += '<div class="col-sm-8"><table ng-if="!issleeper"  class="holder">'+
				 	'<tr ng-repeat="seats in seatlayout.rows">'+
					'<td class="clickable-item" ng-class="{\'seat\' : seat.seatStatus==\'AVAILABLE\',\'selectedSeat\' : seat.seatStatus==\'BOOKING_INPROGRSS\',\'bookedSeat\' : seat.seatStatus==\'BOOKED\'}" ng-repeat="seat in seats.seats" ng-click="seatSelect(seat,'+serviceNumber+')" id="selectSeat-seat.number-'+serviceNumber+'"><input ng-disabled="true" type="text" style="width:35px" ng-if="seat.number"  data-ng-model="seat.number"/></td>'+
				 '</tr>'+
			'</table></div>';
				
			}
			layout+='<div class="col-sm-4" ng-if="seatSelected">'+
					'<select class="form-control" data-ng-model="boardingPoint" required>'+
        			'<option value="" selected="selected">-- Middle Row --</option>'+
        			'<option value="" selected="selected">SR Nager</option>'+
        			'<option value="" selected="selected">Amirpet</option>'+
        			'</select>'+
        			'<div> <label>seats :</label><span ng-repeat="seatNumber in selectedSeates">{{seatNumber}} &nbsp;,</span></div>'+
        			'<input type="button" value="Continue to Payment" class="btn btn-primary" ng-click="continueToPayment()">'
					'</div>';
			
			
			angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).append($compile(layout)($scope));
		});
	}
	$scope.seatLayoutHide = function(serviceNumber){
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
	}
	$scope.showDropingPoints = function(dps,serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		angular.element(document.getElementById(dropingPoint)).append('<table><tr><td>Droping Point</td><td>Time</td></tr>'+
				'<tr><td>kphp</td><td>12:10 PM</td></tr></table>');
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
	$scope.hideBoardingPoints = function(serviceNumber){
		var boardingPoint = 'boardingPoint-'+serviceNumber
		angular.element(document.getElementById(boardingPoint)).empty();
	}
	
	$scope.seatSelect = function(seat,serviceNumber){
		if(!$scope.selectedSeates)
			$scope.selectedSeates= [];
		if(seat.seatStatus=="BOOKED"||seat.seatStatus=="UNAVAILABLE"){
		}else{
			if(seat.seatStatus=="AVAILABLE"){
				$scope.seatTotalFare=seat
				$scope.seatSelected = true;
				$scope.selectedSeates.push(seat.number);
				seat.seatStatus = 'BOOKING_INPROGRSS';
			}else{
				seat.seatStatus = 'AVAILABLE';
				var index = $scope.selectedSeates.indexOf(seat.number)
				if(index>=0){
					$scope.selectedSeates.splice(index, 1);
				}
				if($scope.selectedSeates.lenght<=0)
					$scope.seatSelected = false;
			}	
		}
	}
	$scope.continueToPayment = function(){
		$log.debug("$scope.continueToPayment = function(){");
		 $log.debug("$scope.busJourney -"+$scope.busJourney);
		    $location.url('/detailsPayment');
	}
});