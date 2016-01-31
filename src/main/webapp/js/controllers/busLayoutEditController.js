"use strict";
/*global angular, _*/

angular.module('myBus.layoutEditModules', ['ngTable', 'ui.bootstrap'])

  // ==================================================================================================================
  // ====================================    BusLayoutController   ================================================
  // ==================================================================================================================

  .controller('BusLayoutEditController', function ($rootScope, $scope, $http, $log, ngTableParams, $modal, $filter, busLayoutManager, $routeParams, $location, $cacheFactory) {
        $log.debug('BusLayoutController loading');
        var busLayoutEditCtrl = this;

        busLayoutEditCtrl.valid = false;

        busLayoutEditCtrl.totalSeats = 0;

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layout Details";

        busLayoutEditCtrl.busLayout = {
            rows : null,
            type: null,
            upper : null,
            lower : null,
            upperHeader : '',
            lowerHeader : ''
        };

        busLayoutEditCtrl.layouts = {
            type : null,
            availableOptions: [
              {id: 'SEMI_SLEEPER', name: 'SEMI_SLEEPER'},
              {id: 'AC_SEMI_SLEEPER', name: 'AC_SEMI_SLEEPER'},
              {id: 'SLEEPER', name: 'SLEEPER'}
            ]
       };

       busLayoutEditCtrl.busRows = {
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

      busLayoutEditCtrl.busColumns = {
           columns : null,
           availableOptions: [
             {id: 1, name: '1'},
              {id: 2, name: '2'},
              {id: 3, name: '3'},
              {id: 4, name: '4'}
           ]
      };

      busLayoutEditCtrl.middleRow = {
           middleRow : null,
           availableOptions: [
             {id: 2, name: 'Middle Row After First Seat'},
             {id: 3, name: 'Middle Row After Second Seat'},
             {id: 4, name: 'Middle Row After Third Seat'}
           ]
      };

      busLayoutEditCtrl.middleRowSeats = {
           middleRowSeat : null,
           availableOptions: [
             {id: true, name: 'Yes'},
             {id: false, name: 'No'}
           ]
      };


      var layOutId = $routeParams.id;

      if(layOutId !== ''){
        var cache = $cacheFactory.get($rootScope.id);
        if(cache){
            busLayoutEditCtrl.busLayout = cache.get(layOutId);
        }
        if(busLayoutEditCtrl.busLayout && busLayoutEditCtrl.busLayout.id !== ''){
            busLayoutEditCtrl.name = busLayoutEditCtrl.busLayout.name;
            busLayoutEditCtrl.layouts.type = busLayoutEditCtrl.busLayout.type;
            busLayoutEditCtrl.busRows.rows = busLayoutEditCtrl.busLayout.totalRows;
            busLayoutEditCtrl.busColumns.columns = busLayoutEditCtrl.busLayout.seatsPerRow;
            busLayoutEditCtrl.middleRow.value = busLayoutEditCtrl.busLayout.middleRowPosition;
            busLayoutEditCtrl.middleRowSeats.value = busLayoutEditCtrl.busLayout.middleRowLastSeat;
            var rows = [];
            angular.forEach(busLayoutEditCtrl.busLayout.rows, function(row, key) {
                var seats = [];
                angular.forEach(row, function(busseats, key) {
                    angular.forEach(busseats, function(busseat, key) {
                        if(busseat.number){
                            busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                        }
                        seats.push({number : busseat.number, [busseat.number]: busseat.number});
                    });
                });
                rows.push({seats: seats});
            });
            busLayoutEditCtrl.busLayout.rows = rows;
            busLayoutEditCtrl.valid = true;
        }else{
            busLayoutEditCtrl.busLayout = {
                rows : null,
                type: null,
                upper : null,
                lower : null,
                upperHeader : '',
                lowerHeader : ''
            }
        }
      }

        var seatNames = {"seats":[{"id":1,"name":"A"},{"id":2,"name":"B"},{"id":3,"name":"C"},{"id":4,"name":"D"},{"id":5,"name":"E"},{"id":6,"name":"F"},{"id":7,"name":"G"},{"id":8,"name":"H"},{"id":9,"name":"I"},{"id":10,"name":"J"},{"id":11,"name":"K"},{"id":12,"name":"L"},{"id":13,"name":"M"},{"id":14,"name":"N"},{"id":15,"name":"O"},{"id":16,"name":"P"},{"id":17,"name":"Q"},{"id":18,"name":"R"},{"id":19,"name":"S"},{"id":20,"name":"T"}]};

        function getName(id){
            return $filter('filter')(seatNames.seats, {id: id })[0];
        }

        busLayoutEditCtrl.getSeatName = function(seat){
            return seat.number;
        }

        function initialize(){
            busLayoutEditCtrl.busLayout.type = null;
            busLayoutEditCtrl.busLayout.rows = null;
            busLayoutEditCtrl.busLayout.upper = null;
            busLayoutEditCtrl.busLayout.lower = null;
            busLayoutEditCtrl.busLayout.isBig = false;
            busLayoutEditCtrl.busLayout.upperHeader = '';
            busLayoutEditCtrl.busLayout.lowerHeader = '';
            busLayoutEditCtrl.totalSeats = 0;
        }

        busLayoutEditCtrl.doLayout = function (){
            initialize();
            // layout css class
            var sleeper = false;
            if(busLayoutEditCtrl.layouts.type === 'SLEEPER'){
                sleeper = true;
                busLayoutEditCtrl.layoutCls = 'seat';
            }else{
                busLayoutEditCtrl.layoutCls = 'seat';
            }

            // building the rows and columns
            if(sleeper && busLayoutEditCtrl.busColumns.columns > 0 && busLayoutEditCtrl.busRows.rows > 0){
                for(var k = 0; k < 2; k++){
                    if(k===0){
                       busLayoutEditCtrl.busLayout.upper = getSeats(busLayoutEditCtrl.middleRow.middleRow, busLayoutEditCtrl.middleRowSeats.middleRowSeat);
                       busLayoutEditCtrl.busLayout.upperHeader = 'Upper';
                    }else{
                       busLayoutEditCtrl.busLayout.lower = getSeats(busLayoutEditCtrl.middleRow.middleRow, busLayoutEditCtrl.middleRowSeats.middleRowSeat);
                       busLayoutEditCtrl.busLayout.lowerHeader = 'Lower';
                    }
                }
            }else{
                busLayoutEditCtrl.busLayout.rows = getSeats(busLayoutEditCtrl.middleRow.middleRow, busLayoutEditCtrl.middleRowSeats.middleRowSeat);
            }

            //busLayoutEditCtrl.busLayout.type = busLayoutEditCtrl.layouts.type;
        };

        function getSeats(middleseatpos, middleseat){
            var rows = [];
            var cols = busLayoutEditCtrl.busColumns.columns;
            if(middleseatpos > 0){
                cols = parseInt(cols) +1;
            }

            if (cols > 4){
                busLayoutEditCtrl.busLayout.isBig = true;
            }

            for (var i = 1; i <= cols; i++){
                var seats = [];
                if(i === parseInt(middleseatpos)){
                    for (var j = 1; j <= busLayoutEditCtrl.busRows.rows; j++){
                        var number = getName(j).name+''+i;
                        if(middleseat === 1 && j === busLayoutEditCtrl.busRows.rows){
                            busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                            seats.push({number : number, [number]: number});
                        }else{
                            seats.push({number : null, [number]: null});
                        }
                    }
                }else{
                    for (var j = 1; j <= busLayoutEditCtrl.busRows.rows; j++){
                        var number = getName(j).name+''+i;
                        busLayoutEditCtrl.totalSeats = busLayoutEditCtrl.totalSeats + 1;
                        seats.push({number : number, [number]: number});
                    }
                }
                rows.push({seats :seats})
            }
            return rows;
        }

        busLayoutEditCtrl.goToLayouts = function(){
            $location.url('/layouts');
        };

        $scope.$on('layoutsCreateComplete', function (e, value) {
             busLayoutEditCtrl.goToLayouts();
        });

        busLayoutEditCtrl.saveLayout = function (){
            var rows = [];
            angular.forEach(busLayoutEditCtrl.busLayout.rows, function(row, key) {
                var seats = [];
                angular.forEach(row, function(busseats, key) {
                    angular.forEach(busseats, function(busseat, key) {
                        var seat = {
                            number : null,
                            displayName : null
                        };
                        seat.number = busseat.number;
                        seat.displayName = busseat[seat.number];
                        seats.push(seat);
                    });
                });
                rows.push({seats: seats});
            });
            var layoutToSave = {
                name : busLayoutEditCtrl.name,
                type: busLayoutEditCtrl.layouts.type,
                totalSeats : busLayoutEditCtrl.totalSeats,
                seatsPerRow : busLayoutEditCtrl.busColumns.columns,
                totalRows : busLayoutEditCtrl.busRows.rows,
                middleRowPosition : busLayoutEditCtrl.middleRow.middleRow,
                middleRowLastSeat : busLayoutEditCtrl.middleRowSeats.middleRowSeat,
                rows: rows,
                active : true,
                id : busLayoutEditCtrl.busLayout.id
            };

            if(layoutToSave.id && layoutToSave.id !== 'create'){
                busLayoutManager.updateLayout(layoutToSave);
            }else{
                busLayoutManager.createLayout(layoutToSave);
            }
        };

        $scope.GLOBAL_PENDING_NEIGHBORHOOD_NAME = '(PENDING)';

        $scope.headline = "Layouts";

        return busLayoutEditCtrl;

  })
