package com.example.cryptotrackerapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.cryptotrackerapp.data.db.AppDatabase;
import com.example.cryptotrackerapp.data.db.WatchlistDao;
import com.example.cryptotrackerapp.data.model.WatchlistCoin;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchlistRepository {

    private final WatchlistDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WatchlistRepository(Application app) {
        dao = AppDatabase.getInstance(app).watchlistDao();
    }

    public LiveData<List<WatchlistCoin>> getWatchlist() {
        return dao.getWatchlist();
    }

    public void add(WatchlistCoin coin) {
        executor.execute(() -> dao.addToWatchlist(coin));
    }

    public void remove(WatchlistCoin coin) {
        executor.execute(() -> dao.removeFromWatchlist(coin));
    }

    public void updateOrder(List<WatchlistCoin> coins) {
        executor.execute(() -> dao.update(coins));
    }
}
