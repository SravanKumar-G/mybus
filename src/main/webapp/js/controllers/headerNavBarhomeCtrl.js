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
        $scope.isAdmin = function() {
            var user = userManager.getUser();
            return user.admin;
        }
        $scope.updateHeader = function(){
            if($scope.user && $scope.user.branchOfficeId) {
                branchOfficeManager.load($scope.user.branchOfficeId, function(data){
                    $scope.branchOffice = data;
                });
            }
        };
        $scope.$watch('user', function(){
            $scope.updateHeader();
        });
        $scope.$on('UpdateHeader', function(){
            $scope.updateHeader();
        });
    });

