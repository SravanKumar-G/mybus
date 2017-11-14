"use strict";
/*global angular, _*/

angular.module('myBus.fillingStations', ['ngTable', 'ui.bootstrap'])
.controller("FillingStationsController",function($rootScope, $scope, $uibModal, $filter, $log,NgTableParams,paginationService, fillingStationsManager){
	$scope.headline = "FillingStations";
	$scope.fillingStations = [];
	var loadTableData = function (tableParams) {
		$scope.loading = true;
        fillingStationsManager.getFillingStations(function(response){
			if(angular.isArray(response)){
				$scope.loading = false;
				$scope.fillingStations = response;
			}
		})
	 };

    $scope.init = function() {
        $scope.tableParams = new NgTableParams({
            sorting: {
                name: 'asc'
            }
        }, {
            counts: [],
            getData: function (params) {
                loadTableData(params);
            }
        });
    };

    $scope.init();
    $scope.$on('reloadFillingStations', function (e, value) {
        loadTableData();
    });
	$scope.handleEdit = function(id){
		$rootScope.modalInstance = $uibModal.open({
	        templateUrl : 'edit-fillingstation-modal.html',
	        controller : 'EditFillingStationController',
	        resolve : {
                fillingStationId : function(){
	                return id;
	            }
	        }
	    });
	};
})
// ========================== Modal - Update Amenity  =================================

.controller('EditFillingStationController', function ($scope, $rootScope, $uibModal, $http, $log, fillingStationsManager, fillingStationId) {
	$log.debug("in EditFillingStationController");
    $scope.fillingStation = {};
	$scope.save =function(){
		if(fillingStationId){
            fillingStationsManager.updateFillingStation($scope.fillingStation, function(data){
                $scope.fillingStation = data;
                $rootScope.modalInstance.close(data);
            });
		} else {
            fillingStationsManager.addFillingStation($scope.fillingStation, function(data){
                $scope.fillingStation = data;
                $rootScope.modalInstance.close(data);
            });
		}
	};
    $scope.cancel = function () {
		$rootScope.modalInstance.dismiss('cancel');
    };
	if(fillingStationId) {
        fillingStationsManager.getFillingStation(fillingStationId,function(data){
            $scope.fillingStation = data;
        });
	}
    $scope.reset = function(){
		$scope.fillingStation= {};
	};
}).factory("fillingStationsManager",function($rootScope,$http,$log){
	return {

        getFillingStations: function (callback) {
            $http.get("api/v1/fillingStations/").then(function (response) {
                callback(response.data);
            }, function (error) {
                swal("oops", error, "error");
            });
        },

		addFillingStation: function(fillingStation,callback) {
			$http.post("/api/v1/fillingStations/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "FillingStation has been successfully added", "success");
			},function(error){
				swal("oops", error, "error");
			})
		},

		getFillingStation : function(id,callback){
			$http.get("/api/v1/fillingStations/"+id).then(function(response){
				callback(response.data);
			},function(error){
				swal("oops", error, "error");
			})
		},

		updateFillingStation : function(fillingStation,callback){
			$http.put("/api/v1/fillingStations/",fillingStation).then(function(response){
				callback(response.data);
				$rootScope.$broadcast('reloadFillingStations');
				swal("Great", "FillingStation has been updated successfully", "success");
			},function(error){
				swal("oops", error, "error");
			})
		}
	}
});