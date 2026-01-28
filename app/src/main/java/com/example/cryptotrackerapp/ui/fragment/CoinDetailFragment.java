package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

public class CoinDetailFragment extends Fragment {

    private CoinDetailViewModel coinDetailViewModel;
    private WatchlistViewModel watchlistViewModel;

    private CircularProgressIndicator progressBar;
    private ImageView coinLogo;
    private TextView coinPrice, coinMarketCap, coinVolume, coinDescription;
    private CollapsingToolbarLayout collapsingToolbar;
    private Button addToWatchlistBtn;

    private String coinId;

    public CoinDetailFragment() {
        super(R.layout.fragment_coin_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progressBar);
        coinLogo = view.findViewById(R.id.coinLogo);
        coinPrice = view.findViewById(R.id.coinPrice);
        coinMarketCap = view.findViewById(R.id.coinMarketCap);
        coinVolume = view.findViewById(R.id.coinVolume);
        coinDescription = view.findViewById(R.id.coinDescription);
        collapsingToolbar = view.findViewById(R.id.collapsingToolbar);
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

        progressBar.setVisibility(View.VISIBLE);
        observeCoinDetail();
        coinDetailViewModel.loadCoinDetail(coinId);
    }

    private void observeCoinDetail() {
        coinDetailViewModel.getCoinDetail()
                .observe(getViewLifecycleOwner(), coin -> {

                    if (coin == null || !isAdded()) return;

                    progressBar.setVisibility(View.GONE);

                    collapsingToolbar.setTitle(coin.name);

                    Glide.with(this)
                            .load(coin.image.large)
                            .placeholder(R.drawable.ic_coin_placeholder)
                            .into(coinLogo);

                    coinPrice.setText(String.format(
                            Locale.US, "$%,.2f",
                            coin.marketData.currentPrice.usd
                    ));

                    coinMarketCap.setText(String.format(
                            Locale.US, "Market Cap: $%,.0f",
                            coin.marketData.marketCap.usd
                    ));


                    coinDescription.setText(
                            coin.description != null && coin.description.en != null
                                    ? Html.fromHtml(coin.description.en, Html.FROM_HTML_MODE_LEGACY)
                                    : "No description available"
                    );

                    addToWatchlistBtn.setOnClickListener(v -> {
                        watchlistViewModel.add(new WatchlistCoin(
                                coin.id,
                                coin.name,
                                coin.symbol,
                                coin.marketData.currentPrice.usd,
                                coin.image.small,
                                (int) System.currentTimeMillis()
                        ));
                        Toast.makeText(requireContext(),
                                "Added to Watchlist", Toast.LENGTH_SHORT).show();
                    });
                });
    }
}
