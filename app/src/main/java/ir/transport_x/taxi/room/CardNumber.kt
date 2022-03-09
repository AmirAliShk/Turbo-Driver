package ir.transport_x.taxi.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cardNumbers", indices = [Index(value = ["cardNo"], unique = true)])
data class CardNumber(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "cardNo") val cardNo: String,
    @ColumnInfo(name = "bankName") val bankName: String
)