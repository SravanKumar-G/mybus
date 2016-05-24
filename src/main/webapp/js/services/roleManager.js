/**
 * Created by yks_Srinivas.
 */

var portalApp = angular.module('myBus');

portalApp.factory('roleManager', function ($rootScope, $http,$filter) {
	var roles=[];
	return {
		fechAllRoles : function(){
			$http.get('/api/v1/roles')
				.success(function (data) {
					roles = data;
					$rootScope.$broadcast("roleInitComplete");
				}).error(function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		getRoles : function(){
			return roles;
		},
		getAllRoles : function(callback){
			$http.get('/api/v1/roles')
				.success(function (data) {
					callback(data);
				}).error(function(err,status) {
					sweetAlert("Error",err.message,"error");
				});
		},
		createRole : function (role,callback) {
			role.name = $filter('uppercase')(role.name);
			$http.post('/api/v1/createRole',role)
				.success(function (data) {
					callback(data);
					$rootScope.$broadcast("roleInit");
					swal("Great", "Role has been successfully added", "success");
				}).error(function(err) {
					callback(err);
					sweetAlert("Error",err.message,"error");
				});
		},
		updateRole : function (roleID,role,callback) {
			role.name = $filter('uppercase')(role.name);
			$http.put('/api/v1/role/'+roleID,role)
				.success(function (data) {
					callback(data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.$broadcast("roleInit");
				}).error(function(err) {
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
					.success(function (data) {
						swal("Great", "Roles has been updated successfully", "success");
						$rootScope.$broadcast("roleInit");
						callback(data);
					}).error(function(err) {
						callback(err);
						sweetAlert("Error",err.message,"error");
					});
			})

		},
		getRoleById : function (roleID,callback) {
			$http.get('/api/v1/role/'+roleID)
				.success(function (data) {
					callback(data);
				}).error(function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		getRoleByRoleName : function (rolesName,callback) {
			$http.get('/api/v1/roleByName/'+rolesName)
				.success(function (data) {
					callback(data);
				}).error(function(err) {
					sweetAlert("Error",err.message,"error");
				});
		},
		updateManageingRole : function (roleID,role,callback) {
			$http.put('/api/v1/manageingrole/'+roleID,role)
				.success(function (data) {
					callback(data);
					swal("Great", "Roles has been updated successfully", "success");
					$rootScope.$broadcast("roleInit");
				}).error(function(err) {
					callback(err);
					sweetAlert("Error",err.message,"error");
				});
		}
	};
})
