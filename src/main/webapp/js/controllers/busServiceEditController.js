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
        $scope.sSDates=[];

        busServiceEditCtrl.busService = {
        	active:false,
			serviceName:null,
			serviceNumber:null,
			phoneEnquiry:null,
			cutoffTime:null,
        	serviceTaxType:null,
        	serviceTax:null, 
        	routeId:null,
	        effectiveFrom:null,
        	effectiveTo:null,
        	frequency:null,  
        	weeklyDays:[],
        	specialServiceDates:[],
        	boardingPoints:[],
        	dropingPoints:[],
        	serviceFares:[]
        }
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
    	  /*if(busServiceEditCtrl.busService && busServiceEditCtrl.busService.id !== ''){
    		  
    	  }else{
    		 
    		  busServiceEditCtrl.busService = {
    		        	active:false,
    					serviceName:null,
    					serviceNumber:null,
    					phoneEnquiry:null,
    					cutoffTime:null,
    		        	serviceTaxType:null,
    		        	serviceTax:null, 
    		        	layoutId:null,
    			        effectiveFrom:null,
    		        	effectiveTo:null,
    		        	frequency:null,
    		        	weeklyDays:[],
    		        	specialServiceDates:[],
    		        	boardingPoints:[],
    		        	dropingPoints:[],
    		        	serviceFares:[]
    		        }
    	  }*/
      }

        function initialize(){
        	
        	busServiceEditCtrl.busService.active=false;
        	busServiceEditCtrl.busService.serviceName=null;
        	busServiceEditCtrl.busService.serviceNumber=null;
        	busServiceEditCtrl.busService.phoneEnquiry=null;
        	busServiceEditCtrl.busService.cutoffTime=null;
        	busServiceEditCtrl.busService.serviceTaxType=null;
        	busServiceEditCtrl.busService.serviceTax=null; 
        	busServiceEditCtrl.busService.layoutId=null;
        	busServiceEditCtrl.busService.routeId=null;
        	
        	busServiceEditCtrl.busService.effectiveFrom=null;
        	busServiceEditCtrl.busService.effectiveTo=null;
        	busServiceEditCtrl.busService.weeklyDays=[];
        	busServiceEditCtrl.busService.specialServiceDates=[];
        	busServiceEditCtrl.busService.boardingPoints=[];
        	busServiceEditCtrl.busService.dropingPoints=[];
        	busServiceEditCtrl.busService.serviceFares=[];
        	
        }

        busServiceEditCtrl.getRouteCities = function() {
        	var routeId = busServiceEditCtrl.route.id;
        	console.log('route id -' + routeId);
        	busServiceEditCtrl.busService.routeId=routeId;
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
                            angular.forEach(cities,function(city){
                            	if(value.toCity===city.id){
                            		$scope.dropingPoints=$filter('filter')(city.boardingPoints,true);
                            		angular.forEach($scope.dropingPoints,function(bp){
                            			if(bp.active){
                            				bp.active=false;
                            			}
                            		});
                            	}
                            	if(value.fromCity===city.id){
                            		$scope.boardingPoints=$filter('filter')(city.boardingPoints,true);
                            		
                            		angular.forEach($scope.boardingPoints,function(dp){
                            			if(dp.active){
                            				dp.active=false;
                            			}
                            		});
                            	}
                            });
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

        $scope.saveService = function (){
        	busServiceEditCtrl.busService.effectiveFrom = $filter('date')(busServiceEditCtrl.busService.effectiveFrom,'yyyy-MM-dd');
        	busServiceEditCtrl.busService.effectiveTo = $filter('date')(busServiceEditCtrl.busService.effectiveTo,'yyyy-MM-dd');
        	
        	busServiceManager.createService(busServiceEditCtrl.busService)
        };
        
        $scope.editPublishedService=function(){
        	busServiceEditCtrl.busService.status = "ACTIVE";
        }
        
        $scope.weeklyDays = function(checkedOrUnchecked,index) {
       	  if(busServiceEditCtrl.busService.weeklyDays==undefined){
       		  busServiceEditCtrl.busService.weeklyDays=[];
          }
       	  if(checkedOrUnchecked){
       		  busServiceEditCtrl.busService.weeklyDays.push(busServiceEditCtrl.weeklyDays[index]);
       	  }else{
       		  busServiceEditCtrl.busService.weeklyDays.splice(busServiceEditCtrl.busService.weeklyDays.indexOf(busServiceEditCtrl.weeklyDays[index]), 1);
       	  }        	  
       };
       
       $scope.specialServiceDates = function(specialServiceDate) {
        	  if(busServiceEditCtrl.busService.specialServiceDates==undefined){
        		  busServiceEditCtrl.busService.specialServiceDates=[];
        		  busServiceEditCtrl.busService.specialServiceDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
        		  $scope.sSDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
        	  }else{
        		  busServiceEditCtrl.busService.specialServiceDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
        		  $scope.sSDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
        	  }        	  
        };
        $scope.removeSpecialServiceDatesFromList = function(specialServiceDate) {
      	  if(busServiceEditCtrl.busService.specialServiceDates==undefined){
      		busServiceEditCtrl.busService.specialServiceDates=[];
      	  }else{
      		var index = busServiceEditCtrl.busService.specialServiceDates.indexOf(specialServiceDate)
      		if(index!=-1)
      			busServiceEditCtrl.busService.specialServiceDates.splice(index, 1);
      			$scope.sSDates.splice(index, 1);
      	  }
      	  return '';
      };
        
        $scope.addOrRemoveDropingtime = function(bpOrdbID,active,bpOrdb,time,index){
        	switch (bpOrdb) {
			case 'bp':
				if(active){ 
					var boardPoint = $filter('filter')(busServiceEditCtrl.busService.boardingPoints, bpOrdbID)
					if(boardPoint.length>0){
						boardPoint[0].pickupTime = $filter('date')(time,'HH:mm');
					}else{

						busServiceEditCtrl.busService.boardingPoints.push({
							boardingPointId:bpOrdbID,
							pickupTime : $filter('date')(time,'HH:mm')
						});
					}
				}else{
					busServiceEditCtrl.busService.boardingPoints.splice(index, 1);
				}
				break;
				
			case 'dp':
				if(active){
					var dropingPoint = $filter('filter')(busServiceEditCtrl.busService.dropingPoints, bpOrdbID);
					if(dropingPoint.length>0){
						dropingPoint[0].droppingTime = $filter('date')(time,'HH:mm');
					}else{
						busServiceEditCtrl.busService.dropingPoints.push({
							droppingPointId:bpOrdbID,
							droppingTime:$filter('date')(time,'HH:mm')
						})
					}
				}else{
					busServiceEditCtrl.busService.dropingPoints.splice(index,1)
				}
				
				break;
			default:
				break;
			}
        };
        
        return busServiceEditCtrl;

  })
