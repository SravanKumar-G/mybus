"use strict";
/*global angular, _*/

angular.module('myBus.serviceEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusServiceEditController   ================================================
  // ==================================================================================================================

  .controller('BusServiceEditController', function ($rootScope, $scope, $http, $log, ngTableParams, $modal, $filter, busServiceManager, routesManager,cityManager, layoutNamesPromise, routeNamesPromise, $routeParams, $location, $cacheFactory) {
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
        busServiceEditCtrl.layouts = layoutNamesPromise.data;
        busServiceEditCtrl.routes = routeNamesPromise.data;
        busServiceEditCtrl.weeklyDays = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
        busServiceEditCtrl.specialDays = [{'date':''}]
        busServiceEditCtrl.taxModes  = [
                                      {id: 'FIXED', name: 'FIXED'},
                                      {id: 'PERCENTAGE', name: 'PERCENTAGE'}
                                    ];

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

        busServiceEditCtrl.getRouteCities = function() {
        	var routeId = busServiceEditCtrl.route._id.date;
        	console.log('route id -' + routeId);
        	console.log('route name -' +busServiceEditCtrl.route.name);
        	routesManager.getRoutes(function(data){
        		angular.forEach(data, function(value, key) {
        			console.log('value ' + value.id);
        			console.log('value name - ' + value.name);
            		if (busServiceEditCtrl.route.name === value.name){
                		console.log('via cities -' + value.viaCities);
                		busServiceEditCtrl.routeCities = [];                		
                        cityManager.getCities(function(data){
                            var cities = data;
                            angular.forEach(value.viaCities,function(existingCityId) {
                                angular.forEach(cities,function(city){
                                    if(existingCityId == city.id){
                                    	var routCity = {provideStop:false, hour:'', minutes:'', meridian:'', day:0};
                                    	routCity.name = city.name;
                                    	busServiceEditCtrl.routeCities.push(routCity);
                                    }
                                });
                            });
                        });
                        //break;
            		}
        		});
            });
        };
        
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
