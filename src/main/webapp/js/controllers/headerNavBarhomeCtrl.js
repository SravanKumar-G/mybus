/**
 * Created by svanik on 2/22/2016.
 */
"use strict";
/*global angular, _*/

angular.module('myBus.header', ['ngTable','ui.bootstrap'])

    //
    // ============================= List All ===================================
    //
    .controller('headerNavBarhomeCtrl', function($scope, userManager) {
        console.log("in headerNavBarhomeCtrl");
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
            var user = userManager.getUser();
            if(user != null) {
                return user.firstName+" ,"+ user.lastName;
            } else {
                return null;
            }

        }
    });

