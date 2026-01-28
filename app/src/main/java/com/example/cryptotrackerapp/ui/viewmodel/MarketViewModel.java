package com.example.cryptotrackerapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.repository.CryptoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarketViewModel extends AndroidViewModel {

    private final CryptoRepository repository;
    private final MediatorLiveData<List<Coin>> displayCoins = new MediatorLiveData<>();

    private List<Coin> cachedCoins = new ArrayList<>();
    private Filter currentFilter = Filter.TOP;

    private enum Filter {
        TOP, TRENDING, GAINERS, LOSERS
    }

    public MarketViewModel(@NonNull Application application) {
        super(application);
        repository = new CryptoRepository(application);

        displayCoins.addSource(repository.getAllCoins(), coins -> {
            if (coins == null) return;
            cachedCoins = coins;
            applyFilter(cachedCoins);
        });
    }

    public LiveData<List<Coin>> getAllCoins() {
        return displayCoins;
    }

    public void refreshCoins() {
        repository.fetchCoinsFromApi();
    }

    public void loadTopCoins() {
        currentFilter = Filter.TOP;
        applyFilter(cachedCoins);
    }

    public void loadTrendingCoins() {
        currentFilter = Filter.TRENDING;
        applyFilter(cachedCoins);
    }

    public void loadGainers() {
        currentFilter = Filter.GAINERS;
        applyFilter(cachedCoins);
    }

    public void loadLosers() {
        currentFilter = Filter.LOSERS;
        applyFilter(cachedCoins);
    }

    public void searchCoins(String query) {
        if (query == null || query.trim().isEmpty()) {
            applyFilter(cachedCoins);
            return;
        }

        String q = query.toLowerCase(Locale.US);
        List<Coin> filtered = new ArrayList<>();

        for (Coin coin : cachedCoins) {
            if (coin.name.toLowerCase(Locale.US).contains(q) ||
                    coin.symbol.toLowerCase(Locale.US).contains(q)) {
                filtered.add(coin);
            }
        }

        applyFilter(filtered);
    }

    private void applyFilter(List<Coin> source) {
        List<Coin> result = new ArrayList<>(source);

        switch (currentFilter) {
            case GAINERS:
                result.sort((a, b) ->
                        Double.compare(b.priceChangePercentage24h, a.priceChangePercentage24h)
                );
                break;

            case LOSERS:
                result.sort((a, b) ->
                        Double.compare(a.priceChangePercentage24h, b.priceChangePercentage24h)
                );
                break;

            case TRENDING:
            case TOP:
            default:
                break;
        }

        displayCoins.setValue(result);
    }
}
