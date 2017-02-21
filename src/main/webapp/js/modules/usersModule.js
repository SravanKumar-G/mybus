"use strict";
/*global angular, _*/

angular.module('myBus.userModule', ['ngTable', 'ui.bootstrap'])

  //
  // ============================= List All ===================================
  //
    .controller('UsersController', function($scope,$state, $http, $log, $filter, NgTableParams, $location, usSpinnerService,userManager) {
      $scope.headline = "Users";
      //$scope.users = [];
      $scope.userCount = 0;

      $scope.startSpin = function(){
        usSpinnerService.spin('spinner-1');
      };
      $scope.stopSpin = function(){
        usSpinnerService.stop('spinner-1');
      };

      $scope.currentPageOfUsers = [];
      var loadTableData = function (tableParams, $defer) {
          userManager.getUsers(function (data) {
              $scope.users = data;
              $scope.userCount = data.length;
              var orderedData = tableParams.sorting() ? $filter('orderBy')(data, tableParams.orderBy()) : data;
              $scope.allUsers = orderedData;
              tableParams.total(data.length);
              if (angular.isDefined($defer)) {
                  $defer.resolve(orderedData);
              }
              $scope.currentPageOfUsers = orderedData.slice((tableParams.page() - 1) * tableParams.count(), tableParams.page() * tableParams.count());
          });
      };
      $scope.userContentTableParams = new NgTableParams({
        page: 1,
        count: 50,
        sorting: {
          state: 'asc',
          name: 'asc'
        }
      }, {
        total: $scope.currentPageOfUsers.length,
        getData: function ($defer, params) {
          $scope.$on('UsersInitComplete', function (e, value) {
            loadTableData(params);
          });
        }
      });

    userManager.fetchAllUsers();

    $scope.$on('CreateUserCompleted',function(e,value){
        userManager.fetchAllUsers();
    });

    $scope.$on('DeleteUserCompleted',function(e,value){
        userManager.fetchAllUsers();
    });

    $scope.addUser = function () {
        $state.go('user');
    };

    $scope.editUser = function(userId){
        $location.url('user/'+userId,{'idParam':userId});
    };
    $scope.deleteUser = function(id){
        userManager.deleteUser(id)
    };

    $scope.isAdmin = function(){
        return userManager.getUser().admin;
    };
    })

    //
    // ============================= Add ========================================
    //
    .controller('UserAddController', function($scope,userManager,$window,$log, $location,agentPlanManager, roleManager,cancelManager ) {
        $scope.headline = "Add New User";
        //$scope.isAdd = false;
        $scope.ConfirmPassword = "";
        $scope.user = {};
        $scope.planTypes = [];
        $scope.roles =[];
        $scope.rolesInit = function(){
        	roleManager.getAllRoles(function(data){
        		$scope.roles = data;
        	});
        }
        $scope.rolesInit();
        $scope.usersFromManager=[];
        $scope.onMouseLeave = function(userNameFromUI){
            userManager.getUsers(function(data){
                $scope.usersFromManager=data;
            });
            angular.forEach($scope.usersFromManager,function(user){
                if(user.userName==userNameFromUI){
                    swal("oops!","Username already exist","error");
                }
            });
        };

        $scope.loadFromPlanType = function(){
            agentPlanManager.getPlans(function (data) {
                $scope.planTypes = data;
            });
        };
        $scope.loadFromPlanType();

        $scope.callBlurFunction = function(userPassword){
            $scope.user.password = userPassword;
        };

        $scope.passwordCheck = function(gotPassword){
            if (gotPassword != $scope.user.password) {
                swal("oops!", "Password should match", "error");
            }
        };

        $scope.save = function(){
            if($scope.userForm.$dirty) {
                $scope.userForm.submitted = true;
                if ($scope.userForm.$invalid) {
                    swal("Error!", "Please fix the errors in the user form", "error");
                    return;
                }
                userManager.createUser($scope.user, function (data) {
                    swal("success", "User successfully added", "success");
                });
            }
            $location.url('/users');
        };

        $scope.cancelUser = function(theForm){
            cancelManager.cancel(theForm);
        }
    })

    //
  // ======================== Edit User =====================================
  //
  .controller('UpdateUserController', function ($scope,$stateParams, $location, $http, $log, $modal,userManager,$window,roleManager,cancelManager) {
        $scope.headline = "Edit User";
        $scope.id=$stateParams.id;
        $scope.user={};
        $scope.roles =[];
        $scope.rolesInit = function(){
        	roleManager.getAllRoles(function(data){
        		$scope.roles = data;
        	});
        }
        $scope.rolesInit();
        $scope.loadUserWithId = function(){
            userManager.getUserWithId($scope.id,function(data){
                $scope.user=data;
                $scope.confirmPassword = $scope.user.password;
            })
        };
        $scope.loadUserWithId();

        $scope.save = function(){
            if($scope.userForm.$invalid) {
                swal("Error!","Please fix the errors in the user form","error");
                return;
            }
            if(userManager.validateUserInfo($scope.user, $scope.confirmPassword)) {
                userManager.updateUser($scope.user,function(data){
                    swal("success","User Updated","success");
                    $location.url('/users');
                },function(error) {
                    swal(error.message,"Error saving the user form","error");
                    return;
                });
            } else {
                swal("Error!","Please fix the errors in the user form","error");
                return;
            }
        };
        $scope.cancelUser = function(theForm){
            cancelManager.cancel(theForm);
        }

    }).factory('userManager', function ($http, $log,$rootScope) {

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

            validateUserInfo: function(user, confirmPassword) {
                if(user.password !== confirmPassword) {
                    return false;
                }
                return true;
            },
            fetchAllUsers: function () {
                $log.debug("fetching routes data ...");
                $http.get('/api/v1/users')
                    .then(function (data) {
                        users=data;
                        $rootScope.$broadcast('UsersInitComplete');
                    },function (error) {
                        $log.debug("error retrieving users");
                    });
            },

            getUsers: function (callback) {
                $http.get('/api/v1/users')
                    .then(function (data) {
                        callback(data);
                        $rootScope.$broadcast('FetchingUsersComplete');
                    },function (error) {
                        $log.debug("error retrieving cities");
                    });
            },
            getUserNames: function (callback) {
                $http.get('/api/v1/userNames')
                    .then(function (data) {
                        callback(data);
                        $rootScope.$broadcast('FetchingUserNamesComplete');
                    },function (error) {
                        $log.debug("error retrieving user names");
                    });
            },


            getAllUsers: function () {
                return users;
            },

            createUser: function(user,callback){
                $http.post('/api/v1/user',user).then(function(data){
                    callback(data);
                    $rootScope.$broadcast('CreateUserCompleted');
                },function (err,status) {
                    sweetAlert("Error",err.message,"error");
                });
            },

            getUserWithId:function(id,callback){
                $http.get("/api/v1/userId/" + id).then(function(data){
                    callback(data);
                })
            },
            updateUser : function (user,callback,errorcallback) {
                $http.put('/api/v1/userEdit/'+user.id,user).then(function(data){
                    callback(data);
                    $rootScope.$broadcast('UpdateUserCompleted');
                },function (data, status, header, config) {
                    errorcallback(data);
                });
            },

            deleteUser : function (id){
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this user?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){

                    $http.delete('api/v1/user/'+id).then(function(data){
                        $rootScope.$broadcast('DeleteUserCompleted');
                        swal("Deleted!", "Route was successfully deleted!", "success");
                    },function () {
                        swal("Oops", "We couldn't connect to the server!", "error");
                    });
                })
            },

            getCurrentUser: function (callback, forceRefresh) {
                if (currentUser === null || forceRefresh) {
                    $http.get('/api/v1/user/me')
                        .then(function (response) {
                            currentUser = response.data;
                            return angular.isFunction(callback) && callback(null, currentUser);
                        },function (err, status) {
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
                        .then(function (groups) {
                            currentGroups = groups;
                            return angular.isFunction(callback) && callback(null, groups);
                        },function (err) {
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




