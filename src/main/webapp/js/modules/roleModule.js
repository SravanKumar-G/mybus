/**
 * Created by yks_Srinivas
 */
"use strict";

angular.module('myBus.roleModule', ['ngTable', 'ui.bootstrap'])
	.controller("RoleController",function($scope, $rootScope, $log, NgTableParams, $location, $uibModal, $state, $filter, roleManager){

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
		$scope.rolesContentTableParams = new NgTableParams({
			page: 1,
			count:25,
			sorting: {
				state: 'asc',
				name: 'asc'
			}
		}, {
			total: $scope.currentPageRoles.length,
			getData: function (params) {
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
			$rootScope.modalInstance = $uibModal.open({
				templateUrl: 'addOrUpdate-newRoles.html',
				controller: 'AddOrUpdateNewRoleController',
				size: size,
				resolve: {
					neighborhoodId: function () {
						return null;
					}
				}
			});
			$rootScope.modalInstance.result.then(function (data) {
				$scope.rolesContentTableParams.reload();
			}, function () {
			});
		};

		$scope.initForUpdateRole = function(roleId){
			$rootScope.modalInstance = $uibModal.open({
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
	.controller("AddOrUpdateNewRoleController",function($scope, $rootScope, $log, $uibModal, roleManager, neighborhoodId){

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
				$rootScope.modalInstance.close();
			})
		};

		$scope.UpdateNewRole = function(roleID,role) {
			roleManager.updateRole(roleID,role,function(data){
				$rootScope.modalInstance.close();
			})
		};
		$scope.isInputValid = function () {
			return ($scope.role.name || '') !== '';
		};

		$scope.cancel = function () {
			$rootScope.modalInstance.dismiss('cancel');
		};
	})
	.controller('ManagingRolesController', function ($scope, $log, $state, roleManager) {
		$scope.headline = "Managing Roles";
		$scope.updateAllManagingRoles = [];
		$scope.isEditable = false;
		$scope.getPermissions = function(){
			roleManager.getAllRoles(function(data){
				$scope.roles=data;
				$scope.menus = [];

				angular.forEach($scope.roles,function(role){
					$scope.updateAllManagingRoles[role.name]={'id':role.id,'name':role.name,'menus':role.menus};
				});

				angular.forEach($state.get(),function(eachState){
					if(eachState.level === 1) {
						$scope.menus.push({'name':eachState.name});
					}
				});
				angular.forEach($scope.menus,function(menu){
					angular.forEach($scope.roles,function(role){
						if(!menu.permissions){
							menu.permissions = [];
						}
						if(!role.menus) {
							role.menus=[];
						}

						if(role.menus.indexOf(menu.name)!=-1){
							menu.permissions.push({'id':role.id,'roleName':role.name, 'allowed':true});
						}else {
							menu.permissions.push({'id':role.id,'roleName':role.name, 'allowed':false});
						}
					});
				});
				//menus=[{name:'menu1',permissions:[admin:true,employee:false]}, ]
			});
		};
		$log.debug($scope.updateAllManagingRoles +"$scope.updateAllManagingRoles");
		$scope.getPermissions();

		$scope.addOrRemovefromRoles = function(checkedOrUnchecked,menuName,roleName){
			if(checkedOrUnchecked) {
				if($scope.updateAllManagingRoles[roleName]){
					if(!$scope.updateAllManagingRoles[roleName].menus){
						$scope.updateAllManagingRoles[roleName].menus=[];
						$scope.updateAllManagingRoles[roleName].menus.push(menuName);
					}else{
						$scope.updateAllManagingRoles[roleName].menus.push(menuName);
					}
				}else {
					$scope.updateAllManagingRoles[roleName].name=roleName
					$scope.updateAllManagingRoles[roleName].menus=[];
					$scope.updateAllManagingRoles[roleName].menus.push(roleName);
				}
			}else {
				var index = $scope.updateAllManagingRoles[roleName].menus.indexOf(menuName);
				$scope.updateAllManagingRoles[roleName].menus.splice(index,1)
			}
			$log.debug("checkedOrUnchecked : "+checkedOrUnchecked+" menuName : "+menuName+"  roleName :"+roleName)
		}
		$scope.updateManagingRoles = function(){
			$log.debug("update managing roles");
			$scope.isEditable = false;
			angular.forEach($scope.roles,function(role){
				var manageRoles = $scope.updateAllManagingRoles[role.name];
				roleManager.updateManageingRole(manageRoles.id,manageRoles,function(data){
				})
			})
		}
		$scope.editManagingRoles = function(){
			$scope.isEditable = $scope.isEditable?false:true;
			$log.debug("edit managing roles");
		}
	}).factory('roleManager', function ($rootScope, $http,$filter) {
	var roles=[];
	return {
		fechAllRoles : function(){
			$http.get('/api/v1/roles')
				.then(function (response) {
					roles = response.data;
					$rootScope.$broadcast("roleInitComplete");
				},function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		getRoles : function(){
			return roles;
		},
		getAllRoles : function(callback){
			$http.get('/api/v1/roles')
				.then(function (response) {
					callback(response.data);
				},function(err,status) {
					sweetAlert("Error",err.message,"error");
				});
		},
		createRole : function (role,callback) {
			$http.post('/api/v1/createRole',role)
				.then(function (response) {
					callback(response.data);
					$rootScope.$broadcast("roleInit");
					swal("Great", "Role has been successfully added", "success");
				},function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		updateRole : function (roleID,role,callback) {
			$http.put('/api/v1/role/'+roleID,role)
				.then(function (response) {
					callback(response.data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.$broadcast("roleInit");
				},function(err) {
					callback(err);
					sweetAlert("Error",err.message,"error");
				});
		},
		deleteRole : function (roleID,callback) {

			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Role?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

				$http.delete('/api/v1/role/'+roleID)
					.then(function (response) {
						swal("Great", "Roles has been updated successfully", "success");
						$rootScope.$broadcast("roleInit");
						callback(response.data);
					},function(err) {
						callback(err);
						sweetAlert("Error",err.message,"error");
					});
			})

		},
		getRoleById : function (roleID,callback) {
			$http.get('/api/v1/role/'+roleID)
				.then(function (response) {
					callback(response.data);
				},function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		getRoleByRoleName : function (rolesName,callback) {
			$http.get('/api/v1/roleByName/'+rolesName)
				.then(function (response) {
					callback(response.data);
				},function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		getRoleNames : function (callback) {
			$http.get('/api/v1/role/names')
				.then(function (response) {
					callback(response.data);
				},function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		updateManageingRole : function (roleID,role,callback) {
			$http.put('/api/v1/manageingrole/'+roleID,role)
				.then(function (response) {
					callback(response.data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.$broadcast("roleInit");
				},function(err) {
					callback(err);
					sweetAlert("Error",err.message,"error");
				});
		}
	};
})
