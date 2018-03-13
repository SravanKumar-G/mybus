db.booking.createIndex({'ticketNo':1,'operatorId':1})
db.booking.createIndex({'serviceReportId':1,'operatorId':1})
db.serviceReport.createIndex({'jDate':1,'serviceId':1,'operatorId':1});
db.serviceReport.createIndex({'jDate':1,'serviceNumber':1});


db.booking.updateMany( {}, { $rename: { "serviceId": "serviceReportId" } } )