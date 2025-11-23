package com.vehiclebooking.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vehiclebooking.data.dao.BookingDao
import com.vehiclebooking.data.dao.SearchRecordDao
import com.vehiclebooking.data.dao.UserDao
import com.vehiclebooking.data.model.BookingEntity
import com.vehiclebooking.data.model.SearchRecordEntity
import com.vehiclebooking.data.model.UserEntity
import com.vehiclebooking.security.SecurePreferences
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(
    entities = [UserEntity::class, BookingEntity::class, SearchRecordEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun searchRecordDao(): SearchRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private const val DB_NAME = "vehicle_booking_database"
        private const val PASSPHRASE_KEY = "db_passphrase"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            // Get or generate secure passphrase for database encryption
            val passphrase = getOrCreatePassphrase(context)
            val passphraseBytes = SQLiteDatabase.getBytes(passphrase.toCharArray())
            val factory = SupportFactory(passphraseBytes)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
            .openHelperFactory(factory) // Enable SQLCipher encryption
            .fallbackToDestructiveMigration() // Reset DB on version change
            .build()
        }
        
        /**
         * Get existing passphrase or create a new secure one
         * Passphrase is stored in EncryptedSharedPreferences
         */
        private fun getOrCreatePassphrase(context: Context): String {
            // Ensure SecurePreferences is initialized
            SecurePreferences.init(context)
            
            var passphrase = SecurePreferences.getString(PASSPHRASE_KEY)
            
            if (passphrase == null) {
                // Generate a new secure random passphrase
                passphrase = generateSecurePassphrase()
                SecurePreferences.putString(PASSPHRASE_KEY, passphrase)
            }
            
            return passphrase
        }
        
        /**
         * Generate a cryptographically secure random passphrase
         */
        private fun generateSecurePassphrase(): String {
            val random = SecureRandom()
            val bytes = ByteArray(32) // 256 bits
            random.nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
