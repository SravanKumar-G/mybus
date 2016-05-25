/**
 * Created by srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cResults', ['ngTable', 'ui.bootstrap'])
.controller('B2cResultsController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,$compile) {
	
	$scope.trips=[{
		'fromCityId':'hydrabad',
		'toCityId':'bangalore',
		'serviceFares':'400',
		'tripDate':'24-05-2016',
		'active':'true',
		'boardingPoints':'[{}]',
		'dropingPoints':'[{}]',
		'serviceId':'123',
		'routeId':'123',
		'layoutId':'456',
		'totalSeats':40,
		'availableSeats':20,
		'rows':'[{}]',
		'amenities':'[{},{}]',
		'vehicleAllotmentId':'24-05-2016'
	},
	{

		'fromCityId':'hydrabad',
		'toCityId':'bangalore',
		'serviceFares':'500',
		'tripDate':'24-05-2016',
		'active':'true',
		'boardingPoints':'[{}]',
		'dropingPoints':'[{}]',
		'serviceId':'12354',
		'routeId':'123',
		'layoutId':'456',
		'totalSeats':40,
		'availableSeats':20,
		'rows':'[{}]',
		'amenities':'[{},{}]',
		'vehicleAllotmentId':'24-05-2016'
	},
	{

		'fromCityId':'hydrabad',
		'toCityId':'bangalore',
		'serviceFares':'700',
		'tripDate':'24-05-2016',
		'active':'true',
		'boardingPoints':'[{}]',
		'dropingPoints':'[{}]',
		'serviceId':'1236',
		'routeId':'12312',
		'layoutId':'4562',
		'totalSeats':40,
		'availableSeats':20,
		'rows':'[{}]',
		'amenities':'[{},{}]',
		'vehicleAllotmentId':'24-05-2016'
	}];
	$scope.rows = [{
		'seats':[{
			number:1,
			displayName:'A1',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:2,
			displayName:'A2',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		,{
			number:3,
			displayName:'A3',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		}
		,{
			number:4,
			displayName:'A4',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:5,
			displayName:'A5',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:6,
			displayName:'A6',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		}],

		middleRow:false,

		window:true,

		sideSleeper:false
	},
	{
		'seats':[{
			number:11,
			displayName:'1A1',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		}],

			middleRow:false,

			window:true,

			sideSleeper:false
		
	},
	{
		'seats':[{
			number:11,
			displayName:'1A1',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:12,
			displayName:'1A2',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		,{
			number:13,
			displayName:'1A3',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		}
		,{
			number:14,
			displayName:'1A4',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:15,
			displayName:'1A5',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		},
		{
			number:16,
			displayName:'1A6',
			display:true,
			isForFemale:false,
			gender:'male',
			sleeper:false,
			bookingId:'',
			canceledBookingIds:[],
			seatStatus:'AVAILABLE',
			upperDeck:false,
			active:true
		}],

		middleRow:false,

		window:true,

		sideSleeper:false
	}];
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
	
	$scope.seatLayout = function(serviceId){
		var Layout = '';
			angular.forEach($scope.rows,function(row){
				Layout = Layout+'<tr>';
				angular.forEach(row.seats,function(){
					Layout = Layout+'<td><span class="seat"></span></td>';
				});
				Layout = Layout+'<tr>';
			});
			angular.element(document.getElementById('seatLayout-'+serviceId)).append($compile(Layout)($scope));
	}
	
	
});