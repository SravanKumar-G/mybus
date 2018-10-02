db.booking.createIndex({'ticketNo':1,'operatorId':1})
db.booking.createIndex({'serviceReportId':1,'operatorId':1})
db.serviceReport.createIndex({'jDate':1,'serviceId':1,'operatorId':1});
db.serviceReport.createIndex({'jDate':1,'serviceNumber':1});


db.booking.updateMany( {}, { $rename: { "serviceId": "serviceReportId" } } )

db.cargoBooking.createIndex({'fromBranchId':1});
db.cargoBooking.createIndex({'toBranchId':1});
db.cargoBooking.createIndex({'createdBy':1});
db.cargoBooking.createIndex({'createdAt':1});
db.cargoBooking.createIndex({'fromContact':1});
db.cargoBooking.createIndex({'toContact':1});
db.cargoBooking.createIndex({'paymentType':1});


