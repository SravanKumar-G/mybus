"use strict";
/*global angular, _*/

angular.module('myBus.serviceEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusServiceEditController   ================================================
  // ==================================================================================================================

  .controller('BusServiceEditController', function ($rootScope, $scope, $http, $log, ngTableParams, $modal, $filter, busServiceManager, routesManager,cityManager, amenitiesManager, layoutNamesPromise, routeNamesPromise, amenitiesNamesPromise, $location, $cacheFactory,$stateParams) {
        $log.debug('BusServiceController loading');
        var busServiceEditCtrl = this;

        busServiceEditCtrl.valid = false;

        busServiceEditCtrl.totalSeats = 0;
        
        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Service Details";
        $scope.sSDates=[];
        $scope.amenitiesForUi=[];
        $scope.updateServiceButton = false;
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
        	serviceFares:[],
        	amenities:[]
        }
        busServiceEditCtrl.layouts = layoutNamesPromise.data;
        busServiceEditCtrl.routes = routeNamesPromise.data;
		busServiceEditCtrl.routesMap = {};
		//create map data
		for(var rt in routeNamesPromise.data){
			busServiceEditCtrl.routesMap[busServiceEditCtrl.routes[rt].id] = busServiceEditCtrl.routes[rt].name;
			busServiceEditCtrl.routesMap[busServiceEditCtrl.routes[rt].name] = busServiceEditCtrl.routes[rt].id;
		}
		busServiceEditCtrl.amenities = amenitiesNamesPromise.data;
        busServiceEditCtrl.weeklyDays = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
        busServiceEditCtrl.specialDays = [{'date':''}]
        busServiceEditCtrl.taxModes  = [
                                      {id: 'FIXED', name: 'FIXED'},
                                      {id: 'PERCENTAGE', name: 'PERCENTAGE'}
                                    ];

      var serviceId = $stateParams.id;
      if($stateParams.id != "create" && serviceId !== ''){
    	  $scope.updateServiceButton = true;
      }
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
			busServiceEditCtrl.busService.routesMap = {};

        }
		$scope.$watch('busServiceEditCtrl.busService.routeName', function() {
			if(busServiceEditCtrl.routesMap && busServiceEditCtrl.routesMap[busServiceEditCtrl.busService.routeName]){
				busServiceEditCtrl.busService.routeId = busServiceEditCtrl.routesMap[busServiceEditCtrl.busService.routeName];
				busServiceEditCtrl.getRouteCities();
			}
		});

        busServiceEditCtrl.getRouteCities = function(){
        	busServiceManager.busServiceConfig(busServiceEditCtrl.busService,function(data){
        		console.log("service config -"+data);
        		busServiceEditCtrl.busService = data;
        		busServiceEditCtrl.routeCities = [];
        		
        		cityManager.getCities(function(data){
        			var cities = data;
        			$scope.serviceFares = [];
        			$scope.dropingPoints=[];
        			$scope.boardingPoints=[];
        				
        			
        			
        			angular.forEach(busServiceEditCtrl.busService.serviceFares,function(serviceFare,index){
        				
        				var destinationCityId = $filter('filter')(cities,serviceFare.destinationCityId);
        				var sourceCityId = $filter('filter')(cities,serviceFare.sourceCityId); 
        				
        				if(serviceFare.active){
        					$scope.serviceFares.push({
        						sourceCityId:sourceCityId[0],
        						destinationCityId:destinationCityId[0],
        						active:true
        					})
        				}else {
        					$scope.serviceFares.push({
        						sourceCityId:sourceCityId[0],
        						destinationCityId:destinationCityId[0],
        						active:false
        					})
        				}
        				
        			});
        			
    				angular.forEach(busServiceEditCtrl.busService.boardingPoints,function(bpid){

    					var bpCityTem = $filter('filter')(cities,bpid.boardingPointId);
    					
    					angular.forEach(bpCityTem[0].boardingPoints,function(bp){
    						if(bp.id==bpid.boardingPointId){
        						bp.active = false
        						$scope.boardingPoints.push(bp);
    						}
    					})
    					
                    });
    				
    				angular.forEach(busServiceEditCtrl.busService.dropingPoints,function(dpid){

    					var dpCityTem = $filter('filter')(cities,dpid.droppingPointId);
    					
    					angular.forEach(dpCityTem[0].boardingPoints,function(dp){
    						if(dp.id==dpid.droppingPointId){
        						dp.active = false
        						$scope.dropingPoints.push(dp);
    						}
    					})
                    });
        			
    				busServiceEditCtrl.busService.serviceFares = [];
    				angular.forEach($scope.serviceFares,function(sf){
    					if(sf.active){
    						busServiceEditCtrl.busService.serviceFares.push(
    								{
    									active: true,
			    						cutOffTimeInMinutes: 0,
			    						destinationCityId: sf.destinationCityId.id,
			    						fare: 0,
			    						journeyDuration: 0,
			    						sourceCityId: sf.sourceCityId.id
    								});
    					}
    				});
    				
    				busServiceEditCtrl.busService.dropingPoints= [];
    				busServiceEditCtrl.busService.boardingPoints=[]
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
        
        $scope.updateService = function (){
        	busServiceEditCtrl.busService.effectiveFrom = $filter('date')(busServiceEditCtrl.busService.effectiveFrom,'yyyy-MM-dd');
        	busServiceEditCtrl.busService.effectiveTo = $filter('date')(busServiceEditCtrl.busService.effectiveTo,'yyyy-MM-dd');
        	
        	busServiceManager.updateService(busServiceEditCtrl.busService)
        };
        
        $scope.editPublishedService=function(){
        	busServiceEditCtrl.busService.status = "ACTIVE";
        }
        
        $scope.weeklyDays = function(checkedOrUnchecked,index) {

        	busServiceEditCtrl.busService.specialServiceDates = [];
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
    	   
    	   busServiceEditCtrl.busService.weeklyDays=[];
    	   if(busServiceEditCtrl.busService.specialServiceDates==undefined){
    		   busServiceEditCtrl.busService.specialServiceDates=[];
    		  /* busServiceEditCtrl.busService.specialServiceDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));
    		   $scope.sSDates.push($filter('date')(specialServiceDate,'yyyy-MM-dd'));*/
    	   }
    	   var index = busServiceEditCtrl.busService.specialServiceDates.indexOf($filter('date')(specialServiceDate,'yyyy-MM-dd'))
    	   if(index==-1){
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
						boardPoint[0].pickupTime = $filter('date')(time,'HH:mm a');
					}else{

						busServiceEditCtrl.busService.boardingPoints.push({
							boardingPointId:bpOrdbID,
							pickupTime : $filter('date')(time,'HH:mm a')
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
						dropingPoint[0].droppingTime = $filter('date')(time,'HH:mm a');
					}else{
						busServiceEditCtrl.busService.dropingPoints.push({
							droppingPointId:bpOrdbID,
							droppingTime:$filter('date')(time,'HH:mm a')
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
        
        $scope.addServiceFare = function(sourceCityId,destinationCityId,fieldValue,fieldName) {
        	if(busServiceEditCtrl.busService.serviceFares.length>0){
        		angular.forEach(busServiceEditCtrl.busService.serviceFares,function(sf){
        			if(sf.destinationCityId==destinationCityId && sf.sourceCityId==sourceCityId){
        				switch (fieldName) {
						case "at":
							sf['arrivalTime'] = $filter('date')(fieldValue,'HH:mm a');
							break;
						case "dt":
							sf['departureTime']= $filter('date')(fieldValue,'HH:mm a');
							break;
						case "fare":
							sf['fare'] =fieldValue;
							break;
						case "active":
							sf['active'] =fieldValue;
							break;
						default:
							break;
						}
        			}
        		})
        	}
        	
        }
        $scope.dailyService = function(){
        	busServiceEditCtrl.busService.specialServiceDates = [];
        	busServiceEditCtrl.busService.weeklyDays=[];
        }
        $scope.addOrRemoveAmenitiesToService = function(checkedOrUnchecked,amenityName){
        	if(busServiceEditCtrl.busService.amenities==undefined){
        		busServiceEditCtrl.busService.amenities=[];
        	}
        	if(checkedOrUnchecked){
        		var index = busServiceEditCtrl.busService.amenities.indexOf($filter('filter')(busServiceEditCtrl.busService.amenities, amenityName)[0]);
				if(index==-1){
					var amt = {
						name:amenityName,
						active:checkedOrUnchecked
					}
					busServiceEditCtrl.busService.amenities.push(amt);
				}
        	}else{
        		var index = busServiceEditCtrl.busService.amenities.indexOf($filter('filter')(busServiceEditCtrl.busService.amenities, amenityName)[0]);
        		if(index!=-1){
        			busServiceEditCtrl.busService.amenities.splice(index, 1);
        		}
        		
        	}
        }
        return busServiceEditCtrl;

  })
