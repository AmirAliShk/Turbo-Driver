package ir.team_x.ariana.delivery.room

import android.widget.ArrayAdapter
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCardNo(vararg cardNo: CardNumber)

    @Query("SELECT * FROM cardNumbers")
    fun getCardNo(): List<CardNumber>
}