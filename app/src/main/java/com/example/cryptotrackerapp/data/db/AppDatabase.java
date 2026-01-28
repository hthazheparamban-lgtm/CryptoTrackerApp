package com.example.cryptotrackerapp.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cryptotrackerapp.data.model.Coin;

@Database(entities = {Coin.class}, version = 2, exportSchema = false)
@TypeConverters(SparklineConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CoinDao coinDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "crypto_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
