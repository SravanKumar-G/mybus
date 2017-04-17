"use strict";
/*global angular, _*/

angular.module('myBus.expensesIncomesReportsModule', ['ngTable', 'ui.bootstrap'])
    .controller('expensesIncomesReportsCtrl', function ($scope,$rootScope,NgTableParams,$uibModal, $filter, $location ,userManager,paymentManager,branchOfficeManager,paymentsReportsManager) {
        $scope.payments = [];
        $scope.totalExpense = 0;
        $scope.totalIncome = 0;
        $scope.loading = false;
        $scope.user = userManager.getUser();
        $scope.currentPageOfPayments=[];
        $scope.parseDate = function(){
            $scope.date = $scope.dt.getFullYear()+"-"+('0' + (parseInt($scope.dt.getUTCMonth()+1))).slice(-2)+"-"+('0' + $scope.dt.getDate()).slice(-2);
        }
        $scope.today = function() {
            //var date =
            //date.setDate(date.getDate()-1);
            $scope.dt = new Date();
            $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
            $scope.parseDate();
        };

        $scope.today();
        $scope.date = null;
        $scope.downloadedOn = null;
        $scope.downloaded = false;
        $scope.loading = false;
        $scope.currentPageOfReports = [];
        $scope.submitted = 0;
        $scope.verified = 0;
        $scope.loading = true;
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.clear = function() {
            $scope.dt = null;
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function() {
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else{
                $scope.init();
            }
        }
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
        };
        $scope.popup1 = {
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

        $scope.isAdmin = function() {
            var currentUser = userManager.getUser();
            return currentUser.admin;
        }
        $scope.monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];

        $scope.nextPaymentDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() + 24 * 60 * 60 * 1000);
            $scope.dt.setTime(dt.getTime());
            $scope.init();
        }
        $scope.previousPaymentDay = function() {
            var dt = $scope.dt;
            dt.setTime(dt.getTime() - 24 * 60 * 60 * 1000);
            $scope.dt = dt;
            if($scope.dt >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
            }
            else{
                $scope.init();
            }
        }

        var loadTableData = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            var dateObj = $scope.dt;
            var month = dateObj.getMonth() + 1;
            var day = dateObj.getDate();
            var year = dateObj.getFullYear();
            var newdate = year + "/" + month + "/" + day;
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            paymentsReportsManager.getPayments(newdate,pageable, function(response) {
                if (angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.payments = response.content;
                    console.log(response.content);
                    branchOfficeManager.loadNames(function (data) {
                        $scope.branches = data;
                        angular.forEach($scope.payments, function (payment) {
                            if (payment.type=='EXPENSE'){
                                $scope.totalExpense = $scope.totalExpense + payment.amount;
                            }
                            else if(payment.type=='INCOME') {
                                $scope.totalIncome = $scope.totalIncome + payment.amount;
                            }
                            else{
                                console.log('error')
                            }
                            angular.forEach($scope.branches, function (branchOffice) {
                                if (branchOffice.id == payment.branchOfficeId) {
                                    payment.attributes.branchOfficeId = branchOffice.name;
                                }
                            })
                        })
                    });
                }
                tableParams.total(response.totalElements);
                $scope.count = response.totalElements;
                tableParams.data = $scope.payments;
                $scope.currentPageOfPayments = $scope.payments;
            });
        };

        $scope.init = function() {
            $scope.totalExpense = 0;
            $scope.totalIncome = 0;
            $scope.paymentTableParams = new NgTableParams({
                page: 1,
                count:999999,
                sorting: {
                    status: 'asc'
                },
            }, {
                counts:[],
                getData: function (params) {
                    loadTableData(params);
                }
            });
        };
        $scope.init();

        $scope.serviceReportsPopUp = function (formId) {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'serviceReportPopUp-form-modal.html',
                controller:'serviceReportsPopUpController',
                resolve : {
                    formId : function(){
                        return formId;
                    }
                }
            })
        }

        $scope.BookingDuePopUpExpenses = function(bookingId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'booking-popup-modal.html',
                controller : 'popUpBookingControllerExpenses',
                resolve : {
                    bookingId : function(){
                        return bookingId;
                    }
                }
            });
        };
    })
    .controller("popUpBookingControllerExpenses", function($scope,$rootScope, serviceReportsManager , bookingId){
        $scope.booking = {};
        serviceReportsManager.getBooking(bookingId,function (data) {
            $scope.booking = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .controller("serviceReportsPopUpController", function($scope,$rootScope, serviceReportsManager , formId){
        $scope.service = {};
        serviceReportsManager.getForm(formId,function (data) {
            $scope.service = data;
        })

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };
    })
    .factory('paymentsReportsManager', function ($http, $log) {
        return {
            getPayments: function (date,pageable, callback) {
            $http({url:'/api/v1/payments/day?date=' + date,method:"GET",params:pageable})
                .then(function (response) {
                    callback(response.data);
                }, function (error) {
                    $log.debug("error loading payments");
                });
            }
        }
    });