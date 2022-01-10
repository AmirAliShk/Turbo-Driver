package ir.team_x.cloud_transport.driver.model

data class AccountReportModel(
    var id:Int,
    var driverId:Int,
    var saveDate:String,
    var userId:Int,
    var updateDate:String,
    var updateUserId:String,
    var price:String,
    var type:Int,
    var description:String,
    var paymentDate:String,
    var serviceId:String,
    var paymentTypeName:String
)