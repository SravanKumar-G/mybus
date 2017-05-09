"use strict";
/*global angular, _*/

angular.module('myBus.officeExpensesModule', ['ngTable', 'ui.bootstrap'])
    .controller("OfficeExpensesController",function($rootScope, $scope, $filter, $location, $log,$uibModal, NgTableParams, officeExpensesManager, userManager, branchOfficeManager){
        $scope.loading = false;
        $scope.headline = "Office Expenses";
        $scope.query = {"status":null};
        $scope.user = userManager.getUser();
        $scope.approvedExpenses=[];
        $scope.pendingExpenses=[];
        $scope.pendingTotal = 0;
        $scope.approvedTotal = 0;
        var user = userManager.getUser();
        $scope.currentUser = user.fullName;
        branchOfficeManager.loadNames(function(data) {
            $scope.offices = data;
        });

        $scope.canAddExpense = function() {
            return user.admin || user.branchOfficeId;
        }
        var loadPendingExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            officeExpensesManager.pendingOfficeExpenses(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.pendingExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.pendingTotal = response.totalElements;
                    $scope.count = response.totalElements;
                    tableParams.data = $scope.pendingExpenses;
                }
            });
        };

        var loadApprovedExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            officeExpensesManager.approvedOfficeExpenses(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.approvedExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.approvedTotal = response.totalElements;
                    tableParams.data = $scope.approvedExpenses;
                }
            });
        };
        var loadsearchExpenses = function (tableParams) {
            var sortingProps = tableParams.sorting();
            var sortProps = ""
            for(var prop in sortingProps) {
                sortProps += prop+"," +sortingProps[prop];
            }
            $scope.loading = true;
            var pageable = {page:tableParams.page(), size:tableParams.count(), sort:sortProps};
            officeExpensesManager.searchExpenses(pageable, function(response){
                if(angular.isArray(response.content)) {
                    $scope.loading = false;
                    $scope.searchExpenses = response.content;
                    tableParams.total(response.totalElements);
                    $scope.count = response.totalElements;
                    $scope.searchTotal = response.totalElements;
                    tableParams.data = $scope.searchExpenses;
                }
            });
        };
        $scope.init = function() {
            officeExpensesManager.count(true, function(expensesCount){
                $scope.pendingTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:10,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:expensesCount,
                    getData: function (params) {
                        loadPendingExpenses(params);
                    }
                });
            });
            officeExpensesManager.count(false, function(count){
                $scope.approvedTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:15,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:count,
                    getData: function (params) {
                        loadApprovedExpenses(params);
                    }
                });
            });

        };
        $scope.searchInti = function (){
            officeExpensesManager.count(false, function(count){
                $scope.searchTableParams = new NgTableParams({
                    page: 1, // show first page
                    count:15,
                    sorting: {
                        fullName: 'asc'
                    },
                }, {
                    counts:[],
                    total:count,
                    getData: function (params) {
                        loadsearchExpenses(params);
                    }
                });
            });
        }
        $scope.init();
        $scope.searchFilter = function(){
            $scope.init();
        }
        $scope.handleClickAddExpense = function() {
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-expense-modal.html',
                controller : 'EditExpenseController',
                resolve : {
                    expenseId : function(){
                        return null;
                    }
                }
            });
        };

        $rootScope.$on('UpdateHeader',function (e,value) {
            $scope.init();
        });

        $scope.handleClickUpdateExpense = function(expenseId){
            $rootScope.modalInstance = $uibModal.open({
                templateUrl : 'add-expense-modal.html',
                controller : 'EditExpenseController',
                resolve : {
                    expenseId : function(){
                        return expenseId;
                    }
                }
            });
        };
        $scope.delete = function(expenseId) {
            officeExpensesManager.delete(expenseId, function(data){
                $scope.init();
            });
        };

        $scope.approveOrRejectExpense = function(expense, status){
            if(status == "Reject"){
                expense.status ="Rejected";
            } else {
                expense.status ="Approved";
            }
            officeExpensesManager.save(expense, function(data){
                $rootScope.$broadcast('UpdateHeader');
                swal("Great", "Expense is updated", "success");
            });
        }


        /* date picker functions */
        $scope.check = true;
        $scope.tomorrow;
        $scope.OfficeSelect = null;
        $scope.today = function() {
            $scope.dt = new Date();
            $scope.dt2 = new Date();
            $scope.tomorrow = new Date($scope.dt.getTime() + (24 * 60 * 60 * 1000));
        };

        $scope.today();
        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
        $scope.altInputFormats = ['M!/d!/yyyy'];

        $scope.clear = function() {
            $scope.dt = null;
            $scope.dt2 = null;
        };

        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function() {
            if($scope.dt >= $scope.tomorrow || $scope.dt2 >= $scope.tomorrow){
                swal("Oops...", "U've checked for future, Check Later", "error");
                $scope.check = false;
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
            console.log(check);
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


        $scope.search = function(){
            console.log($scope.dt);
            console.log($scope.dt2);
            console.log($scope.OfficeSelect);
            $scope.searchInti();

        }


    })

    .controller("EditExpenseController",function($rootScope, $scope, $uibModal, $location,$log,NgTableParams,officeExpensesManager, userManager, branchOfficeManager,expenseId) {
        $scope.today = function () {
            $scope.dt = new Date();
        };

        $scope.checkType = function(check){
            if (check == true ){
                $scope.type = true;
            }
            else{
                $scope.type = false;
            }

        }
        $scope.user = userManager.getUser();

        $scope.expense = {'branchOfficeId': $scope.user.branchOfficeId};
        $scope.today();


        branchOfficeManager.loadNames(function (data) {
            $scope.offices = data;
        });

        $scope.cancel = function () {
            $rootScope.modalInstance.dismiss('cancel');
        };


        if (expenseId) {
            $scope.setExpenseIntoModal = function (expenseId) {
                officeExpensesManager.getExpenseById(expenseId, function (data) {
                    $scope.expense = data;
                });
            };
            $scope.setExpenseIntoModal(expenseId);
        }

        $scope.add = function () {
            if (expenseId) {
                if ($scope.addNewExpenseForm.$invalid) {
                    swal("Error!", "Please fix the errors in the form", "error");
                    return;
                }
                officeExpensesManager.save($scope.expense, function (data) {
                    swal("Great", "Saved successfully", "success");
                });
            }
            else {
                $scope.expense.date = $scope.dt;
                officeExpensesManager.save($scope.expense, function (data) {
                    swal("Great", "Saved successfully", "success");
                    $location.url('/officeexpenses');
                });
            }
        }


        $scope.inlineOptions = {
            customClass: getDayClass,
            minDate: new Date(),
            showWeeks: true
        };
        $scope.dateChanged = function () {

        }
        $scope.dateOptions = {
            formatYear: 'yy',
            minDate: new Date(),
            startingDay: 1
        };
        // Disable weekend selection
        function disabled(data) {
            var date = data.date,
                mode = data.mode;
            return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
        }

        $scope.toggleMin = function () {
            $scope.inlineOptions.minDate = $scope.inlineOptions.minDate ? null : new Date();
            $scope.dateOptions.minDate = $scope.inlineOptions.minDate;
        };
        $scope.toggleMin();
        $scope.open1 = function () {
            $scope.popup1.opened = true;
        };
        $scope.setDate = function (year, month, day) {
            $scope.dt = new Date(year, month, day);
        };
        $scope.popup1 = {
            opened: false
        };
        function getDayClass(data) {
            var date = data.date,
                mode = data.mode;
            if (mode === 'day') {
                var dayToCheck = new Date(date).setHours(0, 0, 0, 0);
                for (var i = 0; i < $scope.events.length; i++) {
                    var currentDay = new Date($scope.events[i].date).setHours(0, 0, 0, 0);

                    if (dayToCheck === currentDay) {
                        return $scope.events[i].status;
                    }
                }
            }
        };
    }).factory('officeExpensesManager', function ($rootScope, $http, $log) {
        return {
            pendingOfficeExpenses: function (pageable, callback) {
                $http({url:'/api/v1/officeExpenses/pending',method:"GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving expenses");
                    });
            },
            approvedOfficeExpenses: function (pageable, callback) {
                $http({url:'/api/v1/officeExpenses/approved',method:"GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving expenses");
                    });
            },
            searchExpenses: function(pageable, callback){
                $http({url:'/api/v1/officeExpenses/approved',method:"GET", params: pageable})
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving expenses");
                    });
            },

            count: function (pending, callback) {
                $http.get('/api/v1/officeExpenses/count?pending='+pending)
                    .then(function (response) {
                        callback(response.data);
                    }, function (error) {
                        $log.debug("error retrieving expenses count");
                    });
            },
            delete: function (expenseId, callback) {
                swal({
                    title: "Are you sure?",
                    text: "Are you sure you want to delete this expense?",
                    type: "warning",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    confirmButtonText: "Yes, delete it!",
                    confirmButtonColor: "#ec6c62"},function(){

                    $http.delete('/api/v1/officeExpense/' + expenseId)
                        .then(function (response) {
                            callback(response.data);
                            swal("Great", "Saved Deleted", "success");
                        }, function (error) {
                            $log.debug("error deleting expense");
                            sweetAlert("Error",err.message,"error");
                        });
                })

            },
            getExpenseById: function (id,callback) {
                $log.debug("fetching expense data ...");
                $http.get('/api/v1/officeExpense/'+id)
                    .then(function (response) {
                        callback(response.data);
                    },function (err,status) {
                        sweetAlert("Error",err.message,"error");
                    });
            },
            save: function (officeExpense, callback) {
                if (!officeExpense.id) {
                    $http.post('/api/v1/officeExpense/', officeExpense).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    });
                } else {
                    $http.put('/api/v1/officeExpense/', officeExpense).then(function (response) {
                        if (angular.isFunction(callback)) {
                            callback(response.data);
                            $rootScope.$broadcast('UpdateHeader');
                            swal("Great", "Saved successfully", "success");
                            $rootScope.modalInstance.dismiss('success');
                        }
                    }, function (err, status) {
                        sweetAlert("Error", err.data.message, "error");
                    });
                }
            }
        }
    });