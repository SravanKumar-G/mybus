var portalApp = angular.module('myBus');

portalApp.factory('personService', function ($http, $log) {

    return {
        loadPersons: function (callback) {
            $http.get('/api/v1/persons')
                .success(function (persons) {
                   callback(persons);
                }).error(function(){
                    alert("Error getting the data from the server");
                });
        }
    }
});