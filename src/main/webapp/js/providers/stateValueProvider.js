"use strict";

angular.module('beaconApp')
  .value('states',
    function (rawStates) {
      return _.map(rawStates, function (name, abbreviation) {
        return {abbreviation: abbreviation, name: name, displayName: (abbreviation + ' - ' + name)};
      });
    }({
      'AP': 'Andhra Pradesh',
      'AR': 'Arunachal Pradesh',
      'AS': 'Assam',
      'BH': 'Bihar',
      'CG': 'Chhattisgarh'
    }));