package com.example.cryptotrackerapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cryptotrackerapp.data.model.WatchlistCoin;
import com.example.cryptotrackerapp.repository.WatchlistRepository;

import java.util.List;

public class WatchlistViewModel extends AndroidViewModel {

    private final WatchlistRepository repository;
    private final LiveData<List<WatchlistCoin>> watchlist;

    public WatchlistViewModel(@NonNull Application app) {
        super(app);
        repository = new WatchlistRepository(app);
        watchlist = repository.getWatchlist();
    }

    public LiveData<List<WatchlistCoin>> getWatchlist() {
        return watchlist;
    }

    public void remove(WatchlistCoin coin) {
        repository.remove(coin);
    }

    public void add(WatchlistCoin coin) {
        repository.add(coin);
    }

    public void updateOrder(List<WatchlistCoin> coins) {
        repository.updateOrder(coins);
    }

    public void addToPortfolio(WatchlistCoin coin) {

    }
}
