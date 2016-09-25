/**
 * Created by srinikandula on 9/25/16.
 */
angular.module('myBus')
.directive('number', function() {
    return {
        template: function(elem, attr) {
            return "<input type='text' placeholder='"+attr.placeholder+"' " +
            "class='form-control' ng-model='"+attr.model+"' " +
                "onkeypress='return event.charCode >= 48 && event.charCode <= 57' " +
                " ng-minlength='7' ng-maxlength='10' " +
                " ng-required='true' ng-disabled='"+attr.disabled+"' />";
        }
    };
});