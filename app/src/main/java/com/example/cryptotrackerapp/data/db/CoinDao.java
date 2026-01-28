package com.example.cryptotrackerapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cryptotrackerapp.data.model.Coin;

import java.util.List;

@Dao
public interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoins(List<Coin> coins);

    @Query("SELECT * FROM coins ORDER BY marketCap DESC")
    LiveData<List<Coin>> getAllCoins();

    @Query("SELECT * FROM coins WHERE name LIKE :search OR symbol LIKE :search")
    LiveData<List<Coin>> searchCoins(String search);
    @Query("DELETE FROM coins")
    void clearAll();

}
