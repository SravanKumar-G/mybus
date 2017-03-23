'use strict';

/* App Module */

var myBus = angular.module('myBus', [
  'ui.router',
  'ngTable',
  'myBus.header',
  'myBus.userModule',
  'myBus.amenitiesModule',
  'myBus.cityModule',
  'myBus.cashTransfersModule',
  'myBus.vehicleModule',
  'myBus.branchOfficeModule',
  'myBus.serviceReportsModule',
  'myBus.paymentModule',
  'myBus.routeModule',
  'myBus.roleModule',
  'myBus.agentModule',
  'myBus.dueReportModule',
  'myBus.serviceComboModule'
]);

myBus.config(['$stateProvider','$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('agents',{
                url:'/agents',
                templateUrl: 'partials/agents.tpl.html',
                controller: 'AgentController'
            })
            .state('amenities',{
                url:'/amenities',
                templateUrl: 'partials/amenities.tpl.html',
                controller: 'AmenitiesController'
            })
            .state('cashtransfers',{
                url:'/cashtransfers',
                templateUrl: 'partials/cashTransfers.tpl.html',
                controller: 'cashTransfersController'
            })
            .state('dashboard', {
            	level:1,
                url:'/dashboard',
                templateUrl: 'partials/home.tpl.html',
                controller: 'HomeController'
            })
            .state('duereport', {
                level:1,
                url:'/duereport',
                templateUrl: 'partials/duereport.tpl.html',
                controller: 'DueReportController'
            })
            .state('officeduereport/:id', {
                level:1,
                url:'/officeduereport/:id',
                templateUrl: 'partials/officeduereport.tpl.html',
                controller: 'OfficeDueReportController'
            })
            .state('officeduereport/:id/:date', {
                level:1,
                url:'/officeduereport/:id/:date',
                templateUrl: 'partials/officeduereportByDate.tpl.html',
                controller: 'OfficeDueByDateReportController'
            })
            .state('servicereport/:id', {
                level:2,
                url:'/servicereport/:id',
                templateUrl: 'partials/serviceReport.tpl.html',
                controller: 'ServiceReportController'
            })
            .state('serviceform/:id', {
                level:2,
                url:'/serviceform/:id',
                templateUrl: 'partials/serviceform.tpl.html',
                controller: 'ServiceFormController'
            })
            .state('servicereports', {
                level:2,
                url:'/serviceReports',
                templateUrl: 'partials/serviceReports.tpl.html',
                controller: 'ServiceReportsController'
            })
            .state('servicecombo', {
                level:2,
                url:'/servicecombo',
                templateUrl: 'partials/serviceCombo.tpl.html',
                controller: 'ServiceComboController'
            })
            .state('cities', {
            	url:'/cities',
                level: 1,
                templateUrl: 'partials/cities-list.tpl.html',
                controller: 'CitiesController'
            })
            .state('routes', {
            	url:'/routes',
                level: 1,
                templateUrl: 'partials/routes-list.tpl.html',
                controller: 'RoutesController'
            })
            .state('persons', {
            	level:1,
                url:'/persons',
                templateUrl: 'partials/person.html',
                controller: 'PersonController'
            })
            .state('states', {
            	url:'/states',
                level: 1,
                templateUrl: 'partials/states.html',
                controller: 'CitiesController'
            })
            .state('payments', {
            	level:1,
                url:'/payments',
                templateUrl: 'partials/payments-list.tpl.html',
                controller: 'PaymentController'
            })
            .state('payment', {
                level:1,
                url:'/payment',
                templateUrl: 'partials/paymentAdd.tpl.html',
                controller: 'EditPaymentController'
            })
            .state('city/:id', {
            	level:2,
                url:'/city/:id',
                templateUrl: 'partials/boardingpoints-list.tpl.html',
                controller: 'BoardingPointsListController'
            })
            .state('vehicles', {
            	level:1,
                url:'/vehicles',
                templateUrl: 'partials/vehicle-list.tpl.html',
                controller: 'VehicleController'
            })
            .state('createvehicle', {
                level:2,
                url:'/vehicle/create',
                templateUrl: 'partials/vehicle-edit.tpl.html',
                controller: 'EditVehicleController'
            })
            .state('layouts', {
            	level:1,
                url:'/layouts',
                templateUrl: 'partials/buslayout.tpl.html',
                controller: 'BusLayoutController as busLayoutCtrl'
            })
            .state('layouts/:id', {
            	level:2,
                url:'/layouts/:id',
                templateUrl: 'partials/buslayoutedit.tpl.html',
                controller: 'BusLayoutEditController as busLayoutEditCtrl'
            })
            .state('services', {
            	level:1,
                url:'/services',
                templateUrl: 'partials/busService.tpl.html',
                controller: 'BusServiceController as busServiceCtrl'
            })
            .state('services/:id', {
            	level:3,
                url:'/services/:id',
                templateUrl: 'partials/busServiceEdit.tpl.html',
                controller: 'BusServiceEditController as busServiceEditCtrl',
                resolve : busServiceEditResolver
            })
            .state('roles', {
                url:'/roles',
                templateUrl: 'partials/roles.tpl.html',
                controller: 'RoleController'
            })
            .state('busdetails', {
            	level:1,
                url:'/busdetails',
                templateUrl: 'partials/busdetails.tpl.html',
                controller: 'BusDetailsController'
            })
            .state('users', {
            	level:1,
                url:'/users',
                templateUrl: 'partials/users.tpl.html',
                controller: 'UsersController'
            })
            .state('user', {
            	level:2,
                url:'/user/',
                templateUrl: 'partials/user-editDetails.tpl.html',
                controller: 'UserAddController'
            })
            .state('useredit', {
            	level:3,
                url:'/user/:id',
                templateUrl: 'partials/user-editDetails.tpl.html',
                controller: 'UpdateUserController'
            })
            .state('plans', {
            	level:2,
                url:'/plans',
                templateUrl: 'partials/agentPlan-details.tpl.html',
                controller: 'AgentPlanController'
            })
            .state('plan', {
            	level:3,
                url:'/plan',
                templateUrl: 'partials/agentPlanEdit-details.tpl.html',
                controller: 'AddAgentPlanTypeController'
            })
            .state('docs', {
            	level:1,
                url:'/docs',
                templateUrl: 'partials/api-docs.tpl.html',
                controller: 'APIDocsController'
            })
            .state('account', {
            	level:2,
                url:'/account',
                templateUrl: 'partials/account.tpl.html',
                controller: 'AccountController'
            })
            .state('trip',{
            	level:2,
                url:'/trip',
                templateUrl: 'partials/trip.tpl.html',
                controller: 'TripController as tripCtrl'
            })
            .state('managingroles',{
            	level:2,
                url:'/managingRoles',
                templateUrl: 'partials/managing-roles.tpl.html',
                controller: 'ManagingRolesController'
            })
            .state('booking',{
            	level:2,
                url:'/booking',
                templateUrl: 'partials/booking-info.tpl.html',
                controller: 'BookingController as bookingCtrl'
            }).state('shipments',{
                url:'/shipments',
                templateUrl: 'partials/shipments.tpl.html',
                controller: 'ShipmentsController'
            }).state('shipment',{
                url:'/shipment/:id',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            }).state('editshipment',{
                url:'/shipment',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            }).state('branchoffices',{
                url:'/branchoffices',
                templateUrl: 'partials/branchOffices.tpl.html',
                controller: 'BranchOfficesController'
            }).state('branchoffice',{
                url:'/branchoffice/:id',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            }).state('editbranchoffice',{
                url:'/branchoffice',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            });
        $urlRouterProvider.otherwise( '/');
    }]);

	var busServiceEditResolver = {
			layoutNamesPromise : ['busLayoutManager', function(busLayoutManager){
				return busLayoutManager.getActiveLayoutNames();
			}],
			routeNamesPromise : ['routesManager', function(routesManager){
				return routesManager.getActiveRouteNames();
			}],
			amenitiesNamesPromise : ['amenitiesManager', function(amenitiesManager){
				return amenitiesManager.getAmenitiesName();
			}]
	};


	myBus.run(function ($rootScope,$state, $location, appConfigManager, userManager) {
        $rootScope.menus=[];
		appConfigManager.fetchAppSettings(function (err, cfg) {
			$rootScope.appConfigManager = appConfigManager;
		}, true);
		userManager.getCurrentUser(function (err,data) {
			if (!err) {
				userManager.getGroupsForCurrentUser();
                myBus.constant('currentuser', data);
                $rootScope.currentuser = data;
			}
		});

		$rootScope.$on('$stateChangeSuccess', function() {
			console.log('Current state name: ' + $state.$current.name);
		});
});

