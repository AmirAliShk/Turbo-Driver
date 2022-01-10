package ir.team_x.cloud_transport.taxi_driver.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// TODO when you change the entities structure, please increase the version of dataBase.

@Database(
    entities = [CardNumber::class],
    version = 1,
    exportSchema = false
)
abstract class MyDB : RoomDatabase() {
    abstract fun cardNumberDao(): CardNumberDao

    companion object {
        @Volatile
        private var Instance: MyDB? = null

        fun getDataBase(context: Context): MyDB {
            // if the Instance is not null, then return it,
            // if it is, then create the database
            return Instance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context.applicationContext, MyDB::class.java, "ArianaDeliveryDB")
                        .allowMainThreadQueries()
                        .build()
                Instance = instance
                instance
            }
        }

    }

}