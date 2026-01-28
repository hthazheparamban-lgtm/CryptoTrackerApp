
package com.example.cryptotrackerapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "coins")
public class Coin {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("symbol")
    public String symbol;

    @SerializedName("name")
    public String name;

    @SerializedName("current_price")
    public double currentPrice;

    @SerializedName("market_cap")
    public double marketCap;

    @SerializedName("price_change_percentage_24h")
    public double priceChangePercentage24h;

    @SerializedName("image")
    public String image;
}
