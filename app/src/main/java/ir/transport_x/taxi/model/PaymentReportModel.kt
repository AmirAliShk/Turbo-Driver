package ir.transport_x.taxi.model

data class PaymentReportModel(
    var id:Int,
    var driverId:Int,
    var saveDate:String,
    var price:String,
    var cardNumber:String,
    var bankName:String,
    var description:String,
    var trackingCode:String,
    var replyStatus:Int,
    var replyDate:String,
    var trackingAccept:String
)