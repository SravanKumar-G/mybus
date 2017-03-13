"use strict";
/*global angular, _*/

angular.module('myBus.serviceComboModule', ['ngTable', 'ui.bootstrap'])
.controller("ServiceComboController",function($rootScope, $scope, $uibModal, $location, $filter, $log,NgTableParams, serviceComboManager){
	$scope.headline = "Service Combos";
	$scope.serviceCombos = [];
	$scope.currentPageOfCombos=[];
	var loadTableData = function (tableParams) {
		serviceComboManager.getAll(function(data){
		 if(tableParams) {
			 var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
			 $scope.serviceCombos = orderedData;
			 tableParams.total(orderedData.length);
			 $scope.currentPageOfCombos = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
		 }
		});
	};
	$scope.$on('reloadServiceCombo', function (e, value) {
		loadTableData($scope.serviceComboTableParams);
	});
	$scope.serviceComboTableParams = new NgTableParams(
		{
		 page: 1,
		 count:25,
		 sorting: {
			 name: 'asc'
		 }
		},
		{
		 total: $scope.currentPageOfCombos.length,
		 getData: function (params) {
			 loadTableData(params);
		 }
		});

	$scope.delete = function(id){
		serviceComboManager.delete(id,function(data){
			console.log('deleted ...');
		});
	}

	$scope.handleClickEdit = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'update-serviceCombo-modal.html',
	        controller : 'EditServiceComboModalController',
	        resolve : {
				comboId : function(){
	                return id;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditServiceComboModalController', function ($scope, $rootScope, $location, $uibModal, $http, $log, serviceComboManager, comboId) {
	$log.debug("in EditServiceComboModalController comboId"+ comboId);
    $scope.serviceCombo = {};
	$scope.save =function(){
		serviceComboManager.save($scope.serviceCombo,function(data){
			$rootScope.modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    $scope.isInputValid = function () {
        return ($scope.serviceCombo.name || '') !== '';
    };
	if(comboId) {
		serviceComboManager.getByID(comboId,function(data){
			$scope.serviceCombo = data;
		});
	}

    $scope.resetAmenity = function(){
		$scope.serviceCombo= {};
	};
})
.factory("serviceComboManager",function($rootScope,$http,$location,$log){
	var serviceCombos = [];
	return {
		getAll : function(callback){
			$http.get("/api/v1/serviceCombos").then(function(response){
				serviceCombos= response.data;
				callback(serviceCombos);
			},function(error){
				swal("oops", error, "error");
			});
		},
		getServiceCombos :function(){
			return serviceCombos;
		},
		save: function(serviceCombo,callback) {
			if(!serviceCombo.id) {
				$http.post("/api/v1/serviceCombo",serviceCombo).then(function(response){
					$location.url('servicecombo');
					$rootScope.modalInstance.close();
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Great", "ServiceCombo has been successfully added", "success");
				},function(error){
					swal("oops", error, "error");
				});
			} else {
				$http.put("/api/v1/serviceCombo",serviceCombo).then(function(response){
					$location.url('servicecombo');
					$rootScope.modalInstance.close();
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Great", "ServiceCombo has been updated successfully", "success");
				},function(error){
					swal("oops", error, "error");
				});
			}
		},
		getByID : function(id,callback){
			$http.get("/api/v1/serviceCombo/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},
		delete : function(id,callback){
			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this ServiceCombo?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: true,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){
				$http.delete("/api/v1/serviceCombo/"+id).then(function(data){
					$rootScope.$broadcast("reloadServiceCombo");
					swal("Deleted!", "ServiceCombo has been deleted successfully!", "success");
				},function(error){
					swal("Oops", "We couldn't connect to the server!", "error");
				})
			})
		}
	}
});;