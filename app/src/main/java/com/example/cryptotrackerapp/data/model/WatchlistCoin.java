package com.example.cryptotrackerapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchlist")
public class WatchlistCoin {

    @PrimaryKey
    @NonNull
    public String id;

    public String name;
    public String symbol;
    public double price;
    public String imageUrl;

    // 👇 REQUIRED for reorder
    public int position;

    public WatchlistCoin() {}

    public WatchlistCoin(
            @NonNull String id,
            String name,
            String symbol,
            double price,
            String imageUrl,
            int position
    ) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.imageUrl = imageUrl;
        this.position = position;
    }
}
