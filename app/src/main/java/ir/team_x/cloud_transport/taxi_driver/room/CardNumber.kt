package ir.team_x.cloud_transport.taxi_driver.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardNumbers")
data class CardNumber(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "cardNo") val cardNo: String,
    @ColumnInfo(name = "bankName") val bankName: String
)