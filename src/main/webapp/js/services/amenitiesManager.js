var portalApp = angular.module("myBus");
portalApp.factory("amenitiesManager",function($rootScope,$http,$window,$log){
	var amenities = [];
	return {
		
		fechAmenities : function(){
			$http.get("/api/v1/amenities").success(function(data){
				amenities= data;
				$rootScope.$broadcast('amenitiesInitComplete');
			}).error(function(error){
				swal("oops", error, "error");
			})
		},
		
		getAmenities :function(){
			return amenities;
		},
		getAllAmenities : function(callback){
			
			$http.get("/api/v1/amenities").success(function(data){
				callback(data);
				$rootScope.$broadcast('amenitiesInitComplete');
			}).error(function(error){
				swal("oops", error, "error");
			})
		},
		
		addAmenity: function(amenity,callback) {
			$http.post("/api/v1/amenity",amenity).success(function(data){
				callback(data);
				$rootScope.$broadcast('amenitiesinitStart');
				swal("Great", "Amenity has been successfully added", "success");
			}).error(function(error){
				swal("oops", error, "error");
			})
		},
		
		getAmenityByID : function(amenityID,callback){
			$http.get("/api/v1/amenity/"+amenityID).success(function(data){
				callback(data);
			}).error(function(error){
				swal("oops", error, "error");
			})
		},
		
		updateAmenity : function(amenity,callback){
			$http.put("/api/v1/amenity",amenity).success(function(data){
				callback(data);
				$rootScope.$broadcast('amenitiesinitStart');
				swal("Great", "Amenity has been updated successfully", "success");
			}).error(function(error){
				swal("oops", error, "error");
			})
		},
		deleteAmenity : function(amenityID,callback){

			swal({
				title: "Are you sure?",
				text: "Are you sure you want to delete this Amenity?",
				type: "warning",
				showCancelButton: true,
				closeOnConfirm: false,
				confirmButtonText: "Yes, delete it!",
				confirmButtonColor: "#ec6c62"},function(){

					$http.delete("/api/v1/amenity/"+amenityID).success(function(data){
						callback(data);
						$rootScope.$broadcast('amenitiesinitStart');
						swal("Deleted!", "Amenity has been deleted successfully!", "success");
					}).error(function(error){
						swal("Oops", "We couldn't connect to the server!", "error");
					})

				})
		}
	}
	
	
});
