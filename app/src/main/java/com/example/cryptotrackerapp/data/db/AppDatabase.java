package com.example.cryptotrackerapp.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.data.model.WatchlistCoin;

@Database(
        entities = {
                Coin.class,
                WatchlistCoin.class
        },
        version = 6,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CoinDao coinDao();
    public abstract WatchlistDao watchlistDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "crypto_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
