"use strict";
/*global angular, _*/

angular.module('myBus.layoutModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusLayoutController', function ($scope, $http, $log, ngTableParams, $modal, $filter, busLayoutManager, $location) {
    $log.debug('BusLayoutController loading');
    var busLayoutCtrl = this;


        busLayoutCtrl.busLayout = {
            rows : null,
            type: null,
            upper : null,
            lower : null,
            upperHeader : '',
            lowerHeader : ''
        };

        busLayoutCtrl.busLayouts = {
            busLayout : null,
            availableOptions: [
              {id: 'SEMI_SLEEPER', name: 'SEMI_SLEEPER'},
              {id: 'AC_SEMI_SLEEPER', name: 'AC_SEMI_SLEEPER'},
              {id: 'SLEEPER', name: 'SLEEPER'}
            ]
       };

       busLayoutCtrl.busRows = {
           rows : null,
           availableOptions: [
            {id: '10', name: '10'},
            {id: '11', name: '11'},
            {id: '12', name: '12'},
            {id: '13', name: '13'},
            {id: '14', name: '14'},
            {id: '15', name: '15'},
            {id: '16', name: '16'},
            {id: '17', name: '17'}

           ]
      };

        var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};

        function getName(id){
            return $filter('filter')(seatNames.seats, {id: id })[0];
        }

        busLayoutCtrl.getSeatName = function(seat){
            return seat.displayName;
        }

        busLayoutCtrl.busColumns = {
             columns : null,
             availableOptions: [
               {id: 1, name: '1'},
                {id: 2, name: '2'},
                {id: 3, name: '3'},
                {id: 4, name: '4'}
             ]
        };

        busLayoutCtrl.middleRow = {
             middleRow : null,
             availableOptions: [
               {id: 2, name: 'Middle Row After First Seat'},
               {id: 3, name: 'Middle Row After Second Seat'},
               {id: 4, name: 'Middle Row After Third Seat'}
             ]
        };

        busLayoutCtrl.middleRowSeats = {
             middleRowSeat : null,
             availableOptions: [
               {id: 1, name: 'Yes'},
               {id: 2, name: 'No'}
             ]
        };

        function initialize(){
            busLayoutCtrl.busLayout.type = null;
            busLayoutCtrl.busLayout.rows = null;
            busLayoutCtrl.busLayout.upper = null;
            busLayoutCtrl.busLayout.lower = null;
            busLayoutCtrl.busLayout.isBig = false;
            busLayoutCtrl.busLayout.upperHeader = '';
            busLayoutCtrl.busLayout.lowerHeader = '';
        }

        busLayoutCtrl.doLayout = function (){
            initialize();
            // layout css class
            var sleeper = false;
            if(busLayoutCtrl.busLayouts.busLayout === 'SLEEPER'){
                sleeper = true;
                busLayoutCtrl.layoutCls = 'seat';
            }else{
                busLayoutCtrl.layoutCls = 'seat';
            }

            // building the rows and columns
            if(sleeper && busLayoutCtrl.busColumns.columns > 0 && busLayoutCtrl.busRows.rows > 0){
                for(var k = 0; k < 2; k++){
                    if(k===0){
                       busLayoutCtrl.busLayout.upper = getSeats(busLayoutCtrl.middleRow.middleRow, busLayoutCtrl.middleRowSeats.middleRowSeat);
                       busLayoutCtrl.busLayout.upperHeader = 'Upper';
                    }else{
                       busLayoutCtrl.busLayout.lower = getSeats(busLayoutCtrl.middleRow.middleRow, busLayoutCtrl.middleRowSeats.middleRowSeat);
                       busLayoutCtrl.busLayout.lowerHeader = 'Lower';
                    }
                }
            }else{
                busLayoutCtrl.busLayout.rows = getSeats(busLayoutCtrl.middleRow.middleRow, busLayoutCtrl.middleRowSeats.middleRowSeat);
            }

            busLayoutCtrl.busLayout.type = busLayoutCtrl.busLayouts.busLayout;
        };

        function getSeats(middleseatpos, middleseat){
            var rows = [];
            var cols = busLayoutCtrl.busColumns.columns;
            if(middleseatpos > 0){
                cols = parseInt(cols) +1;
            }

            if (cols > 4){
                busLayoutCtrl.busLayout.isBig = true;
            }

            for (var i = 1; i <= cols; i++){
                var seats = [];
                if(i === parseInt(middleseatpos)){
                    $log.debug(middleseatpos);
                    for (var j = 1; j <= busLayoutCtrl.busRows.rows; j++){
                        var displayName = getName(j).name+''+i;
                        if(middleseat === 1 && j === busLayoutCtrl.busRows.rows){
                            seats.push({displayName : displayName, [displayName]: displayName});
                        }else{
                            seats.push({displayName : null, [displayName]: null});
                        }
                    }
                }else{
                    for (var j = 1; j <= busLayoutCtrl.busRows.rows; j++){
                        var displayName = getName(j).name+''+i;
                        seats.push({displayName : displayName, [displayName]: displayName});
                    }
                }
                rows.push({seats :seats})
            }
            return rows;
        }

        busLayoutCtrl.saveLayout = function (){
            $log.debug(busLayoutCtrl.busLayout);
            var layoutToSave = {
                type: busLayoutCtrl.busLayout.type,
                rows: busLayoutCtrl.busLayout.rows
            };
            busLayoutManager.createLayout(layoutToSave);
        };

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layouts";

        return busLayoutCtrl;

  })
