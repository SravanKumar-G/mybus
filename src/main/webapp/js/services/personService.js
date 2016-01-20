var portalApp = angular.module('myBus');
portalApp.factory('personService', function ($http, $log) {

    return {
        loadPersons: function (callback) {
            $http.get('/api/v1/persons')
                .success(function (data) {
                    callback(data);
                }).error(function () {
                    alert("Error getting the data from the server");
                });

        },

        createPersons: function (person, callback) {
            $http.post('/api/v1/person', person).success(function (data) {
                callback(data);
            }).error(function () {
                alert("Error saving the data");
            });

        },
        deletePerson:function(personId,callback){
            $http.delete('/api/v1/person/'+personId).success(function(data){
                callback();
            });

        },
        findByIdPerson:function(personId,callback) {
            $http.get('/api/v1/person/' + personId).success(function (data) {
                callback(data);
            });
        },

        updatePerson: function(person,callback) {
            $http.put('/api/v1/person/'+person.id,person).success(function (data) {
                callback(data);
                //$rootScope.$broadcast('updatePersonCompleteEvent');
            });
        }
    }

});

