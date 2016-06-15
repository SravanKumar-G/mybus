/**
 * Created by yks-srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,$compile,b2cBusResultsManager) {
	$scope.trips=[];
	$scope.selectedSeat = [];
	$scope.bJny 
	$scope.selectedSeates = [];
	$scope.modifySearch = false;
	$scope.getBusJourney=function(){
		b2cBusResultsManager.getbusJourney(function(data){
			$scope.bJny = data.busJournies[0];
			$scope.getAvailableTrips($scope.bJny.journeyType);
			if(data.bJny.length==2)
				$scope.bJny['returnJourney']=data.bJny[0].returnJourney
		});
	}
	$scope.getBusJourney();
	$log.debug($scope.bJny)
	$scope.modifySearchEvent=function(){
			$scope.modifySearch = $scope.modifySearch?false:true;
	}
	$scope.getAvailableTrips = function(journeyType){
		b2cBusResultsManager.getAvailableTrips(journeyType,function(data){
			$scope.trips = data;
		})
	}
	$scope.sleeper = [];
	$scope.upper=[];
	$scope.lower=[];
	$scope.CurrentTrips = []
	$scope.seatLayout = function(trip,index){
		console.log('Trip ' +trip);
		$scope.CurrentTrips[index]=trip;
		var layoutId = trip.layoutId,serviceNumber = trip.serviceNumber;
		angular.element(document.getElementById('busSeatLayout-'+serviceNumber)).empty();
		$scope.seatlayout = '';
		b2cBusResultsManager.seatLayout(layoutId,function(data){
			$scope.seatlayout = data;
			$scope.lowRows = [];
			$scope.upRows = [];
			$scope.upper[index] = [];
			$scope.lower[index] = [];
			angular.forEach($scope.seatlayout.rows,function(row){
				angular.forEach(row.seats,function(seat){
					if(seat.sleeper){
						if(seat.upperDeck){
							$scope.upRows.push(seat);
						}else{
							$scope.lowRows.push(seat);
						}
						$scope.sleeper[index] = seat.sleeper ;
						console.log('assigned to '+$scope.sleeper[index])
					}else{
					}
					
				})
				if($scope.upRows.length>0)
					$scope.upper[index].push($scope.upRows);
				if($scope.lowRows.length>0)
					$scope.lower[index].push($scope.lowRows);
				$scope.upRows=[];
				$scope.lowRows=[];
			});
			console.log($scope.upper[index])
	
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
	$scope.busJourney = [];
	$scope.seatSelect = function(seat,trip,tripIndex){
		var serviceNumber = trip.serviceNumber;
		$scope.busJourney[tripIndex] = $scope.busJourney[tripIndex] ? $scope.busJourney[tripIndex] : [];
		$scope.busJourney[tripIndex].busJourney=$scope.bJny;
		$scope.busJourney[tripIndex].busJourney.fare = trip.serviceFares[0].fare;
		$scope.busJourney[tripIndex].busJourney.serviceName = trip.serviceName;
		$scope.busJourney[tripIndex].busJourney.serviceNumber = trip.serviceNumber;
		if(!$scope.busJourney[tripIndex].busJourney.seatNumbers)
			$scope.busJourney[tripIndex].busJourney.seatNumbers= [];
		if(seat.seatStatus=="BOOKED"||seat.seatStatus=="UNAVAILABLE"){
		}else{
			if(seat.seatStatus=="AVAILABLE"){
				$scope.seatTotalFare=seat
				$scope.seatSelected = true;
				$scope.busJourney[tripIndex].busJourney.seatNumbers.push(seat.number);
				$scope.busJourney[tripIndex].busJourney.totalFare=( $scope.busJourney[tripIndex].busJourney.totalFare ? $scope.busJourney[tripIndex].busJourney.totalFare :  0 ) + $scope.busJourney[tripIndex].busJourney.fare;
				seat.seatStatus = 'BOOKING_INPROGRSS';
			}else{
				seat.seatStatus = 'AVAILABLE';
				var index = $scope.busJourney[tripIndex].busJourney.seatNumbers.indexOf(seat.number)
				if(index>=0){
					$scope.busJourney[tripIndex].busJourney.seatNumbers.splice(index, 1);
					$scope.busJourney[tripIndex].busJourney.totalFare-=$scope.busJourney[tripIndex].busJourney.fare;
				}
				if($scope.selectedSeates.lenght<=0)
					$scope.seatSelected = false;
			}	
		}
	}
	$scope.continueToPayment = function(busJourney){
		$log.debug(busJourney)
		busJourney.boardingPoints = JSON.parse(busJourney.boardingPoints)
		busJourney.dropingPoints = JSON.parse(busJourney.dropingPoints)
		b2cBusResultsManager.blockSeat(busJourney,function(data){
			$location.url('/detailsPayment');
		})
	}
});