
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
			
			$http.post('/api/v1/payment',payment).success(function(data){
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		},
		
		processToRefund : function(refundResponse,callback){
			
			$http.get('/api/v1/paymentRefund?pID='+refundResponse.pID+'&refundAmount='+refundResponse.refundAmount+'&disc='+refundResponse.disc).success(function(data){
				console.log("console"+data)
				if(data.paymentName=='PAYU'){
					swal("Great", JSON.parse(data.refundResponseParams).msg, "success");
				}else{
					swal("Great", data.refundResponseParams, "success");
				}
				callback(data);
			}).error(function (err,status) {
				sweetAlert("Error",err.message,"error");
				callback(err);
			});
		}
		
	};
});