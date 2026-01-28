package com.example.cryptotrackerapp.repository;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.cryptotrackerapp.data.api.CoinGeckoApi;
import com.example.cryptotrackerapp.data.api.RetrofitClient;
import com.example.cryptotrackerapp.data.db.AppDatabase;
import com.example.cryptotrackerapp.data.db.CoinDao;
import com.example.cryptotrackerapp.data.model.Coin;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CryptoRepository {

    private CoinDao coinDao;
    private CoinGeckoApi api;
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
        new FetchCoinsTask(api, coinDao).execute();
    }


    private static class FetchCoinsTask extends AsyncTask<Void, Void, Void> {
        private CoinGeckoApi api;
        private CoinDao coinDao;

        FetchCoinsTask(CoinGeckoApi api, CoinDao coinDao) {
            this.api = api;
            this.coinDao = coinDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Call<List<Coin>> call = api.getMarketCoins("usd", "market_cap_desc", 50, 1, true);
            try {
                Response<List<Coin>> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    coinDao.insertCoins(response.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
