"use strict";
/*global angular, _*/

angular.module('myBus.personModules', ['ngTable', 'ui.bootstrap'])
    .controller('PersonController', function ($rootScope, $http,$scope,$modal, personService) {
        $scope.persons = [];

        $scope.displayPersons = function(data){
            $scope.persons = data;
        };

        $scope.loadPersons = function () {
            personService.loadPersons($scope.displayPersons)
        };

        $scope.loadPersons();

        $scope.addPersonOnClick = function(){
            var modalInstance = $modal.open({
                templateUrl: 'add-person-modal.html',
                controller: 'AddPersonModalController'
            });
        };

        $scope.deletePersonOnClick = function(personId){
            var modalInstance=$modal.open({
                templateUrl: 'delete-person-modal.html',
                controller: 'DeletePersonModalController',
                resolve:{
                    deleteId:function(){
                        return personId;
                    }
                }
            })


        };

        $scope.updatePersonOnClick = function(personId){
            console.log("Loading");
            var modalInstance = $modal.open({
                templateUrl: 'update-person-modal.html',
                controller: 'UpdatePersonModalController',
                resolve: {
                    fetchId: function () {
                        return personId;
                    }
                }

            });
        };
        //watch event handler
        $scope.$on('loadPersonsEvent', function (e, value) {
            $scope.loadPersons();
        });


    })

    .controller('AddPersonModalController',function($scope,$modalInstance,$http,$log,personService){
        $scope.person = {
            name: null,
            age: null,
            phone : null
        };
        $scope.ok = function () {
            if ($scope.person.name === null || $scope.person.age === null || $scope.person.phone == null) {
                $log.error("Empty person data.  nothing was added.");
                $modalInstance.close(null);
            }
            personService.createPersons($scope.person, function(data){
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.isInputValid = function () {

            return ($scope.person.name || '') !== '' &&
                ($scope.person.age || '') !== '' &&
                ($scope.person.phone || '') !== '';

        };

    })

    .controller('UpdatePersonModalController',function($scope,$modalInstance,$http,$log,personService,fetchId){
        $scope.person = {};
        $scope.person=fetchId;
        $scope.displayPersons = function(data){
            $scope.person = data;
        };

        $scope.setPersonIntoView = function(fetchId){
            personService.findByIdPerson(fetchId,$scope.displayPersons);

        };
        $scope.setPersonIntoView(fetchId);

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.firstCallBack = function(){
            console.log("executing function1");
        }

        $scope.ok = function (fetchId) {
            if ($scope.person.name === null || $scope.person.age === null || $scope.person.phone == null) {
                $log.error("Empty person data.  nothing was added.");
                $modalInstance.close(null);
            }
                personService.updatePerson($scope.person, function(data){
                    console.log("we are at OK");
                $modalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.isInputValid = function () {
            return ($scope.person.name || '') !== '' &&
                ($scope.person.age || '') !== '' &&
                ($scope.person.phone || '') !== '';
        };
    })
//-- -----------------------------for delete model popup-----------------------
    .controller('DeletePersonModalController',function($scope,$modalInstance,$http,$log,personService,deleteId){
        $scope.person = {};
        $scope.displayPersons = function(data){$scope.person = data;
        };

        $scope.loadPersons = function () {
            personService.loadPersons($scope.displayPersons)
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.ok = function (personId) {

            personService.deletePerson(deleteId,$scope.loadPersons);
            $modalInstance.close();

        };
        $scope.$on('loadPersonsEvent', function (e, value) {
            $scope.loadPersons();
        });
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    });