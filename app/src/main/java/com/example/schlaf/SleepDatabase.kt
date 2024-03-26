package com.example.schlaf

import android.content.Context
import androidx.room.*
import java.time.LocalDateTime

@Database(entities = [Sleep::class], version = 1)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDataDao(): SleepDataDao
}

@Dao
interface SleepDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepData(sleepEntry: Sleep)

    @Query("SELECT * FROM sleep_data ORDER BY year, month, day ASC")
    suspend fun getAllSleepData(): MutableList<Sleep>
}

object DatabaseProvider {
    private var instance: SleepDatabase? = null

    fun getDatabase(context: Context): SleepDatabase {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                SleepDatabase::class.java,
                "sleep_database"
            ).build()
            instance = db
            db
        }
    }
}