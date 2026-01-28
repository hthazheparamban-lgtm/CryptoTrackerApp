package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.model.WatchlistCoin;
import com.example.cryptotrackerapp.ui.viewmodel.CoinDetailViewModel;
import com.example.cryptotrackerapp.ui.viewmodel.WatchlistViewModel;

import java.util.Locale;

public class CoinDetailFragment extends Fragment {

    private CoinDetailViewModel coinDetailViewModel;
    private WatchlistViewModel watchlistViewModel;

    private ImageView coinImage;
    private TextView coinName;
    private TextView coinSymbol;
    private TextView coinPrice;
    private TextView coinChange;
    private ProgressBar progressBar;
    private Button addToWatchlistBtn;

    private String coinId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_coin_detail, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        coinImage = view.findViewById(R.id.coinImage);
        coinName = view.findViewById(R.id.coinName);
        coinSymbol = view.findViewById(R.id.coinSymbol);
        coinPrice = view.findViewById(R.id.coinPrice);
        coinChange = view.findViewById(R.id.coinChange);
        progressBar = view.findViewById(R.id.progressBar);
        addToWatchlistBtn = view.findViewById(R.id.addToWatchlistBtn);

        coinDetailViewModel =
                new ViewModelProvider(this).get(CoinDetailViewModel.class);

        watchlistViewModel =
                new ViewModelProvider(this).get(WatchlistViewModel.class);

        if (getArguments() != null) {
            coinId = getArguments().getString("coin_id");
        }

        if (coinId == null) {
            Toast.makeText(requireContext(), "Invalid coin", Toast.LENGTH_SHORT).show();
            return;
        }

        observeCoinDetail();
        coinDetailViewModel.loadCoinDetail(coinId);
    }

    private void observeCoinDetail() {
        coinDetailViewModel.getCoinDetail().observe(
                getViewLifecycleOwner(),
                coin -> {
                    if (coin == null) return;

                    progressBar.setVisibility(View.GONE);

                    coinName.setText(coin.name);
                    coinSymbol.setText(coin.symbol.toUpperCase(Locale.US));

                    coinPrice.setText(
                            String.format(
                                    Locale.US,
                                    "$%,.2f",
                                    coin.marketData.currentPrice.usd
                            )
                    );

                    coinChange.setText(
                            String.format(
                                    Locale.US,
                                    "%.2f%%",
                                    coin.marketData.priceChangePercentage24h
                            )
                    );

                    int color = coin.marketData.priceChangePercentage24h >= 0
                            ? R.color.green
                            : R.color.red;

                    coinChange.setTextColor(requireContext().getColor(color));

                    Glide.with(this)
                            .load(coin.image.large)
                            .into(coinImage);

                    addToWatchlistBtn.setOnClickListener(v -> {
                        WatchlistCoin watchlistCoin = new WatchlistCoin(
                                coin.id,
                                coin.name,
                                coin.symbol,
                                coin.marketData.currentPrice.usd,
                                coin.image.small,
                                (int) System.currentTimeMillis()
                        );

                        watchlistViewModel.add(watchlistCoin);
                        Toast.makeText(
                                requireContext(),
                                "Added to Watchlist",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }
        );
    }
}
