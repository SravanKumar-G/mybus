/**
 * Created by yks-srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,$compile,b2cBusResultsManager) {
	$scope.trips=[];
	$scope.selectedSeat = [];
	$scope.busJourney = {} 
	$scope.selectedSeates = [];
	$scope.modifySearch = false;
	$scope.getBusJourney=function(){
		b2cBusResultsManager.getbusJourney(function(data){
			$scope.busJourney = data.busJournies[0];
			$scope.getAvailableTrips($scope.busJourney.journeyType);
			if(data.busJournies.length==2)
				$scope.busJourney['returnJourney']=data.busJournies[0].returnJourney
		});
	}
	$scope.getBusJourney();
	$log.debug($scope.busJourney)
	$scope.modifySearchEvent=function(){
			$scope.modifySearch = $scope.modifySearch?false:true;
	}
	$scope.getAvailableTrips = function(journeyType){
		b2cBusResultsManager.getAvailableTrips(journeyType,function(data){
			$scope.trips = data;
		})
	}
	
	$scope.seatLayout = function(trip){
		$scope.CurrentTrips=trip;
		var layoutId = trip.layoutId,serviceNumber = trip.serviceNumber;
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
		$scope.seatlayout = '';
		$scope.boardingPoints=[{
			refId:"1423423",
			bpName:"SR Nager",
			time:"10:10"
		},
		{
			refId:"1423423",
			bpName:"Amirpet",
			time:"10:10"
		}
		]
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
					'<td class="clickable-item" ng-class="{\'seat\' : seat.seatStatus==\'AVAILABLE\',\'selectedSeat\' : seat.seatStatus==\'BOOKING_INPROGRSS\',\'bookedSeat\' : seat.seatStatus==\'BOOKED\'}" ng-repeat="seat in seats.seats" ng-click="seatSelect(seat,CurrentTrips)" id="selectSeat-seat.number-'+serviceNumber+'"><input ng-disabled="true" type="text" style="width:35px" ng-if="seat.number"  data-ng-model="seat.number"/></td>'+
				 '</tr>'+
			'</table></div>';
				
			}
			layout+='<div class="col-sm-4" ng-if="seatSelected">'+
					'<select class="form-control" data-ng-model="busJourney.boardingPoints" required>'+
        			'<option value="" selected="selected">-- Middle Row --</option>'+
        			'<option ng-repeat="bp in boardingPoints" value="{{bp}}">{{bp.bpName}}</option>'+
        			'</select>'+
        			'<div> <label>seats :</label><span ng-repeat="seatNumber in busJourney.seatsNumber">{{seatNumber}} &nbsp;,</span></div>'+
        			'<div> <label>Fare :</label><span>{{busJourney.fare}} &nbsp;,</span></div>'+
        			'<div> <label>Total Fare :</label><span>{{busJourney.totalFare}} &nbsp;,</span></div>'+
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
		$scope.dpsViews = dps;
		var temp = '<table class="bpDptop dp-pull"><tr><td>Droping Point</td><td>Time</td></tr>'+
		'<tr ng-repeat="dpsV in dpsViews"><td>{{dpsV.droppingName}}</td><td>{{dpsV.droppingTime}}</td></tr></table>'
		angular.element(document.getElementById(dropingPoint)).append($compile(temp)($scope));
	}
	$scope.hideDropingPoints = function(serviceNumber){
		var dropingPoint = 'dropingPoint-'+serviceNumber
		angular.element(document.getElementById(dropingPoint)).empty();
	}
	
	$scope.showBoardingPoints = function(bps,serviceNumber){
		$scope.bpsViews = bps;
		var boardingPoint = 'boardingPoint-'+serviceNumber;
		var temp ='<table class="bpDptop bp-pull"><tr><td>Boarding Point</td><td>Time</td></tr>'+
		'<tr  ng-repeat="bpv in bpsViews"  in bpsViews"><td>{{bpv.droppingName}}</td><td>{{bpv.droppingTime}}</td></tr></table>'
		angular.element(document.getElementById(boardingPoint)).append($compile(temp)($scope));
	}
	$scope.hideBoardingPoints = function(serviceNumber){
		var boardingPoint = 'boardingPoint-'+serviceNumber
		angular.element(document.getElementById(boardingPoint)).empty();
	}
	
	$scope.seatSelect = function(seat,trip){
		var serviceNumber = trip.serviceNumber;
		$scope.busJourney.fare = trip.serviceFares[0].fare;
		$scope.busJourney.serviceName = trip.serviceName;
		$scope.busJourney.serviceNumber = trip.serviceNumber;
		if(!$scope.busJourney.seatsNumber)
			$scope.busJourney.seatsNumber= [];
		if(seat.seatStatus=="BOOKED"||seat.seatStatus=="UNAVAILABLE"){
		}else{
			if(seat.seatStatus=="AVAILABLE"){
				$scope.seatTotalFare=seat
				$scope.seatSelected = true;
				$scope.busJourney.seatsNumber.push(seat.number);
				$scope.busJourney.totalFare+=$scope.busJourney.fare;
				seat.seatStatus = 'BOOKING_INPROGRSS';
			}else{
				seat.seatStatus = 'AVAILABLE';
				var index = $scope.busJourney.seatsNumber.indexOf(seat.number)
				if(index>=0){
					$scope.busJourney.seatsNumber.splice(index, 1);
					$scope.busJourney.totalFare-=$scope.busJourney.fare;
				}
				if($scope.selectedSeates.lenght<=0)
					$scope.seatSelected = false;
			}	
		}
	}
	$scope.continueToPayment = function(){
		$log.debug($scope.busJourney)
		$scope.busJourney.boardingPoints = JSON.parse($scope.busJourney.boardingPoints)
		b2cBusResultsManager.blockSeat($scope.busJourney,function(data){
			$location.url('/detailsPayment');
		})
	}
});