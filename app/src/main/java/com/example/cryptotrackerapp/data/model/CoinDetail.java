package com.example.cryptotrackerapp.data.model;

import com.google.gson.annotations.SerializedName;

public class CoinDetail {

    public String id;
    public String name;
    public String symbol;

    @SerializedName("market_data")
    public MarketData marketData;

    public Description description;

    public Image image;


    public static class MarketData {

        @SerializedName("current_price")
        public Price currentPrice;

        @SerializedName("market_cap")
        public Price marketCap;

        @SerializedName("total_volume")
        public Price volume;

        @SerializedName("high_24h")
        public Price high24h;

        @SerializedName("low_24h")
        public Price low24h;

        @SerializedName("price_change_percentage_24h")
        public double priceChangePercentage24h;
    }

    public static class Price {
        public double usd;
    }

    public static class Description {
        public String en;
    }

    public static class Image {
        public String large;
        public String small;
        public String thumb;
    }
}
