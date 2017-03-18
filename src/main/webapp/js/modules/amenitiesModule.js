"use strict";
/*global angular, _*/

angular.module('myBus.amenitiesModule', ['ngTable', 'ui.bootstrap'])
.controller("AmenitiesController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams, amenitiesManager){
	$scope.headline = "Amenities";
	$scope.amenities = [];
	$scope.currentPageOfAmenities=[];
	$scope.amenity = {};
	 var loadTableData = function (tableParams, $defer) {
         var data = amenitiesManager.getAmenities();
		 if(tableParams) {
			 var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
			 $scope.amenities = orderedData;
			 tableParams.total(data.length);
			 if (angular.isDefined($defer)) {
				 $defer.resolve(orderedData);
			 }
			 $scope.currentPageOfAmenities = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
		 }
     };
     $scope.amenitiesContentTableParams = new NgTableParams(
		 {
			 page: 1,
			 count:25,
			 sorting: {
				 name: 'asc'
			 }
		 },
		 {
			 total: $scope.currentPageOfAmenities.length,
			 getData: function (params) {
				 loadTableData(params);
			 }
		 }
     );
	$scope.$on('amenitiesInitComplete', function (e, value) {
		loadTableData($scope.amenitiesContentTableParams);
	});
	$scope.$on('amenitiesinitStart', function (e, value) {
		amenitiesManager.fechAmenities();
	});
	 amenitiesManager.fechAmenities();
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
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl: 'add-Amenity-modal.html',
	        controller: 'AddAmenityModalController',
	        size: size
	    });
		$rootScope.modalInstance.result.then(function (data) {
	        $log.debug("results from modal: " + angular.toJson(data));
	        //$scope.cityContentTableParams.reload();
	    }, function () {
	        $log.debug('Modal dismissed at: ' + new Date());
	    });
	};

	$scope.handleClickUpdateAmenity = function(amenityID){
		$rootScope.modalInstance = $uibModal.open({
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

.controller('UpdateAmenityModalController', function ($scope, $rootScope, $uibModal, $http, $log, amenitiesManager, amenityId) {
	$log.debug("in UpdateAmenityModalController");
    $scope.amenity = {};
	$scope.updateAmenity =function(){ 
		amenitiesManager.updateAmenity($scope.amenity,function(data){
			$scope.amenity = data;
			$rootScope.modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
    };
		console.log("loading amenity info....");
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
.controller('AddAmenityModalController', function ($scope,$state, $http, $log, $rootScope,amenitiesManager) {
	$log.debug("in AddAmenityModalController");
	
    $scope.amenity = {
        name: null,
        active: false
    };
    
    $scope.addAmenity = function(){ 
    	amenitiesManager.addAmenity($scope.amenity,function(data){
    		$scope.amenity = data;
			$rootScope.modalInstance.close(data);
    	});
    };
	
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.amenity.name || '') !== '';
    };
    
})
.factory("amenitiesManager",function($rootScope,$http,$window,$log){
	var amenities = [];
	return {

		fechAmenities : function(){
			$http.get("/api/v1/amenities").then(function(response){
				amenities= response.data;
				$rootScope.$broadcast('amenitiesInitComplete');
			},function(error){
				swal("oops", error, "error");
			});
		},

		getAmenities :function(){
			return amenities;
		},
		getAllAmenities : function(callback){
			$http.get("/api/v1/amenities").then(function(response){
				callback(response.data);
				$rootScope.$broadcast('amenitiesInitComplete');
			},function(error){
				swal("oops", error, "error");
			});
		},
		getAmenitiesName : function() {
			return $http(
				{
					method:'GET',
					url:'/api/v1/amenities'
				}
			);
		},
		addAmenity: function(amenity,callback) {
			$http.post("/api/v1/amenity",amenity).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('amenitiesinitStart');
				swal("Great", "Amenity has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},

		getAmenityByID : function(amenityID,callback){
			$http.get("/api/v1/amenity/"+amenityID).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},

		updateAmenity : function(amenity,callback){
			$http.put("/api/v1/amenity",amenity).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('amenitiesinitStart');
				swal("Great", "Amenity has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},
		deleteAmenity : function(amenityID,callback){

			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Amenity?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: true,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

				$http.delete("/api/v1/amenity/"+amenityID).then(function(data){
					callback(data);
					$rootScope.$broadcast('amenitiesinitStart');
					swal("Deleted!", "Amenity has been deleted successfully!", "success");
				},function(error){
					swal("Oops", "We couldn't connect to the server!", "error");
				});

			});
		}
	}


});;