package com.example.cryptotrackerapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cryptotrackerapp.data.api.CoinGeckoApi;
import com.example.cryptotrackerapp.data.api.RetrofitClient;
import com.example.cryptotrackerapp.data.db.AppDatabase;
import com.example.cryptotrackerapp.data.db.CoinDao;
import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.data.model.CoinDetail;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CryptoRepository {

    private final CoinDao coinDao;
    private final CoinGeckoApi api;

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

    private static class FetchCoinsTask
            extends AsyncTask<Void, Void, Void> {

        private final CoinGeckoApi api;
        private final CoinDao coinDao;

        FetchCoinsTask(CoinGeckoApi api, CoinDao coinDao) {
            this.api = api;
            this.coinDao = coinDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Call<List<Coin>> call =
                        api.getMarketCoins(
                                "usd",
                                "market_cap_desc",
                                50,
                                1,
                                true
                        );

                Response<List<Coin>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    coinDao.clearAll();
                    coinDao.insertCoins(response.body());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    public void fetchCoinDetail(
            String coinId,
            MutableLiveData<CoinDetail> liveData
    ) {
        new FetchCoinDetailTask(api, liveData).execute(coinId);
    }

    private static class FetchCoinDetailTask
            extends AsyncTask<String, Void, CoinDetail> {

        private final CoinGeckoApi api;
        private final MutableLiveData<CoinDetail> liveData;

        FetchCoinDetailTask(
                CoinGeckoApi api,
                MutableLiveData<CoinDetail> liveData
        ) {
            this.api = api;
            this.liveData = liveData;
        }

        @Override
        protected CoinDetail doInBackground(String... ids) {
            try {
                Response<CoinDetail> response =
                        api.getCoinDetail(ids[0]).execute();

                if (response.isSuccessful()) {
                    return response.body();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CoinDetail coinDetail) {
            liveData.setValue(coinDetail);
        }
    }
}
