"use strict";
/*global angular,_*/

var portalApp = angular.module('myBus');

portalApp.factory('userManager', function ($http, $log,$rootScope) {

  var GRP_READ_ONLY = "Read-only"
    , GRP_AUTHOR = "Author"
    , GRP_PUBLISHER = "Publisher"
    , GRP_ADMIN = "Admin"
    , GRP_DEVELOPER = "Developer"
    , GRP_BUSINESS_ADMIN = "Business Admin"
    , currentUser = null
    , currentGroups = null
    , hasRoleReadOnly = null
    , hasRoleAuthor = null
    , hasRolePublisher = null
    , hasRoleAdmin = null
    , hasRoleDeveloper = null
    , hasRoleBusinessAdmin = null;

  var users = {};

  return {

    fetchAllUsers: function () {
      $log.debug("fetching routes data ...");
      $http.get('/api/v1/users')
          .success(function (data) {
            users=data;
            $rootScope.$broadcast('UsersInitComplete');
          })
          .error(function (error) {
            $log.debug("error retrieving users");
          });
    },

      getUsers: function (callback) {
          $log.debug("fetching users data ...");
          $http.get('/api/v1/users')
              .success(function (data) {
                  callback(data);
                  $rootScope.$broadcast('FetchingUsersComplete');
              })
              .error(function (error) {
                  $log.debug("error retrieving cities");
              });
      },

      getAllUsers: function () {
          return users;
      },

    createUser: function(user,callback){
      $http.post('/api/v1/user',user).success(function(data){
        callback(data);
        $rootScope.$broadcast('CreateUserCompleted');
      })
          .error(function (err,status) {
            /*var errorMsg = "error adding new city info. " + (err && err.error ? err.error : '');
             $log.error(errorMsg);
             alert(errorMsg);*/
            sweetAlert("Error",err.message,"error");
          });
    },

    getUserWithId:function(id,callback){
      $http.get("/api/v1/userId/" + id).success(function(data){
        callback(data);
       // $rootScope.$broadcast("UpdateUserCompleted");
      })

    },
    updateUser : function (user,callback) {
      $http.put('/api/v1/userEdit/'+user.id,user).success(function(data){
        callback(data);
        $rootScope.$broadcast('UpdateUserCompleted');
      })
    },

    deleteUser : function (id){
      swal({
            title: "Are you sure?",
            text: "Are you sure you want to delete this route?",
            type: "warning",
            showCancelButton: true,
            closeOnConfirm: false,
            confirmButtonText: "Yes, delete it!",
            confirmButtonColor: "#ec6c62"},function(){

            $http.delete('api/v1/user/'+id).success(function(data){
            $rootScope.$broadcast('DeleteUserCompleted');
            swal("Deleted!", "Route was successfully deleted!", "success");
            }).error(function () {
              swal("Oops", "We couldn't connect to the server!", "error");
            });
      })
    },

    getCurrentUser: function (callback, forceRefresh) {
      if (currentUser === null || forceRefresh) {
        $http.get('/api/v1/user/me')
          .success(function (user) {
            currentUser = user;
            return angular.isFunction(callback) && callback(null, user);
          })
          .error(function (err, status) {
            $log.error('Error getting current user. Status code ' + status + ".  " + angular.toJson(err));
            //angular.isFunction(callback) && callback(err);
            document.location = "/"; // redirect to login
          });
      } else {
        return angular.isFunction(callback) && callback(null, currentUser);
      }
    },
    getUser: function(){
      return currentUser;
    },

    getGroupsForCurrentUser: function (callback, forceRefresh) {
      if (currentGroups === null || forceRefresh) {
        $http.get('/api/v1/user/groups')
          .success(function (groups) {
            currentGroups = groups;
            return angular.isFunction(callback) && callback(null, groups);
          })
          .error(function (err) {
            $log.error('Error getting current user\'s groups. ' + angular.toJson(err));
            return angular.isFunction(callback) && callback(err);
          });
      } else {
        return angular.isFunction(callback) && callback(null, currentGroups);
      }
    },

    isReadOnly: function () {
      if (hasRoleReadOnly === null && currentGroups) {
        hasRoleReadOnly = _.any(currentGroups, function (grp) {
          return GRP_READ_ONLY === grp.name;
        });
      }
      return hasRoleReadOnly;
    },

    isDeveloper: function () {
      if (hasRoleDeveloper === null && currentGroups) {
        hasRoleDeveloper = _.any(currentGroups, function (grp) {
          return GRP_DEVELOPER === grp.name;
        });
      }
      return hasRoleDeveloper;
    },

    isAuthor: function () {
      if (hasRoleAuthor === null && currentGroups) {
        hasRoleAuthor = _.any(currentGroups, function (grp) {
          return GRP_AUTHOR === grp.name;
        });
      }
      return hasRoleAuthor;
    },

    isPublisher: function () {
      if (hasRolePublisher === null && currentGroups) {
        hasRolePublisher = _.any(currentGroups, function (grp) {
          return GRP_PUBLISHER === grp.name;
        });
      }
      return hasRolePublisher;
    },

    isAdmin: function () {
      if (hasRoleAdmin === null && currentGroups) {
        hasRoleAdmin = _.any(currentGroups, function (grp) {
          return GRP_ADMIN === grp.name;
        });
      }
      return hasRoleAdmin;
    },

    isBusinessAdmin: function (businessId) {
      var isBusAdm = false;
      if (hasRoleBusinessAdmin === null && currentGroups) {
        hasRoleBusinessAdmin = _.any(currentGroups, function (grp) {
          return GRP_BUSINESS_ADMIN === grp.name;
        });
      }
      if (hasRoleBusinessAdmin) {
        if (businessId) {
          isBusAdm = currentUser && currentUser.customData && _.contains(currentUser.customData.businessIds, businessId);
        } else {
          isBusAdm = true;
        }
      }
      return isBusAdm;
    },

    canAddPOI: function () {
      return this.isAuthor() || this.isPublisher() || this.isAdmin();
    },

    canEditPOI: function (businessId) {
      return this.isPublisher() || this.isAdmin() || this.isBusinessAdmin(businessId);
    },

    canAddOrEditPOI: function (isAdd, businessId) {
      return isAdd ? this.canAddPOI() : this.canEditPOI(businessId);
    },

    canViewAPIDocs: function () {
      return this.isDeveloper() || this.isAdmin() || this.isPublisher() || this.isAuthor();
    },

    canViewBusinesses: function () {
      return this.isReadOnly() || this.isAuthor() || this.isPublisher() || this.isAdmin() || this.isBusinessAdmin();
    }
  };
});
