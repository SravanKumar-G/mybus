"use strict";
/*global angular, _*/

angular.module('myBus.personModules', ['ngTable', 'ui.bootstrap'])
    .controller('PersonController', function ($scope, personService) {
        $scope.persons = [];

        $scope.displayPersons = function(data){
            $scope.persons = data;
        };

        $scope.loadPersons = function () {
            personService.loadPersons($scope.displayPersons)
        }

        $scope.loadPersons();
    });

