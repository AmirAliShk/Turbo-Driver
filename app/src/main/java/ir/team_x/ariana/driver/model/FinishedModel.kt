package ir.team_x.ariana.driver.model

import org.json.JSONArray

data class FinishedModel(
    var id:Int,
    var customerId:Int,
    var customerName: String,
    var sourceAddressId:Int,
    var saveDate:String,
    var status:Int,
    var finishDate:String,
    var acceptDate:String,
    var price:String,
    var statusDes:String,
    var sourceAddress:String,
    var destinationAddress:String,
    var statusColor:String,
    var cancelDate:String,
    var isCreditCustomer:String
)