package com.example.cryptotrackerapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.cryptotrackerapp.data.db.SparklineConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("sparkline_in_7d")
    @TypeConverters(SparklineConverter.class)
    public Sparkline sparkline;

    public static class Sparkline {
        @SerializedName("price")
        public List<Float> price;
    }
}
