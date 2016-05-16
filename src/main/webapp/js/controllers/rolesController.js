/**
 * Created by yks_Srinivas
 */
"use strict";

angular.module('myBus.roleModules', ['ngTable', 'ui.bootstrap'])
.controller("RolesController",function($scope, $log, ngTableParams, $location, $modal, $state, $filter, roleManager){

	$scope.headline="User Roles";
	
	$scope.currentPageRoles={};
	
	var loadTableData = function (tableParams, $defer) {
        var data = roleManager.getRoles();
        var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
        $scope.roles= orderedData;
        tableParams.total(data.length);
        if (angular.isDefined($defer)) {
            $defer.resolve(orderedData);
        }
        $scope.currentPageRoles = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
    };
    $scope.rolesContentTableParams = new ngTableParams({
        page: 1,
        count:25,
        sorting: {
            state: 'asc',
            name: 'asc'
        }
    }, {
        total: $scope.currentPageRoles.length,
        getData: function ($defer, params) {
            $scope.$on('roleInitComplete', function (e, value) {
                loadTableData(params);
            });
        }
    });
	$scope.deleteRole = function(roleID){
		roleManager.deleteRole(roleID,function(data){
			$modalInstance.close();
		})
	};
	
	$scope.$on('roleInit',function(e,value){
		roleManager.fechAllRoles()
	});
	
	$scope.$on('roleInitComplete',function(e,value){
		loadTableData($scope.rolesContentTableParams)
	});
	
	$scope.handleClickAddNewRole = function (size) {
		var modalInstance = $modal.open({
			templateUrl: 'addOrUpdate-newRoles.html',
			controller: 'AddOrUpdateNewRoleController',
			size: size,
			resolve: {
				neighborhoodId: function () {
					return null;
				}
			}
		});
		modalInstance.result.then(function (data) {
			$log.debug("results from modal: " + angular.toJson(data));
			$scope.cityContentTableParams.reload();
		}, function () {
			$log.debug('Modal dismissed at: ' + new Date());
		});
	};
	
	$scope.initForUpdateRole = function(roleId){
        var modalInstance = $modal.open({
            templateUrl : 'addOrUpdate-newRoles.html',
            controller : 'AddOrUpdateNewRoleController',
            resolve : {
            	neighborhoodId : function(){
                    return roleId;
                }
            }
        });
    };
	roleManager.fechAllRoles();
})
.controller("AddOrUpdateNewRoleController",function($scope,$log, $modalInstance, roleManager, neighborhoodId){
	
	$scope.role ={};
	$scope.isForRoleUpdate = false;
	
	if(neighborhoodId){
		roleManager.getRoleById(neighborhoodId,function(data){
			$scope.role=data;
			$scope.isForRoleUpdate = true
		})
	}
	
	$scope.addNewRole = function() {
		roleManager.createRole($scope.role,function(data){
			$modalInstance.close();
		})
	};
	
	$scope.UpdateNewRole = function(roleID,role) {
		roleManager.updateRole(roleID,role,function(data){
			$modalInstance.close();
		})
	};
	$scope.isInputValid = function () {
		return ($scope.role.name || '') !== '';
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
});