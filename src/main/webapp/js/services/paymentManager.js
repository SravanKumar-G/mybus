
var portalApp = angular.module('myBus');

portalApp.factory('paymentManager',function($rootScope, $http, $log, $window){
	
	return {
		
		proceedToPay : function(payment,callback){
			
			$http.post('/api/v1/payment/payu',payment).success(function(data){
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		}
	};
});