"use strict";
/*global angular, _*/

angular.module('myBus.amenitiesModule', ['ngTable', 'ui.bootstrap'])
.controller("AmenitiesController",function($rootScope, $scope, $modal, $filter, $log,ngTableParams, amenitiesManager){
	
	$scope.headline = "Available Amenities";
	
	$scope.amenities = [];
	
	$scope.currentPageOfAmenity=[];
	
	$scope.amenity = {};
	amenitiesManager.fechAmenities();
	
	 var loadTableData = function (tableParams, $defer) {
         var data = amenitiesManager.getAmenities();
         var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
         $scope.amenities = orderedData;
         tableParams.total(data.length);
         if (angular.isDefined($defer)) {
             $defer.resolve(orderedData);
         }
         $scope.currentPageOfAmenity = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
     };
	
     $scope.$on('amenitiesInitComplete', function (e, value) {
         loadTableData($scope.amenitiesContentTableParams);
     });
     $scope.$on('amenitiesinitStart', function (e, value) {
    	 amenitiesManager.fechAmenities();
     });
     $scope.amenitiesContentTableParams = new ngTableParams(
    		 {
    			 page: 1,
    			 count:25,
    			 sorting: {
    				 state: 'asc',
    				 name: 'asc'
    			 }
    		 }, 
    		 {
    			 total: $scope.currentPageOfAmenity.length,
    			 getData: function ($defer, params) {
    				 $scope.$on('amenitiesInitComplete', function (e, value) {
    					 loadTableData(params);
    				 });
    			 }
    		 }
     );
     
	
	$scope.getAllAmenities = function(){
		amenitiesManager.getAllAmenities(function(data){
			$scope.amenities =data;
		});
	};
	
	$scope.getAmenityById = function(){ 
		amenitiesManager.getAmenityByID($scope.amenity.id,function(data){
			$scope.amenity = data;
		});
	}
	
	$scope.deleteAmenityById = function(amenityID){ 
		amenitiesManager.deleteAmenity(amenityID,function(data){
			$scope.amenity = data;
		});
	}
	
	
	$scope.handleClickAddAmenity = function (size) {
	    var modalInstance = $modal.open({
	        templateUrl: 'add-Amenity-modal.html',
	        controller: 'AddAmenityModalController',
	        size: size,
	        resolve: {
	            neighborhoodId: function () {
	                return null;
	            }
	        }
	    });
	    modalInstance.result.then(function (data) {
	        $log.debug("results from modal: " + angular.toJson(data));
	        //$scope.cityContentTableParams.reload();
	    }, function () {
	        $log.debug('Modal dismissed at: ' + new Date());
	    });
	};

	$scope.handleClickUpdateAmenity = function(amenityID){
	    var modalInstance = $modal.open({
	        templateUrl : 'update-amenity-modal.html',
	        controller : 'UpdateAmenityModalController',
	        resolve : {
	            amenityId : function(){
	                return amenityID;
	            }
	        }
	    });
	};
	
	//$scope.getAllAmenities();
})
// ========================== Modal - Update Amenity  =================================

.controller('UpdateAmenityModalController', function ($scope, $modalInstance, $http, $log, amenitiesManager, amenityId) {
	$log.debug("in UpdateAmenityModalController");
    $scope.amenity = {};
 
	$scope.updateAmenity =function(){ 
		amenitiesManager.updateAmenity($scope.amenity,function(data){
			$scope.amenity = data;
			$modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
            
    };
    amenitiesManager.getAmenityByID(amenityId,function(data){
    	$scope.amenity = data;
	});
	
    
    $scope.resetAmenity = function(){
		$scope.amenity= {};
	};
})

//
// ========================== Modal - Add Amenity =================================
//
.controller('AddAmenityModalController', function ($scope, $modalInstance,$state, $http, $log, amenitiesManager) {
	$log.debug("in AddAmenityModalController");
	
    $scope.amenity = {
        name: null,
        active: false
    };
    
    $scope.addAmenity = function(){ 
    	amenitiesManager.addAmenity($scope.amenity,function(data){
    		$scope.amenity = data;
    		$modalInstance.close(data);
    	});
    };
	
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
    };
    
});