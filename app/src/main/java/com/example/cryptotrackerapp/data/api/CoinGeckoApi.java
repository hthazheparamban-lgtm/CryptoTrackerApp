package com.example.cryptotrackerapp.data.api;

import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.data.model.CoinDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinGeckoApi {

    @GET("coins/markets")
    Call<List<Coin>> getMarketCoins(
            @Query("vs_currency") String currency,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline
    );

    @GET("coins/{id}")
    Call<com.example.cryptotrackerapp.data.model.CoinDetail> getCoinDetail(
            @Path("id") String coinId
    );

}
