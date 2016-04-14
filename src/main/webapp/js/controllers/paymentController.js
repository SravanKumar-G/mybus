
angular.module('myBus.paymentModule', ['ngTable', 'ui.bootstrap'])
.factory('FormSubmitter', FormSubmitter)
.directive('formSubmitter', formSubmitterDirective)
.controller("PaymentController", function($rootScope, $http,$scope,$modal, paymentManager,FormSubmitter){
	
	console.log("In PaymentController");
	
	$scope.headline = "Proceed to pay";
	$scope.paymentsDetails = "Payments and Refund Details "; 
	
	$scope.payment = {};
	
	$scope.payments =[];
	
	$scope.paymentToBeRefund = {};
	
    $scope.paymentButtonClicked = function(){
    	 	paymentManager.proceedToPay($scope.payment,function(data){
    	 		console.log('Payment response',data);
    	 		FormSubmitter.submit(data)
    	 	});
    };
    
    $scope.paymentButtonResetFields= function(){
    	$scope.payment = {};
    };
    
    $scope.getAllPaymentDetails = function(){
    	paymentManager.getAllPayments(function(data){
    		$scope.payments=data;
    	});
    };
    
    $scope.getAllPaymentDetails();
    
    $scope.paymentButtonForRefund= function(paymentid){
    	paymentManager.processToRefund(paymentid,function(data){
    		$scope.getAllPaymentDetails();
    	})
    };
    isInputValid = function(paymentStatus,refundStatus){
     	return false;
    };
    
});

function FormSubmitter($rootScope, $sce) {
	//Expose our Api
	return {
		submit: submit
	}

	function submit(params) {
		var url = $sce.trustAsResourceUrl(params.paymentGateways.pgRequestUrl);
		console.log('params',params,url);
		$rootScope.$broadcast('form.submit', {
			params: params,
			url : url 	
		});
	}
}

function formSubmitterDirective($timeout) {
    return {
        restrict: 'E',
        replace: true,
        template:'<form name="payForm" class = "payForm" action="{{ url }}" method="POST">' +
    	    	'<input type="hidden" name="firstname" value="{{ paymentForm.firstName }}" />'+
    	    	'<input type="hidden" name="lastname" value="{{ paymentForm.lastName }}" />'+
    	    	'<input type="hidden" name="surl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="phone" value="{{ paymentForm.phoneNo }}" />'+
    	    	'<input type="hidden" name="key" value="{{ paymentForm.paymentGateways.pgAccountID }}" />'+
    	    	'<input type="hidden" name="hash" value ="{{ paymentForm.hashCode }}" />'+
    	    	'<input type="hidden" name="curl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="furl" value="{{ paymentForm.paymentGateways.pgCallbackUrl }}" />'+
    	    	'<input type="hidden" name="txnid" value="{{ paymentForm.merchantRefNo }}" />'+
    	    	'<input type="hidden" name="productinfo" value="bus" size="64"/>'+
    	    	'<input type="hidden" name="amount" value="{{ paymentForm.amount }}" />'+
    	    	'<input type="hidden" name="pg" value="" />'+
    	    	'<input type="hidden" name="email" value="{{ paymentForm.emailID }}" />'+
    	    	'<input type="hidden" name="Bankcode" value="" />'+
    	    	'<input type="hidden" name="enforce_paymethod" value="creditcard|debitcard|netbanking|cashcard|Emi" />'+
    	    	'</form>',
	    	
    	    	
        link: function($scope, $elementType, $attrs) {
            $scope.$on('form.submit', function(event, data) {
                $scope.paymentForm = data.params;
                $scope.url = data.url;

                console.log('auto submitting form...',data,$elementType);

                $timeout(function() {
                	var submitEvent = $(payForm)
                	submitEvent.submit();
                })
            })
        }
    }
}