/**
 * Created by svanik on 2/22/2016.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.header', ['ngTable','ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('headerNavBarhomeCtrl', function($scope, userManager, branchOfficeManager) {
        $scope.branchOffice = {};
        $scope.user = {};
        $scope.currentDate = function(){
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1; //January is 0!

            var yyyy = today.getFullYear();
            if(dd<10){
                dd='0'+dd;
            }
            if(mm<10){
                mm='0'+mm;
            }
            var today = dd+'/'+mm+'/'+yyyy;
            return today;
        };

        $scope.userName = function() {
            $scope.user= userManager.getUser();
            if($scope.user != null) {
                return $scope.user.firstName+" ,"+ $scope.user.lastName;
            } else {
                return null;
            }
        }
        $scope.amountToBePaid = function() {
            var user = userManager.getUser();
            return user? user.amountToBePaid: "";
        }
        $scope.amountToBeCollected = function() {
            var user = userManager.getUser();
            return user? user.amountToBeCollected: "";
        }

        $scope.isAdmin = function() {
            var user = userManager.getUser();
            if(user != null) {
                return user.admin;
            } else {
                return false;
            }
        }
        $scope.updateHeader = function(){

            if($scope.user && $scope.user.branchOfficeId) {
                /*branchOfficeManager.load($scope.user.branchOfficeId, function(data){
                    $scope.branchOffice = data;
                });*/
                console.log('updating header');
                userManager.getCurrentUser(null, true);
            }
        };
        $scope.$on('UpdateHeader', function(){
            $scope.updateHeader();
        });
    }).controller('MenuBarController', function($scope,$rootScope, $location, $stateParams,userManager ) {
        $scope.currentUser = null;
        $scope.$on('currentuserLoaded', function(){
            $scope.currentUser = $rootScope.currentuser;
            if($scope.currentUser.accessibleModules){
                var accessibleModules = $scope.currentUser.accessibleModules;
                var allModules = $scope.currentUser.attributes.allModules.split(",");
                for(var a=0;a<allModules.length;a++){
                    if(accessibleModules.indexOf(allModules[a]) > -1){
                        $scope.currentUser['canAccess'+allModules[a]]  = true;
                    }
                }
            }
            console.log($scope.currentUser);
        });
    });

