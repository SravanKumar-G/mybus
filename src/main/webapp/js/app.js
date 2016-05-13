'use strict';

/* App Module */

var myBus = angular.module('myBus', [
  'ui.router',
  'ngAnimate',
  'ngTouch',
  'ngTable',
  'dndLists',
  'ui.bootstrap',
  'unsavedChanges',
  'angularSpinner',
  'myBus.routesModules',
  'myBus.citiesModules',
  'myBus.expensesModules',
  'myBus.boardingPointModule',
  'myBus.vehicleModule',
  'myBus.personModules',
  'myBus.layoutModules',
  'myBus.layoutEditModules',
  'myBus.serviceModules',
  'myBus.serviceEditModules',
  'myBus.homeModule',
  'myBus.busDetailModule',
  'myBus.userModule',
  'myBus.agentPlanModule',
  'myBus.paymentModule',
  'myBus.tripModule',
  'myBus.amenitiesModule'
]);

myBus.config(['$stateProvider','$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        $stateProvider.
            state('dashboard', {
                url:'/dashboard',
                templateUrl: 'partials/home.tpl.html',
                controller: 'HomeController'
            }).
            state('cities', {
                url:'/cities',
                templateUrl: 'partials/cities-list.tpl.html',
                controller: 'CitiesController'
            })
            .state('routes', {
                url:'/routes',
                templateUrl: 'partials/routes-list.tpl.html',
                controller: 'RoutesController'
            })
            .state('persons', {
                url:'/persons',
                templateUrl: 'partials/person.html',
                controller: 'PersonController'
            })
            .
            state('states', {
                url:'/states',
                templateUrl: 'partials/states.html',
                controller: 'CitiesController'
            }).
            state('expenses', {
                url:'/expenses',
                templateUrl: 'partials/expenses-list.tpl.html',
                controller: 'ExpensesController'
            }).
            state('city/:id', {
                url:'/city/:id',
                templateUrl: 'partials/boardingpoints-list.tpl.html',
                controller: 'BoardingPointsListController'
            }).
            state('vehicles', {
                url:'/vehicles',
                templateUrl: 'partials/vehicle-list.tpl.html',
                controller: 'VehicleController'
            }).
            state('layouts', {
                url:'/layouts',
                templateUrl: 'partials/buslayout.tpl.html',
                controller: 'BusLayoutController as busLayoutCtrl'
            }).
            state('layouts/:id', {
                url:'/layouts/:id',
                templateUrl: 'partials/buslayoutedit.tpl.html',
                controller: 'BusLayoutEditController as busLayoutEditCtrl'
            }).
            state('services', {
                url:'/services',
                templateUrl: 'partials/busService.tpl.html',
                controller: 'BusServiceController as busServiceCtrl'
            }).
            state('services/:id', {
                url:'/services/:id',
                templateUrl: 'partials/busServiceEdit.tpl.html',
                controller: 'BusServiceEditController as busServiceEditCtrl',
                resolve : busServiceEditResolver
            }).
            state('busdetails', {
                url:'/busdetails',
                templateUrl: 'partials/busdetails.tpl.html',
                controller: 'BusDetailsController'
            }).
            state('users', {
                url:'/users',
                templateUrl: 'partials/users.tpl.html',
                controller: 'UsersController'
            }).
            state('user', {
                url:'/user',
                templateUrl: 'partials/user-details.tpl.html',
                controller: 'UserAddController'
            }).
            state('userEdit', {
                url:'/user/',
                params:{
                    idParam : null
                },
                templateUrl: 'partials/user-editDetails.tpl.html',
                controller: 'UpdateUserController'
            }).

            state('plans', {
                url:'/plans',
                templateUrl: 'partials/agentPlan-details.tpl.html',
                controller: 'AgentPlanController'
            }).
            state('plan', {
                url:'/plan',
                templateUrl: 'partials/agentPlanEdit-details.tpl.html',
                controller: 'AddAgentPlanTypeController'
            }).
            state('docs', {
                url:'/docs',
                templateUrl: 'partials/api-docs.tpl.html',
                controller: 'APIDocsController'
            }).
            state('account', {
                url:'/account',
                templateUrl: 'partials/account.tpl.html',
                controller: 'AccountController'
            }).
            state('payment',{
                url:'/payment',
                templateUrl: 'partials/payment.tpl.html',
                controller: 'PaymentController'
            }).
            state('amenities',{
                url:'/amenities',
                templateUrl: 'partials/amenities.tpl.html',
                controller: 'AmenitiesController'
            }).
            state('trip',{
                url:'/trip',
                templateUrl: 'partials/trip.tpl.html',
                controller: 'TripController'
            });
        $urlRouterProvider.otherwise( '/');
    }]);

	var busServiceEditResolver = {
			layoutNamesPromise : ['busLayoutManager', function(busLayoutManager){
				return busLayoutManager.getActiveLayoutNames();
			}],
			routeNamesPromise : ['routesManager', function(routesManager){
				return routesManager.getActiveRouteNames();
			}]
	};


myBus.run(function ($rootScope,$state, $location, appConfigManager, userManager) {
  appConfigManager.fetchAppSettings(function (err, cfg) {
    $rootScope.appConfigManager = appConfigManager;
  }, true);
  userManager.getCurrentUser(function (err) {
    if (!err) {
      userManager.getGroupsForCurrentUser();
    }
  });
  $rootScope.userManager = userManager;
  $rootScope.poiSearchText = '';
  $rootScope.searchPOIs = function () {
    $location.url('/businesses?name=' + $rootScope.poiSearchText);
  };
  $rootScope.$on('$stateChangeSuccess', function() {
     console.log('Current state name: ' + $state.$current.name);
  });
  //categoriesManager.reloadCategoryData();
  //classificationsManager.reloadClassificationData();
  //citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
});

