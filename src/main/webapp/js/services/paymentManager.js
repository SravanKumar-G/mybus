
var portalApp = angular.module('myBus');

portalApp.factory('paymentManager',function($rootScope, $http, $log, $window){

	return {
		
		getAllPayments : function(callback){
			$http.get('/api/v1/payments').success(function(data){
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		},
		
		proceedToPay : function(payment,callback){
			
			$http.post('/api/v1/payment/payu',payment).success(function(data){
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		},
		
		processToRefund : function(paymentid,callback){
			
			$http.post('/api/v1/paymentRefund',paymentid).success(function(data){
				console.log("console"+data)
				swal("Great", JSON.parse(data.refundResponseParams).msg, "success");
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		}
		
	};
});