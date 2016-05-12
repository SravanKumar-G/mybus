/**
 * Created by svanik on 1/20/2016.
 */

var portalApp = angular.module('myBus');

portalApp.factory('rolesManager', function ($rootScope, $http, $log, $window) {

    var roles = {};

    return{
        fetchAllRoles: function () {
            $log.debug("fetching roles data ...");
            $http.get('/api/v1/roles')
                .success(function (data) {
                    roles = data;
                    $rootScope.$broadcast('RolesInitComplete');
                })
                .error(function (error) {
                    $log.debug("error retrieving roles");
                });
        },
        getRole: function(roleId,callback,errorcallback){
             $http.get('/api/v1/role/'+roleId).success(function(data){
                 callback(data);
             })
             .error(function (error) {
                  errorcallback(error);
                  $log.debug("error retrieving cities");
             });
        },

        getAllRoles: function () {
            return roles;
        },

        createRole: function(route,callback,errorcallback){
            $http.post('/api/v1/role',role).success(function(data){
                callback(data);
                $rootScope.$broadcast('CreateRoleCompleted');
            })
            .error(function (err,status) {
                sweetAlert("Error",err.message,"error");
            });
        },

        deleteRole: function(roleId) {
            swal({
                title: "Are you sure?",
                text: "Are you sure you want to delete this Role?",
                type: "warning",
                showCancelButton: true,
                closeOnConfirm: false,
                confirmButtonText: "Yes, delete it!",
                confirmButtonColor: "#ec6c62"},function(){

                $http.delete('/api/v1/route/' + roleId).success(function (data) {
                    $rootScope.$broadcast('DeleteRoleCompleted');
                    swal("Deleted!", "Role was successfully deleted!", "success");
                    }).error(function (error) {
                       swal("Oops", "We couldn't connect to the server!", "error");
                    });
            })
        },

        updateRole: function(role,callback){
            $http.put('/api/v1/route/'+role.id,role).success(function(data){
                $rootScope.$broadcast('UpdateRouteCompleted');
            })
            .error(function(){
                alert("Error Updating Route");
            });
        }

    }

});