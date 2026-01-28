package com.example.cryptotrackerapp.data.db;

import androidx.room.TypeConverter;

import com.example.cryptotrackerapp.data.model.Coin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SparklineConverter {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromSparkline(Coin.Sparkline sparkline) {
        if (sparkline == null || sparkline.price == null) return null;
        return gson.toJson(sparkline.price);
    }

    @TypeConverter
    public static Coin.Sparkline toSparkline(String data) {
        if (data == null) return null;

        Type listType = new TypeToken<List<Float>>() {}.getType();
        List<Float> prices = gson.fromJson(data, listType);

        Coin.Sparkline sparkline = new Coin.Sparkline();
        sparkline.price = prices;
        return sparkline;
    }
}
