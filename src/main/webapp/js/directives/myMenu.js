angular.module('myBus')
.directive('myMenu',[function(){
	return {
		 restrict: "E",
	     template: '<li><a ng-if="label != \'Configuration\'" ui-sref="{{label | lowercase}}">{{label}}</a>'+
	     				'<a ng-if="label == \'Configuration\'" class="dropdown-toggle" data-toggle="dropdown">{{label}}<span class="caret"></span></a>'+
	     			'</li>',
	     scope: {label:'@', noSecond: '='}
	};
}]);