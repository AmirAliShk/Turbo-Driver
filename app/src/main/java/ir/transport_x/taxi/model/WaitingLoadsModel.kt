package ir.transport_x.taxi.model

data class WaitingLoadsModel(
    var id:Int,
    var customerId:Int,
    var sourceAddressId:Int,
    var count:Int,
    var description:String,
    var carType:Int,
    var stopTime:Int,
    var saveDate:String,
    var userId:Int,
    var driverId:Int,
    var finishDate:String,
    var voipId:String,
    var acceptDate:String,
    var price:String,
    var customerName:String,
    var phoneNumber:String,
    var mobile:String,
    var sourceStationName:String,
    var fixedMessage:String,
    var sourceAddress:String,
    var destinationAddress:String,
    var serviceTypeId:Int
)