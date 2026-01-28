package com.example.cryptotrackerapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cryptotrackerapp.data.model.WatchlistCoin;

import java.util.List;

@Dao
public interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToWatchlist(WatchlistCoin coin);

    @Delete
    void removeFromWatchlist(WatchlistCoin coin);

    @Update
    void update(List<WatchlistCoin> coins);

    @Query("SELECT * FROM watchlist ORDER BY position ASC")
    LiveData<List<WatchlistCoin>> getWatchlist();

}
