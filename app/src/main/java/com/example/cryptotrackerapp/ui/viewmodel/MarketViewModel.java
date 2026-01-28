package com.example.cryptotrackerapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.repository.CryptoRepository;
import java.util.List;

public class MarketViewModel extends AndroidViewModel {

    private CryptoRepository repository;
    private LiveData<List<Coin>> allCoins;

    public MarketViewModel(@NonNull Application application) {
        super(application);
        repository = new CryptoRepository(application);
        allCoins = repository.getAllCoins();
    }

    public LiveData<List<Coin>> getAllCoins() {
        return allCoins;
    }

    public LiveData<List<Coin>> searchCoins(String query) {
        return repository.searchCoins(query);
    }
    public void refreshCoins() {
        repository.fetchCoinsFromApi();
    }
}
