"use strict";
/*global angular, _*/

angular.module('myBus.tripModule', ['ngTable', 'ui.bootstrap'])
.controller("TripController",function($rootScope,$scope, $log, $filter,ngTableParams,tripManager,cityManager,$cacheFactory){

	var tripCtrl = this;
	
	tripCtrl.trips = [];
	
	tripCtrl.trip = {};
	
	tripCtrl.currentPageTrips = [];
	
	tripCtrl.currentSelectionSeats = [];
	
	tripCtrl.busLayout = {};
	
	var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};
	
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
	
	
	tripCtrl.getTrip = function(tripId){
		 $log.debug("Selected trip..." + tripId);
		for(var i=0; i<tripCtrl.trips.length ; i++)	{
			if ( tripCtrl.trips[i].id == tripId) {
				
				tripCtrl.trip = tripCtrl.trips[i];
			}
		}
		tripCtrl.showLayout();
	};
	
	tripCtrl.showLayout = function() {
		 $log.debug("showing layout for trip..." + tripCtrl.trip.id);
         var cache = $cacheFactory.get($rootScope.id);
		 if(cache){
		     tripCtrl.busLayout = cache.get(tripCtrl.trip.layoutId);
		 }
		 if (typeof tripCtrl.busLayout.id == 'undefined') {
			tripManager.getLayoutByID(tripCtrl.trip.layoutId,function(data) {
				tripCtrl.busLayout = data;
			});
		 }
		 
		 if(tripCtrl.busLayout.type === 'SLEEPER'){
			 tripCtrl.busLayout.sleeper = true;
			 tripCtrl.busLayout.layoutCls = 'sleeper';
         }else{
        	 tripCtrl.busLayout.layoutCls = 'seat';
         }
		 var rows = angular.copy(tripCtrl.busLayout.rows);
		 
		 if(tripCtrl.busLayout.sleeper && tripCtrl.busLayout.seatsPerRow && tripCtrl.busLayout.totalRows){
             for(var k = 0; k < 2; k++){
                 if(k===0){
                	 tripCtrl.busLayout.upper = getSeats(true, rows);
                	 tripCtrl.busLayout.upperHeader = 'Upper';
                 }else{
                	 tripCtrl.busLayout.lower = getSeats(true, rows);
                	 tripCtrl.busLayout.lowerHeader = 'Lower';
                 }
             }
         }else if(tripCtrl.busLayout.seatsPerRow && tripCtrl.busLayout.totalRows){
        	 tripCtrl.busLayout.rows = getSeats(false, rows);
         }
		 
	};
	
	function getName(id){
        return $filter('filter')(seatNames.seats, {id: id })[0];
    }
	
	function getSeats(sleeper, oldrows){
        var rows = [];
        var middleseat = tripCtrl.busLayout.middleRowSeat;
        var  middleseatpos = tripCtrl.busLayout.middleRowPosition;
        var cols = tripCtrl.busLayout.seatsPerRow;

        if(sleeper && cols > 2){
            cols = 2;
        }

        if(middleseatpos > 0){
            cols = parseInt(cols) +1;
        }

        if (cols > 4){
        	tripCtrl.busLayout.isBig = true;
        }

        for (var i = 1; i <= cols; i++){
            var seats = [];
            if(i === parseInt(middleseatpos)){
                for (var j = 1; j <= tripCtrl.busLayout.totalRows; j++){
                    var number = getName(j).name+''+i;
                    console.log(j+','+tripCtrl.busLayout.totalRows);
                    if(angular.equals(middleseat, true) && angular.equals(j, parseInt(tripCtrl.busLayout.totalRows))){
                        if(!sleeper){
                        	tripCtrl.busLayout.totalSeats = tripCtrl.busLayout.totalSeats + 1;
                            seats.push({
                            	number : number, 
                            	[number]: number
                            });
                        }
                    }else{
                        seats.push({number : null, [number]: null});
                    }
                }
            }else{
                for (var j = 1; j <= tripCtrl.busLayout.totalRows; j++){
                    var number = getName(j).name+''+i;
                    var displayName = number;
                    if(oldrows && !sleeper){
//                        console.log(rows);
//                        var row = oldrows[i-1].seats;
//                        displayName = $filter('filter')(row, {number: number})[0].displayName;
                    }
                    tripCtrl.busLayout.totalSeats = tripCtrl.busLayout.totalSeats + 1;
                    seats.push({number : number, [number]: displayName});
                }
            }
            rows.push({seats :seats})
        }
        return rows;
    };
    
    tripCtrl.getSeatName = function(seat){
        return seat.number;
    };

    
    tripCtrl.markSeatForSelection = function(seat , rowNumber) {
    	tripCtrl.currentSelectionSeats[tripCtrl.currentSelectionSeats.length] = rowNumber + "-" + seat.number;
    	
    };
    
    $scope.getMatchingClass = function(rowNumber,seat) {
    	if ( seat.number == null) {
    		return "";
    	}  
    	if ( tripCtrl.currentSelectionSeats.indexOf(rowNumber + "-" + seat.number) !== -1) {
    		return "selectingSeat";
    	}
    	return "seat";
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