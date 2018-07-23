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
    'myBus.serviceComboModule',
    'myBus.tripComboModule',
    'myBus.officeExpensesModule',
    'myBus.paginationService',
    'myBus.expensesIncomesReportsModule',
    'myBus.returnTicketsModule',
    'myBus.bookingModule',
    'myBus.sequenceModule',
    'myBus.fuelExpenseReportModule',
    'myBus.invoiceModule',
    'myBus.gstFilters',
    'myBus.fillingStations',
    'myBus.tripReportsModule',
    'myBus.cargoBooking',
    'myBus.colletionZoneModule',
    'myBus.operatorAccountsModule',
    'myBus.staffModule'

]);

myBus.config(['$stateProvider','$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('newbooking',{
                level:1,
                url:'/newbooking',
                templateUrl: 'partials/cargoBooking.tpl.html',
                controller: 'CargoBookingController'
            }) .state('viewcargobooking/:id',{
                url:'/viewcargobooking/:id',
                templateUrl: 'partials/cargoBookingDetails.tpl.html',
                controller: 'CargoBookingController'
            }).state('cargobookings',{
                url:'/cargobookings',
                templateUrl: 'partials/cargoBookings.tpl.html',
                controller: 'CargoBookingListController'
            }).state('collectionzones',{
                url:'/collectionZones',
                templateUrl: 'partials/collectionZones.tpl.html',
                controller: 'CollectionZonesController'
            })
            .state('agents',{
                url:'/agents',
                templateUrl: 'partials/agents.tpl.html',
                controller: 'AgentController'
            }).state('operatoraccounts',{
                url:'/operatoraccounts',
                templateUrl: 'partials/operatoraccounts.tpl.html',
                controller: 'OperatorAccountsController'
            })
            .state('invoices',{
                url:'/inovices',
                templateUrl: 'partials/invoice.tpl.html',
                controller: 'InvoiceController'
            })
            .state('amenities',{
                url:'/amenities',
                templateUrl: 'partials/amenities.tpl.html',
                controller: 'AmenitiesController'
            })
            .state('cashbalances',{
                url:'/cashbalances',
                templateUrl: 'partials/cashBalances.tpl.html',
                controller: 'CashBalancesController'
            })
            .state('bookinganalytics',{
                url:'/bookinganalytics',
                templateUrl: 'partials/bookingtotalsbyphone.tpl.html',
                controller: 'BookingAnalyticsController'
            })
            .state('bookingsbyphone',{
                url:'/bookingsbyphone',
                templateUrl: 'partials/bookingsbyphone.tpl.html',
                controller: 'BookingsByPhoneController',
                params:{phoneNumber: null, totalBookings:0}
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
            .state('expensesincomesreports',{
                url:'/expensesincomesreports',
                templateUrl: 'partials/expensesIncomesReports.tpl.html',
                controller: 'expensesIncomesReportsCtrl'
            })
            .state('expensesincomesreports/:date',{
                url:'/expensesincomesreports/:date',
                templateUrl: 'partials/expensesIncomesReports.tpl.html',
                controller: 'expensesIncomesReportsCtrl'
            })
            .state('fuelexpensereports',{
                url:'/fuelexpensereports',
                templateUrl: 'partials/fuelExpenseReports.tpl.html',
                controller: 'fuelExpenseReportsCtrl'
            })
            .state('fuelexpensereports/:date',{
                url:'/fuelexpensereports/:date',
                templateUrl: 'partials/fuelExpenseReports.tpl.html',
                controller: 'fuelExpenseReportsCtrl'
            })
            .state('officeduereport/:id', {
                level:2,
                url:'/officeduereport/:id',
                templateUrl: 'partials/officeduereport.tpl.html',
                controller: 'OfficeDueReportController'
            })
            .state('officeduereport/:id/:date', {
                level:2,
                url:'/officeduereport/:id/:date',
                templateUrl: 'partials/officeduereportByDate.tpl.html',
                controller: 'OfficeDueByDateReportController'
            })
            .state('officeduereportbyservice/:serviceNumber', {
                level:2,
                url:'/officeduereportbyservice/:serviceNumber',
                templateUrl: 'partials/officeDueReportByService.tpl.html',
                controller: 'OfficeDueByServiceController'
            })
            .state('officeduereportbyagent/:agentName', {
                level:2,
                url:'/officeduereportbyagent/:agentName',
                templateUrl: 'partials/officeDueReportByAgent.tpl.html',
                controller: 'OfficeDueByAgentController'
            })
            .state('returnTicketsByDate/:date',{
                url:'/returnTicketsByDate/:date',
                templateUrl: 'partials/returnTicketsByDate.tpl.html',
                controller: 'returnTicketsByDateController'
            })
            .state('returnTicketsByAgent/:agent',{
                url:'/returnTicketsByAgent/:agent',
                templateUrl: 'partials/returnTicketsByAgent.tpl.html',
                controller: 'returnTicketsByAgentController'
            })
            .state('returntickets',{
                url:'/returntickets',
                templateUrl: 'partials/returnTickets.tpl.html',
                controller: 'returnTicketsController'
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
            .state('serviceReports/:date', {
                level:2,
                url:'/serviceReports/:date',
                templateUrl: 'partials/serviceReports.tpl.html',
                controller: 'ServiceReportsController'
            })
            .state('shipmentsequence', {
                level:2,
                url:'/shipmentSequence',
                templateUrl: 'partials/shipmentSequence.tpl.html',
                controller: 'sequenceController'
            })
            .state('pendingreports', {
                level:2,
                url:'/pendingReports',
                templateUrl: 'partials/pendingReports.tpl.html',
                controller: 'pendingReportController'
            })
            .state('reportstobereviewed', {
                level:2,
                url:'/reportstobereviewed',
                templateUrl: 'partials/pendingReports.tpl.html',
                controller: 'ReportsToBeReviewedController'
            })
            .state('servicecombo', {
                level:2,
                url:'/servicecombo',
                templateUrl: 'partials/serviceCombo.tpl.html',
                controller: 'ServiceComboController'
            })
            .state('tripcombo', {
                level:2,
                url:'/tripcombo',
                templateUrl: 'partials/tripCombo.tpl.html',
                controller: 'TripComboController'
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
            }).state('staff', {
                level:1,
                url:'/staff',
                templateUrl: 'partials/staff-list.tpl.html',
                controller: 'StaffListController'
            }).state('editstaff/:id', {
                level:1,
                url:'/editstaff/:id',
                templateUrl: 'partials/edit-staff.tpl.html',
                controller: 'EditStaffController'
            })
            .state('createvehicle', {
                level:2,
                url:'/createvehicle',
                templateUrl: 'partials/vehicle-edit.tpl.html',
                controller: 'EditVehicleController'
            })
            .state('vehicle/:id', {
                level:2,
                url:'/vehicle/:id',
                templateUrl: 'partials/vehicle-edit.tpl.html',
                controller: 'EditVehicleController'
            })
            .state('officeexpenses', {
                level:1,
                url:'/officeexpenses',
                templateUrl: 'partials/officeExpenses.tpl.html',
                controller: 'OfficeExpensesController'
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
                level:2,
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
            .state('manageroles',{
                level:2,
                url:'/manageroles',
                templateUrl: 'partials/managing-roles.tpl.html',
                controller: 'ManagingRolesController'
            })
            .state('booking',{
                level:2,
                url:'/booking',
                templateUrl: 'partials/booking-info.tpl.html',
                controller: 'BookingController as bookingCtrl'
            })
            .state('shipments',{
                url:'/shipments',
                templateUrl: 'partials/shipments.tpl.html',
                controller: 'ShipmentsController'
            })
            .state('shipment',{
                url:'/shipment/:id',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            })
            .state('editshipment',{
                url:'/shipment',
                templateUrl: 'partials/shipmentedit.tpl.html',
                controller: 'EditShipmentController'
            })
            .state('branchoffices',{
                url:'/branchoffices',
                templateUrl: 'partials/branchOffices.tpl.html',
                controller: 'BranchOfficesController'
            })
            .state('branchoffice',{
                url:'/branchoffice/:id',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            })
            .state('editbranchoffice',{
                url:'/branchoffice',
                templateUrl: 'partials/branchOfficeEdit.tpl.html',
                controller: 'EditBranchOfficeController'
            }).state('gstfilters',{
                url:'/gstfilters',
                templateUrl: 'partials/gstFilters.tpl.html',
                controller: 'GSTFiltersController'
            })
            .state('suppliers',{
                url:'/suppliers',
                templateUrl: 'partials/suppliers.tpl.html',
                controller: 'FillingStationsController'
            })
            .state('tripreports',{
                url:'/tripreports',
                templateUrl: 'partials/tripReports.tpl.html',
                controller: 'TripReportsController'
            })
            .state('tripreports/:date', {
                level:2,
                url:'/tripReports/:date',
                templateUrl: 'partials/tripReports.tpl.html',
                controller: 'TripReportsController'
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


myBus.run(function ($rootScope,$state, $location, appConfigManager, userManager, opratingAccountsManager) {
    $rootScope.menus=[];
    appConfigManager.fetchAppSettings(function (err, cfg) {
        $rootScope.appConfigManager = appConfigManager;
    }, true);
    userManager.getCurrentUser(function (err,data) {
        if (!err) {
            userManager.getGroupsForCurrentUser();
            myBus.constant('currentuser', data);
            $rootScope.currentuser = data;
            $rootScope.$broadcast("currentuserLoaded");
            opratingAccountsManager.getAccount($rootScope.currentuser.operatorId, function (operatorAccount) {
                $rootScope.operatorAccount = operatorAccount;
            });
        }
    });

});