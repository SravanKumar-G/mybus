
angular.module('myBus').directive('dateRangePicker', function() {
	return {
		restrict: 'A',
		templateUrl: '/partials/dateRangePicker.html',
		controller: 'DateRangePickerCtrl',
		replace: true
	};
});