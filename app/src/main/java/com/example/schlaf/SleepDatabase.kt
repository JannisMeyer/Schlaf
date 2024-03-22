package com.example.schlaf

import android.content.Context
import androidx.room.*
import java.time.LocalDateTime

@Database(entities = [Sleep::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDataDao(): SleepDataDao
}

@Dao
@TypeConverters(LocalDateTimeConverter::class)
interface SleepDataDao {
    @Insert
    suspend fun insertSleepData(sleepData: MutableList<Sleep>)

    @Query("SELECT * FROM sleep_data ORDER BY date ASC")
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

class LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}