/**
 * Created by srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cHome', ['ngTable', 'ui.bootstrap'])
.controller('B2cHomeController',function($scope, $log, $modal,$state, $filter, ngTableParams,$location, $rootScope,b2cHomeManager) {
	
	$log.debug("in myBusB2c.b2cHome at B2cHomeController");
	$scope.headline = "Buy Your Tickets Now!";
	$scope.allCities =[];
	$scope.dateOfJourney='';

	$scope.getAllCities = function(){
		b2cHomeManager.getAllcites(function(data){
			$scope.allCities = data;
		})
	}
	
	$scope.getAllCities();
	 $scope.selectFromCity = function(item){
         $scope.fromCity = item.name;
     };

     $scope.selectToCity = function(item, model, label, event){
         $scope.toCity = item.name;
     };
     
     var date = new Date();
     $scope.minDate = $filter('date')(date.setDate((new Date()).getDate()),'yyyy-MM-dd');
     $scope.maxDate = $filter('date')(date.setDate((new Date()).getDate() + 30),'yyyy-MM-dd');
     
     $scope.onSelectDateOfJourney = function(){
	     if($scope.dateOfJourney!=''){
	    	 $scope.rminDate = $filter('date')($scope.dateOfJourney,'yyyy-MM-dd');
	     }else{
	    	 $scope.rminDate = $scope.minDate; 
	     }
     }
    
     $scope.handleClickUpdateRoute = function(routeId){
       
     };
     
     $scope.searchBuses = function(){
	    $log.debug("$scope.busJourney -"+$scope.busJourney);
	    $location.url('/results/'+$scope.busJourney);
     }
});
