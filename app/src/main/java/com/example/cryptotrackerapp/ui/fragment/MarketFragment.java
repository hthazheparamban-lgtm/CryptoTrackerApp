package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    private final List<HeroCoin> heroCoins = new ArrayList<>();
    private int heroIndex = 0;
    private boolean heroInitialized = false;

    private View heroView;


    private final Runnable heroRunnable = new Runnable() {
        @Override
        public void run() {
            rotateHero();
            if (heroView != null) {
                heroView.postDelayed(this, 5000);
            }
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
        heroView = heroCoinImage;

        progressBar = view.findViewById(R.id.progressBar);
        errorLayout = view.findViewById(R.id.errorLayout);
        emptyText = view.findViewById(R.id.emptyText);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        view.findViewById(R.id.retryButton)
                .setOnClickListener(v -> viewModel.refreshCoins());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(null); // performance
        adapter = new CoinAdapter(this::navigateToDetail);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshCoins());

        observeCoins();

        showLoading();
        viewModel.refreshCoins();
    }


    private void navigateToDetail(Coin coin) {
        if (!isAdded()) return;

        NavController navController =
                NavHostFragment.findNavController(this);

        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId()
                        == R.id.marketFragment) {

            Bundle bundle = new Bundle();
            bundle.putString("coin_id", coin.id);

            navController.navigate(
                    R.id.action_marketFragment_to_coinDetailFragment,
                    bundle
            );
        }
    }

    private void observeCoins() {
        viewModel.getAllCoins()
                .observe(getViewLifecycleOwner(), coins -> {

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
                    adapter.submitList(coins);

                    if (!heroInitialized) {
                        setupHeroCoins(coins);
                        updateHero();
                        heroView.postDelayed(heroRunnable, 5000);
                        heroInitialized = true;
                    }
                });
    }

    private void setupHeroCoins(List<Coin> coins) {
        heroCoins.clear();
        heroIndex = 0;

        int count = Math.min(5, coins.size());
        for (int i = 0; i < count; i++) {
            Coin c = coins.get(i);
            heroCoins.add(
                    new HeroCoin(
                            c.name,
                            String.format(
                                    Locale.US,
                                    "$%,.2f",
                                    c.currentPrice
                            ),
                            c.image
                    )
            );
        }
    }

    private void rotateHero() {
        if (heroCoins.isEmpty()) return;
        heroIndex = (heroIndex + 1) % heroCoins.size();
        updateHero();
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
        super.onDestroyView();
        heroInitialized = false;
        if (heroView != null) {
            heroView.removeCallbacks(heroRunnable);
        }
    }
}
