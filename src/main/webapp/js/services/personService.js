var portalApp = angular.module('myBus');

portalApp.factory('personService', function ($rootScope, $http, $log) {

    return {
        loadPersons: function (callback) {
            $http.get('/api/v1/persons')
                .success(function (data) {
                    callback(data);
                }).error(function () {
                    alert("Error getting the data from the server");
                });

        },

        createPerson : function(person,callback) {
            $http.post('/api/v1/person',person).success(function (data) {
                callback(data);
            }).error(function () {
                alert("Error saving the data");
            });

        },

        savePerson : function(person,callback) {
            $http.put('/api/v1/person/'+person.id, person).success(function (data) {
                callback(data);
                $rootScope.$broadcast('loadPersonsEvent', 123);
            }).error(function () {
                alert("Error saving the data");
            });

        },

        deletePerson : function(personId,callback){
            $http.delete('/api/v1/person/'+personId).success(function(data){
                callback(data);

            });
        },

        getPerson : function(personId, callbackReference){
            $http.get('/api/v1/person/'+personId).success(function(resultData){
                console.log("loaded from server ");
                callbackReference(resultData);
            });

        }
    }
});