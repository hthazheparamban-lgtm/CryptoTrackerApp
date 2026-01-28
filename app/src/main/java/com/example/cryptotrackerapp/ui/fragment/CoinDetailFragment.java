package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.api.CoinGeckoApi;
import com.example.cryptotrackerapp.data.model.CoinDetail;
import com.example.cryptotrackerapp.data.api.RetrofitClient;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinDetailFragment extends Fragment {

    private ImageView coinImage;
    private TextView coinPrice, coinMarketCap, coinVolume, coinDescription;
    private CollapsingToolbarLayout collapsingToolbar;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        coinImage = view.findViewById(R.id.heroCoinImage);
        coinPrice = view.findViewById(R.id.heroCoinPrice);

        coinMarketCap = view.findViewById(R.id.coinMarketCap);
        coinVolume = view.findViewById(R.id.coinVolume);
        coinDescription = view.findViewById(R.id.coinDescription);
        collapsingToolbar = view.findViewById(R.id.collapsingToolbar);


        String coinId = getArguments() != null
                ? getArguments().getString("coin_id")
                : null;

        if (coinId == null) {
            Log.e("CoinDetail", "coin_id is NULL");
            return;
        }


        coinPrice.setText("Loading...");
        coinMarketCap.setText("Market Cap: --");
        coinVolume.setText("24h Volume: --");
        coinDescription.setText("Loading description...");

        fetchCoinDetail(coinId);
    }

    private void fetchCoinDetail(String coinId) {
        CoinGeckoApi api = RetrofitClient
                .getClient()
                .create(CoinGeckoApi.class);

        api.getCoinDetail(coinId).enqueue(new Callback<CoinDetail>() {
            @Override
            public void onResponse(
                    @NonNull Call<CoinDetail> call,
                    @NonNull Response<CoinDetail> response
            ) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("CoinDetail", "API response failed");
                    return;
                }

                CoinDetail coin = response.body();


                if (collapsingToolbar != null) {
                    collapsingToolbar.setTitle(coin.name);
                }


                coinPrice.setText(
                        "$" + String.format("%,.2f",
                                coin.marketData.currentPrice.usd)
                );


                coinMarketCap.setText(
                        "Market Cap: $" + String.format("%,.0f",
                                coin.marketData.marketCap.usd)
                );


                coinVolume.setText(
                        "24h Volume: $" + String.format("%,.0f",
                                coin.marketData.volume.usd)
                );

                if (coin.description != null && coin.description.en != null) {
                    coinDescription.setText(
                            Html.fromHtml(
                                    coin.description.en,
                                    Html.FROM_HTML_MODE_LEGACY
                            )
                    );
                }

                Glide.with(requireContext())
                        .load(coin.image.large)
                        .placeholder(R.drawable.ic_coin_placeholder)
                        .error(R.drawable.ic_coin_placeholder)
                        .into(coinImage);
            }

            @Override
            public void onFailure(
                    @NonNull Call<CoinDetail> call,
                    @NonNull Throwable t
            ) {
                Log.e("CoinDetail", "API error", t);
            }
        });
    }
}
