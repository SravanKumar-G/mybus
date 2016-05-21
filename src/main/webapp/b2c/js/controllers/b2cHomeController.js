/**
 * Created by srinu.
 */
"use strict";
/*global angular, _*/

angular.module('myBusB2c.b2cHome', ['ngTable', 'ui.bootstrap'])
.controller('B2cHomeController',function($scope, $log, $modal, $filter, ngTableParams,$location, $rootScope,b2cHomeManager) {
	$log.debug("in myBusB2c.b2cHome at B2cHomeController");
	$scope.headline = "Buy Your Tickets Now!";
	$scope.allCities =[];
	
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
});
