package com.example.cryptotrackerapp.ui;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.cryptotrackerapp.data.api.CoinGeckoApi;
import com.example.cryptotrackerapp.data.api.RetrofitClient;
import com.example.cryptotrackerapp.data.db.AppDatabase;
import com.example.cryptotrackerapp.data.db.CoinDao;
import com.example.cryptotrackerapp.data.model.Coin;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class CryptoRepository {

    private final CoinDao coinDao;
    private final CoinGeckoApi api;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CryptoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        coinDao = db.coinDao();
        api = RetrofitClient.getClient().create(CoinGeckoApi.class);
    }

    public LiveData<List<Coin>> getAllCoins() {
        return coinDao.getAllCoins();
    }

    public LiveData<List<Coin>> searchCoins(String query) {
        return coinDao.searchCoins("%" + query + "%");
    }

    public void fetchCoinsFromApi() {
        executor.execute(() -> {
            try {
                Call<List<Coin>> call =
                        api.getMarketCoins("usd", "market_cap_desc", 50, 1, true);

                Response<List<Coin>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {

                    coinDao.clearAll();
                    coinDao.insertCoins(response.body());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
