package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.model.Coin;
import com.example.cryptotrackerapp.data.model.HeroCoin;
import com.example.cryptotrackerapp.ui.adapter.CoinAdapter;
import com.example.cryptotrackerapp.ui.viewmodel.MarketViewModel;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarketFragment extends Fragment {

    private MarketViewModel viewModel;
    private CoinAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View errorLayout;
    private TextView emptyText;

    private ImageView heroCoinImage;
    private TextView heroCoinName;
    private TextView heroCoinPrice;

    private ChipGroup categoryChipGroup;
    private SearchView searchView;

    private final List<HeroCoin> heroCoins = new ArrayList<>();
    private int heroIndex = 0;
    private boolean heroRunning = false;

    private final Runnable heroRunnable = new Runnable() {
        @Override
        public void run() {
            if (!heroRunning || heroCoins.isEmpty()) return;
            heroIndex = (heroIndex + 1) % heroCoins.size();
            updateHero();
            heroCoinImage.postDelayed(this, 5000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_market, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        heroCoinImage = view.findViewById(R.id.heroCoinImage);
        heroCoinName = view.findViewById(R.id.heroCoinName);
        heroCoinPrice = view.findViewById(R.id.heroCoinPrice);

        progressBar = view.findViewById(R.id.progressBar);
        errorLayout = view.findViewById(R.id.errorLayout);
        emptyText = view.findViewById(R.id.emptyText);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(null);
        adapter = new CoinAdapter(this::navigateToDetail);
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.openWatchlistButton).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_marketFragment_to_watchlistFragment)
        );

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(viewModel::refreshCoins);
        view.findViewById(R.id.retryButton)
                .setOnClickListener(v -> viewModel.refreshCoins());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleSearch(newText);
                return true;
            }
        });

        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            stopHero();
            showLoading();
            searchView.setQuery("", false);
            searchView.clearFocus();

            if (checkedId == R.id.chipTop) {
                viewModel.loadTopCoins();
            } else if (checkedId == R.id.chipTrending) {
                viewModel.loadTrendingCoins();
            } else if (checkedId == R.id.chipGainers) {
                viewModel.loadGainers();
            } else if (checkedId == R.id.chipLosers) {
                viewModel.loadLosers();
            }
        });

        observeCoins();
        showLoading();
        viewModel.refreshCoins();
    }

    private void handleSearch(String query) {
        stopHero();
        if (query == null || query.trim().isEmpty()) {
            viewModel.loadTopCoins();
        } else {
            viewModel.searchCoins(query.trim());
        }
    }

    private void observeCoins() {
        viewModel.getAllCoins().observe(getViewLifecycleOwner(), coins -> {
            swipeRefreshLayout.setRefreshing(false);

            if (coins == null) {
                showError();
                return;
            }

            if (coins.isEmpty()) {
                showEmpty();
                return;
            }

            showList();
            adapter.submitList(new ArrayList<>(coins));
            setupHeroCoins(coins);
            startHero();
        });
    }

    private void setupHeroCoins(List<Coin> coins) {
        heroCoins.clear();
        heroIndex = 0;

        int count = Math.min(5, coins.size());
        for (int i = 0; i < count; i++) {
            Coin c = coins.get(i);
            heroCoins.add(new HeroCoin(
                    c.name,
                    String.format(Locale.US, "$%,.2f", c.currentPrice),
                    c.image
            ));
        }
    }

    private void startHero() {
        if (heroCoins.isEmpty()) return;
        heroRunning = true;
        updateHero();
        heroCoinImage.postDelayed(heroRunnable, 5000);
    }

    private void stopHero() {
        heroRunning = false;
        heroCoinImage.removeCallbacks(heroRunnable);
    }

    private void updateHero() {
        HeroCoin coin = heroCoins.get(heroIndex);
        heroCoinName.setText(coin.name);
        heroCoinPrice.setText(coin.priceText);

        Glide.with(this)
                .load(coin.imageUrl)
                .placeholder(R.drawable.ic_coin_placeholder)
                .into(heroCoinImage);
    }

    private void navigateToDetail(Coin coin) {
        if (!isAdded()) return;

        NavController navController = NavHostFragment.findNavController(this);
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.marketFragment) {

            Bundle bundle = new Bundle();
            bundle.putString("coin_id", coin.id);
            navController.navigate(
                    R.id.action_marketFragment_to_coinDetailFragment,
                    bundle
            );
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        stopHero();
        super.onDestroyView();
    }

}
