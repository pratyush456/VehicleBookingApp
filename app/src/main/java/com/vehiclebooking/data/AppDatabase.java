package com.vehiclebooking.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.vehiclebooking.data.dao.BookingDao;
import com.vehiclebooking.data.dao.SearchRecordDao;
import com.vehiclebooking.data.dao.UserDao;
import com.vehiclebooking.data.model.BookingEntity;
import com.vehiclebooking.data.model.SearchRecordEntity;
import com.vehiclebooking.data.model.UserEntity;

@Database(entities = {UserEntity.class, BookingEntity.class, SearchRecordEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract BookingDao bookingDao();
    public abstract SearchRecordDao searchRecordDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vehicle_booking_database")
                            .allowMainThreadQueries() // For simplicity in this migration, ideally use background threads
                            .fallbackToDestructiveMigration() // Reset DB on version change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
