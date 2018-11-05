"use strict";
/*global angular, _*/

angular.module('myBus.operatorAccountsModule', ['ngTable', 'ui.bootstrap'])
.controller("OperatorAccountsController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,opratingAccountsManager){
	$scope.headline = "OperatorAccounts";
    $scope.loading = false;
    $scope.accounts = [];

    $scope.count = 0;
    var loadTableData = function (tableParams) {
		$scope.loading = true;
        opratingAccountsManager.getAccounts( function(response){
			if(angular.isArray(response)){
				$scope.loading = false;
				$scope.accounts = response;
                $scope.count = $scope.accounts.length;
            }
		})
	 };

	 $scope.init = function(){
             $scope.accountTableParams = new NgTableParams(
                 {
                     page: 1,
                     size: 20,
                     count: 10,
                     sorting: {
                         name: 'asc'
                     }
                 },
                 {
                 	counts:[],
                     getData: function (params) {
                         loadTableData(params);
                     }
                 }
             );
	 };
	 $scope.init();

	$scope.$on('reloadAccounts', function (e, value) {
        $scope.init();
	});

	$scope.addAccount = function () {
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl: 'edit-account-modal.html',
	        controller: 'EditAccountController',
            resolve : {
                accountId : function(){
                    return null;
                }
            }
	    })
	};

	$scope.editAccount = function(accountId){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'edit-account-modal.html',
	        controller : 'EditAccountController',
	        resolve : {
                accountId : function(){
	                return accountId;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditAccountController', function ($scope, $rootScope, $uibModal, $http, accountId, opratingAccountsManager) {
	$scope.account = {};
	$scope.saveAccount =function(){
        opratingAccountsManager.saveAccount($scope.account,function(data){
			$scope.account = data;
            $rootScope.$broadcast('reloadAccounts');
			$rootScope.modalInstance.close(data);
		});
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };

    if(accountId){
        opratingAccountsManager.getAccount(accountId,function(data){
            $scope.account = data;
        });
	}
})
.factory("opratingAccountsManager",function($rootScope,$http){
	var accounts = [];
	return {
        getAccounts: function (callback) {
            $http.get("/api/v1/operatorAccount/all").then(function (response) {
                accounts = response.data;
                callback(accounts);
            }, function (error) {
                swal("oops", error, "error");
            });
        },
        saveAccount: function (account, callback) {
            $http.post("/api/v1/operatorAccount/", account).then(function (response) {
                callback(response.data);
                $rootScope.$broadcast('reloadAccounts');
                swal("Great", "Account has been successfully added", "success");
            }, function (error) {
                swal("oops", error, "error");
            })
        },
        getAccount: function (id, callback) {
            $http.get("/api/v1/operatorAccount/" + id).then(function (response) {
                callback(response.data);
            }, function (error) {
                swal("oops", error, "error");
            })
        }
    }
});