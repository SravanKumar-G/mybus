"use strict";
/*global angular, _*/

angular.module('myBus.userModule', ['ngTable', 'ui.bootstrap'])

  //
  // ============================= List All ===================================
  //
    .controller('UsersController', function($scope,$state, $http, $log, $filter, ngTableParams, $location, usSpinnerService,userManager) {
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
      $scope.userContentTableParams = new ngTableParams({
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
    .controller('UserAddController', function($scope,userManager,$window,$log,agentPlanManager, roleManager,cancelManager ) {
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
        $log.debug("roles"+$scope.roles);
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

        $scope.saveButtonClicked = function(){
            userManager.createUser($scope.user,function(data){
                swal("success","User successfully added","success");
                $location.url('/users');
            });


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

        $scope.updateUser = function(){
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

    });




