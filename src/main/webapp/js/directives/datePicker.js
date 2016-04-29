
angular.module('myBus')
.directive('mybusDatepicker',['ngTable', 'ui.bootstrap',function($compile,$timeout){
	return {
		replace : true,
		template: 
        '<input '+
        	'id="{{id}}" '+ 
        	'name="{{name}}" '+
            'datepicker-popup="{{popup}}" '+ 
        	'datepicker-options="{{options}}" '+
        	'date-disabled="{{dateDisabled}}" '+
        	'min="{{min}}" '+
            'max="{{max}}" '+
            'open="opened" '+
            'ng-pattern="/^(?:[1-9]|1\d|2\d|3[0-1]) (?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec) (?:1|2)(?:\d{3})$/gim"/>',
		scope: {
			ngModel: '=',
			dateOptions: '@',
            dateDisabled: '@',
            opened: '=',
            min: '@',
            max: '@',
            popup: '@',
            options: '@',
            name: '@',
            id: '@'
		},
		link : function($scope,$element,$attrs,$controller){
			 $controller.$formatters.shift();
		}
			
	};
  
}])