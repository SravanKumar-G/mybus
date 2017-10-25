"use strict";
/*global angular, _*/

angular.module('myBus.invoiceModule', ['ngTable', 'ui.bootstrap'])
    .controller("InvoiceController",function($rootScope, $scope, $filter, $location,paginationService, $log,$uibModal, NgTableParams, userManager, invoiceManager){
        $scope.invoice = {};
        /* date picker functions */
        $scope.check = true;
        $scope.today = function() {
            $scope.dt = new Date();
            $scope.dt2 = new Date();
        };
        $scope.today();
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.clear = function() {
            $scope.dt = new Date();
            $scope.dt2 = new Date();
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };

        $scope.toggleMin = function() {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function(year, month, day) {
            $scope.dt = new Date(year, month, day);
            $scope.dt2 = new Date(year, month, day);
        };
        $scope.monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        $scope.popup1= {
            opened: false
        };

        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0,0,0,0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
            return '';
        }

        /*Date picker 2 */
        $scope.open2 = function() {
            $scope.popup2.opened = true;
        };

        $scope.popup2 = {
            opened: false
        };

        $scope.searchInit = function (){
            $scope.searchTableParams = new NgTableParams({
                page: 1, // show first page
                count:15,
                sorting: {
                    date: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    $scope.loading = true;
                    invoiceManager.search($scope.query,function(invoice) {
                        $scope.loading = false;
                        $scope.invoice = invoice;
                    });
                }
            });
        }


        $scope.search = function(){
            $scope.query = {
                "startDate" : $scope.dt.getFullYear()+"-"+[$scope.dt.getMonth()+1]+"-"+$scope.dt.getDate(),
                "endDate" :  $scope.dt2.getFullYear()+"-"+[$scope.dt2.getMonth()+1]+"-"+$scope.dt2.getDate(),
                "channel" :$scope.channel
            }
            $scope.searchInit();
        }

    })
    .factory('invoiceManager', function ($rootScope, $q, $http, $log) {
        var cashTransfer = {};
        return {
            search: function(query, callback){
                $http.post('/api/v1/serviceForm/bookings/search', query).then(function (response) {
                    if (angular.isFunction(callback)) {
                        callback(response.data);
                    }
                }, function (err, status) {
                    sweetAlert("Error searching bookings", err.message, "error");
                });
            },
        };
    });

