/**
 * Created by srinikandula on 9/25/16.
 */
angular.module('myBus')
.directive('number', function() {
    return {
        template: function(elem, attr) {
        	
        	var minlength = 7;
        	if ( attr.minlength != null ){
        		minlength = attr.minlength;
        	}
        	
            return "<input type='text' placeholder='"+attr.placeholder+"' " +
            "class='form-control' data-ng-model='"+attr.model+"' " +
                "onkeypress='return event.charCode >= 48 && event.charCode <= 57' " +
                " data-ng-minlength='" + minlength +"' data-ng-maxlength='10' " +
                " data-ng-required='true' data-ng-disabled='"+attr.disabled+"' />";
        }
    };
});