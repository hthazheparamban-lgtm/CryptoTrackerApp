package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

public class CoinDetailFragment extends Fragment {

    private CoinDetailViewModel coinDetailViewModel;
    private WatchlistViewModel watchlistViewModel;

    private CircularProgressIndicator progressBar;
    private ImageView heroCoinImage;
    private TextView heroCoinPrice;
    private TextView coinMarketCap;
    private TextView coinVolume;
    private TextView coinDescription;
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


        progressBar = view.findViewById(R.id.progressBar);
        heroCoinImage = view.findViewById(R.id.heroCoinImage);
        heroCoinPrice = view.findViewById(R.id.heroCoinPrice);
        coinMarketCap = view.findViewById(R.id.coinMarketCap);
        coinVolume = view.findViewById(R.id.coinVolume);
        coinDescription = view.findViewById(R.id.coinDescription);
        addToWatchlistBtn = view.findViewById(R.id.addToWatchlistBtn);

        coinDetailViewModel = new ViewModelProvider(this)
                .get(CoinDetailViewModel.class);

        watchlistViewModel = new ViewModelProvider(this)
                .get(WatchlistViewModel.class);


        if (getArguments() != null) {
            coinId = getArguments().getString("coin_id");
        }

        if (coinId == null) {
            Toast.makeText(requireContext(), "Invalid coin", Toast.LENGTH_SHORT).show();
            return;
        }

        observeCoinDetail();


        progressBar.setVisibility(View.VISIBLE);

        coinDetailViewModel.loadCoinDetail(coinId);
    }

    private void observeCoinDetail() {
        coinDetailViewModel.getCoinDetail()
                .observe(getViewLifecycleOwner(), coin -> {

                    if (coin == null || !isAdded()) return;

                    progressBar.setVisibility(View.GONE);

                    heroCoinPrice.setText(
                            String.format(
                                    Locale.US,
                                    "$%,.2f",
                                    coin.marketData.currentPrice.usd
                            )
                    );


                    coinMarketCap.setText(
                            String.format(
                                    Locale.US,
                                    "Market Cap: $%,.0f",
                                    coin.marketData.marketCap.usd
                            )
                    );

                    if (coin.description != null && coin.description.en != null &&
                            !coin.description.en.isEmpty()) {
                        coinDescription.setText(
                                android.text.Html.fromHtml(
                                        coin.description.en,
                                        android.text.Html.FROM_HTML_MODE_LEGACY
                                )
                        );
                    } else {
                        coinDescription.setText("No description available.");
                    }


                    Glide.with(this)
                            .load(coin.image.large)
                            .placeholder(R.drawable.ic_coin_placeholder)
                            .into(heroCoinImage);


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
                });
    }
}
