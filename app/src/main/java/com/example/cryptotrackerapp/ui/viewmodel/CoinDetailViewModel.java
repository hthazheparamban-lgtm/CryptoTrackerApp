package com.example.cryptotrackerapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cryptotrackerapp.data.model.CoinDetail;
import com.example.cryptotrackerapp.repository.CryptoRepository;

public class CoinDetailViewModel extends AndroidViewModel {

    private final CryptoRepository repository;
    private final MutableLiveData<CoinDetail> coinDetail = new MutableLiveData<>();

    public CoinDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new CryptoRepository(application);
    }

    public LiveData<CoinDetail> getCoinDetail() {
        return coinDetail;
    }

    public void loadCoinDetail(String coinId) {
        repository.fetchCoinDetail(coinId, coinDetail);
    }
}
