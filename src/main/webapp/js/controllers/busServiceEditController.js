"use strict";
/*global angular, _*/

angular.module('myBus.serviceEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusServiceEditController   ================================================
  // ==================================================================================================================

  .controller('BusServiceEditController', function ($rootScope, $scope, $http, $log, ngTableParams, $modal, $filter, busServiceManager, $routeParams, $location, $cacheFactory) {
        $log.debug('BusServiceController loading');
        var busServiceEditCtrl = this;

        busServiceEditCtrl.valid = false;

        busServiceEditCtrl.totalSeats = 0;

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Service Details";

        busServiceEditCtrl.busService = {
            rows : null,
            type: null,
            name : null,
            upper : null,
            lower : null,
            upperHeader : '',
            lowerHeader : ''
        };

      var serviceId = $routeParams.id;

      if(serviceId !== ''){
        var cache = $cacheFactory.get($rootScope.id);
        if(cache){
            busServiceEditCtrl.busService = cache.get(serviceId);
        }
        if(busServiceEditCtrl.busService && busServiceEditCtrl.busService.id !== ''){
        }else{
            busServiceEditCtrl.busService = {
                rows : null,
                type: null,
                name : null,
                upper : null,
                lower : null,
                upperHeader : '',
                lowerHeader : ''
            }
        }
      }

        function initialize(){
        	busServiceEditCtrl.busService.name = null;
            busServiceEditCtrl.busService.type = null;
            busServiceEditCtrl.busService.rows = null;
            busServiceEditCtrl.busService.upper = null;
            busServiceEditCtrl.busService.lower = null;
            busServiceEditCtrl.busService.isBig = false;
            busServiceEditCtrl.busService.upperHeader = '';
            busServiceEditCtrl.busService.lowerHeader = '';
            busServiceEditCtrl.totalSeats = 0;
        }

        busServiceEditCtrl.doService = function (){
            initialize();
            // service css class
            var sleeper = false;
            if(busServiceEditCtrl.services.type === 'SLEEPER'){
                sleeper = true;
                busServiceEditCtrl.serviceCls = 'seat';
            }else{
                busServiceEditCtrl.serviceCls = 'seat';
            }
        };

        busServiceEditCtrl.goToServices = function(){
            $location.url('/services');
        };

        $scope.$on('servicesCreateComplete', function (e, value) {
             busServiceEditCtrl.goToServices();
        });

        busServiceEditCtrl.saveService = function (){
        };

        return busServiceEditCtrl;

  })
