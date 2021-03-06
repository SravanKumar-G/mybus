var services = db.serviceReport.find({}).toArray();
for(i in services) {
	var service = services[i];
	if(service.attrs.formId) {
		var bookings = db.booking.find({'formId':service.attrs.formId}).toArray();
		for(b in bookings) {
			var booking = bookings[b];
			booking.serviceName = service.serviceName;
			booking.serviceNumber = service.serviceNumber;
			db.booking.save(booking);
		}
	}

}



//find office dues by journey date
db.booking.aggregate([{ $match: { 'due': true } },{$group:{_id:"$jDate",total:{$sum:"$netAmt"}}}])
db.booking.aggregate([{ $match: { 'due': true } },{$group:{_id:"$serviceNumber",total:{$sum:"$netAmt"}}}])

db.orders.aggregate([
                     { $match: { status: "A" } },
                     { $group: { _id: "$cust_id", total: { $sum: "$amount" } } },
                     { $sort: { total: -1 } }
                   ])


db.branchOffice.updateMany({},{$set:{'cashBalance':0}})


//update branchid on payments
var users = db.user.find({}).toArray();
for(i in users) {
	var user = users[i];
	var payments = db.payment.find({'createdBy':user._id.str}).toArray();
	for(p in payments) {
		var payment = payments[p];
		payment.branchOfficeId = user.branchOfficeId;
		db.payment.save(payment);
	}
}


/**
 * Clear service reports for a day
 */

"journeyDate" : ISODate("2017-04-26T04:00:00Z")
serviceReport.getAttributes().put(ServiceReport.SUBMITTED_ID, savedForm.getId());
//delete ServiceForm
payment.setServiceFormId(serviceForm.getId());


//ISODate("2017-04-29T00:00:00Z") -- aws
 var serviceReports = db.serviceReport.find({"journeyDate" : ISODate("2017-04-28T04:00:00Z")}).toArray();
 var serviceReports = db.serviceReport.find({"journeyDate" : ISODate("2017-12-20T00:00:00Z")}).toArray();

 for(i in serviceReports) {
    var serviceReport = serviceReports[i];
    var bookings = db.booking.find({'serviceId':serviceReport._id.str}).toArray();
    for(b in bookings) {
    	var booking = bookings[b];
    	db.booking.remove({'ticketNo':booking.ticketNo});
	}
    print("serviceReport  "+ serviceReport._id.str +" bookings:" +bookings.length);
    db.booking.remove({'serviceId':serviceReport._id.str});
    db.serviceForm.remove({'serviceReportId':serviceReport._id.str});
    db.payment.remove({'serviceFormId': serviceReport.attrs.formId});
    db.serviceReport.remove({"_id": ObjectId(serviceReport._id.str)});
 }

 db.serviceReportStatus.remove({'reportDate': ISODate("2017-08-09T04:00:00Z")})
db.serviceReportStatus.remove({'reportDate': ISODate("2017-12-20T00:00:00Z")})


//add indexes to booking
db.booking.createIndex({'due':1})
db.booking.createIndex({'formId':1})
db.booking.createIndex({'serviceId':1})
db.booking.createIndex({'bookedBy':1})
db.booking.createIndex({'source':1})



//fix the submitted services but still show as pending

var serviceForms = db.serviceForm.find({},{'serviceReportId':1}).toArray()
for(i in serviceForms) {
    var serviceForm = serviceForms[i];
    if(serviceForm.serviceReportId) {
        var serviceReport = db.serviceReport.findOne(serviceForm.serviceReportId);
        if(serviceReport && !serviceReport.status) {
            print('Wrong status for service report  ' + serviceForm.serviceReportId +'   found form ' + serviceForm._id.str);
            serviceReport.status = "SUBMITTED";
            serviceReport.attrs.formId = serviceForm._id.str;
        }
	}
}



//remove duplicate bookings

var duplicateTickets = db.booking.aggregate([{"$match":{"formId":{$exists:true}}},{"$group" : {_id:"$ticketNo", ids: { $push : "$_id"},serviceReportIds: { $push : "$serviceReportId"},count:{$sum:1},due:{$push:"$due"}}},{"$match": {"count": { "$gt": 1 }}}],{"allowDiskUse":true}).toArray()

for(t in duplicateTickets){
    var ticket = duplicateTickets[t];

    for(var i=0;i<ticket.due.length-1;i++){
        if(ticket.due[i] === false){
            db.booking.remove({"_id":ticket.ids[i]})
        }
    }
}

var duplicateTickets = db.booking.aggregate([{"$group" : {_id:"$ticketNo", ids: { $push : "$_id"},count:{$sum:1},due:{$push:"$due"}}},{"$match": {"count": { "$gt": 1 }}}]).toArray()

for(t in duplicateTickets){
    var ticket = duplicateTickets[t];
    for(var i=0;i<ticket.due.length;i++){
        if(ticket.due[0] === true && ticket.due[1] === true){
            db.booking.remove({"_id":ticket.ids[0]})
        }
        if(ticket.due[0] === true && ticket.due[1] === false){
            db.booking.remove({"_id":ticket.ids[1]})
        }
        if(ticket.due[0] === false && ticket.due[1] === true){
            db.booking.remove({"_id":ticket.ids[0]})
        }
    }
}




//Fix office expenses after migrating to ApproveStatus.java

var officeExpenses = db.officeExpense.find({}).toArray()
for(var i=0;i<officeExpenses.length;i++){
     if(officeExpenses[i].status === 'APPROVED'){
         officeExpenses[i].status = 'Approved';
     } else if(officeExpenses[i].status === 'REJECTED'){
         officeExpenses[i].status = 'Rejected';
     }
    db.officeExpense.save(officeExpenses[i]);
}