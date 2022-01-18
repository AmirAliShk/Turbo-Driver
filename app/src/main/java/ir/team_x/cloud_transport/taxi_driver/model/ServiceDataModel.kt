package ir.team_x.cloud_transport.taxi_driver.model

data class ServiceDataModel(
  var id:Int,
  var customerId:Int,
  var sourceAddressId:Int,
  var count:Int,
  var description:String,
  var fixedDescription:String,
  var carType:Int, // like economy....
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
  var carTypeName:String,
  var sourceAddress:String,
  var destinationAddress:String,
  var priceService:String,
  var discount:String,
)