'use strict';
/*global angular, _*/

angular.module('myBus.homeModule', ['ngTable', 'ui.bootstrap'])
  .controller('HomeController', function($scope, $http, $log, $modal, $filter, $location) {
    $scope.headline = "Srikrishna Travels - Admin Portal";
  });