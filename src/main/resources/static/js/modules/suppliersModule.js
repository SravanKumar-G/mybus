"use strict";
/*global angular, _*/

angular.module('myBus.suppliers', ['ngTable', 'ui.bootstrap'])
.controller("SuppliersListController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,paginationService, suppliersManager){
	$scope.headline = "Suppliers";
	$scope.suppliers = [];
	var loadTableData = function (tableParams) {
		$scope.loading = true;
        suppliersManager.getSuppliers(function(response){
			if(angular.isArray(response)){
				$scope.loading = false;
				$scope.suppliers = response;
			}
		})
	 };

    $scope.init = function() {
        $scope.tableParams = new NgTableParams({
            sorting: {
                name: 'asc'
            }
        }, {
            counts: [],
            getData: function (params) {
                loadTableData(params);
            }
        });
    };

    $scope.init();
    $scope.$on('reloadSuppliers', function (e, value) {
        loadTableData();
    });
	$scope.handleEdit = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'edit-supplier-modal.html',
	        controller : 'EditSupplierController',
	        resolve : {
                supplierId : function(){
	                return id;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditSupplierController', function ($scope, $rootScope, $uibModal, $http, $log, suppliersManager, supplierId) {
	$scope.supplier = {};
	$scope.save =function(){
		if(supplierId){
            suppliersManager.updateSupplier($scope.supplier, function(data){
                $scope.supplier = data;
                $rootScope.modalInstance.close(data);
            });
		} else {
            suppliersManager.addSupplier($scope.supplier, function(data){
                $scope.supplier = data;
                $rootScope.modalInstance.close(data);
            });
		}
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };
	if(supplierId) {
        suppliersManager.getSupplier(supplierId,function(data){
            $scope.supplier = data;
        });
	}
    $scope.reset = function(){
		$scope.supplier= {};
	};
}).factory("suppliersManager",function($rootScope,$http,$log){
	return {

        getSuppliers: function (callback) {
            $http.get("/api/v1/suppliers/").then(function (response) {
                callback(response.data);
            }, function (error) {
                swal("oops", error, "error");
            });
        },

		addSupplier: function(fillingStation,callback) {
			$http.post("/api/v1/suppliers/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "FillingStation has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},

		getSupplier : function(id,callback){
			$http.get("/api/v1/suppliers/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},

		updateSupplier : function(fillingStation,callback){
			$http.put("/api/v1/suppliers/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "FillingStation has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			})
		}
	}
});