package ir.team_x.ariana.driver.model

data class FinishedModel(
    var id:Int,
    var customerId:Int,
    var customerName: String,
    var sourceAddressId:Int,
    var destinationAddressId:Int,
    var saveDate:String,
    var status:Int,
    var finishDate:String,
    var acceptDate:String,
    var price:String,
    var statusDes:String,
    var sourceAddress:String,
    var destinationAddress:String,
    var statusColor:String,
    var cancelDate:String
)