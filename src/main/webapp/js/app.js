'use strict';

/* App Module */

var myBus = angular.module('myBus', [
  'ngRoute',
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
  'myBus.personModules',
  'myBus.layoutModules',
  'myBus.layoutEditModules',
  'myBus.serviceModules',
  'myBus.serviceEditModules',
  'myBus.homeModule',
  'myBus.busDetailModule'
]);

myBus.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
        when('/dashboard', {
          templateUrl: 'partials/home.tpl.html',
          controller: 'HomeController'
        }).
        when('/cities', {
          templateUrl: 'partials/cities-list.tpl.html',
          controller: 'CitiesController'
        })
        .when('/routes', {
          templateUrl: 'partials/routes-list.tpl.html',
          controller: 'RoutesController'
        })
        .when('/persons', {
          templateUrl: 'partials/person.html',
          controller: 'PersonController'
        })
        .
        when('/states', {
          templateUrl: 'partials/states.html',
          controller: 'CitiesController'
        }).
        when('/expenses', {
          templateUrl: 'partials/expenses-list.tpl.html',
          controller: 'ExpensesController'
        }).
        when('/city/:id', {
          templateUrl: 'partials/boardingpoints-list.tpl.html',
          controller: 'BoardingPointsListController'
        }).
        when('/layouts', {
          templateUrl: 'partials/buslayout.tpl.html',
          controller: 'BusLayoutController as busLayoutCtrl'
        }).
        when('/layouts/:id', {
          templateUrl: 'partials/buslayoutedit.tpl.html',
          controller: 'BusLayoutEditController as busLayoutEditCtrl'
        }).
        when('/busdetails', {
          templateUrl: 'partials/busdetails.tpl.html',
          controller: 'BusDetailsController'
        }).
        when('/users', {
          templateUrl: 'partials/users.tpl.html',
          controller: 'UsersController'
        }).
        when('/user', {
          templateUrl: 'partials/user-details.tpl.html',
          controller: 'UserEditController'
        }).
        when('/users-new', {
          templateUrl: 'partials/user-details.tpl.html',
          controller: 'UserAddController'
        }).
        when('/docs', {
          templateUrl: 'partials/api-docs.tpl.html',
          controller: 'APIDocsController'
        }).
        when('/account', {
          templateUrl: 'partials/account.tpl.html',
          controller: 'AccountController'
        })
        .otherwise({
          redirectTo: '/'
        });
  }]);

myBus.run(function ($rootScope, $location, appConfigManager, userManager) {
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
  //categoriesManager.reloadCategoryData();
  //classificationsManager.reloadClassificationData();
  //citiesAndNeighborhoodsManager.fetchAllCityAndNeighborhoodData();
});

