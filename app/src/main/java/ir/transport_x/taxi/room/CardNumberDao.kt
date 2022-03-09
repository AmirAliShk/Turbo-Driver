package ir.transport_x.taxi.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCardNo(vararg cardNo: CardNumber)

    @Query("SELECT cardNo FROM cardNumbers")
    fun getCardNo(): List<String>
}