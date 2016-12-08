//"use strict";
/*global angular, _*/
angular.module('myBus.bookingModules', ['ui.bootstrap'])

// ==================================================================================================================
  // ====================================    BookingController   ================================================
  // ==================================================================================================================

  .controller('BookingController', function ($scope,bookingHelper,$location) {
	
	  var bookingCtrl = this;
	  bookingCtrl.contactInfo = {
			  email : null,
			  phone : null
	  };
	  
	  bookingCtrl.passengerInfo=[];
	  bookingCtrl.showPayment = false;
	  bookingCtrl.passengerInfo.boardingPoint = bookingHelper.getTripInfo().boardingPoint;
	  bookingCtrl.passengerInfo.primaryPassenger = {};
	  bookingCtrl.passengerInfo.primaryPassenger.name= null;
	  bookingCtrl.passengerInfo.primaryPassenger.age = null;
	  bookingCtrl.passengerInfo.primaryPassenger.gender = null;
	  bookingCtrl.passengerInfo.primaryPassenger.seatNumber= bookingHelper.getTripInfo().selectedSeats[0];
	  bookingCtrl.passengerInfo.copassengers = [];
	  var copassengerCount = bookingHelper.getTripInfo().selectedSeats.length -1;
	  for (var i=1; i <= copassengerCount ;i++){
		  var temp = {
				  name : null,
				  gender : null,
				  age : null,
				  seatNumber : bookingHelper.getTripInfo().selectedSeats[i]
			  };
		  bookingCtrl.passengerInfo.copassengers.push(temp);
	  };
	  
	  bookingCtrl.doPayment = function() {
		  
		  var bookingInfo = {};
		  var passengers = [];
		  
		  passengers.push(bookingCtrl.passengerInfo.primaryPassenger);
		  if ( bookingCtrl.passengerInfo.length > 0) {
			  passengers.push(bookingCtrl.passengerInfo.copassengers);
		  }
		  bookingInfo.ONE_WAY = passengers;
		  
		  
		  bookingHelper.setPassengerInfo(bookingInfo);
		  $location.url("/payment");
	  }
	  
	  return bookingCtrl;
  });