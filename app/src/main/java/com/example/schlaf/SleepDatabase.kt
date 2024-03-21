package com.example.schlaf

import android.content.Context
import androidx.room.*

@Database(entities = [Sleep::class], version = 1)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDataDao(): SleepDataDao
}

@Dao
interface SleepDataDao {
    @Insert
    suspend fun insertSleepData(sleepData: Sleep)

    @Query("SELECT * FROM sleep_data ORDER BY date ASC")
    suspend fun getAllSleepData(): List<Sleep>
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