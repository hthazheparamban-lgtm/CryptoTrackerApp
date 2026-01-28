package com.example.cryptotrackerapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.model.Coin;

import java.util.Locale;

public class CoinAdapter extends ListAdapter<Coin, CoinAdapter.CoinViewHolder> {

    public interface OnCoinClickListener {
        void onCoinClick(Coin coin);
    }

    private final OnCoinClickListener listener;

    public CoinAdapter(OnCoinClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Coin> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Coin>() {
                @Override
                public boolean areItemsTheSame(@NonNull Coin oldItem, @NonNull Coin newItem) {
                    return oldItem.id.equals(newItem.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Coin oldItem, @NonNull Coin newItem) {
                    return oldItem.currentPrice == newItem.currentPrice &&
                            oldItem.priceChangePercentage24h == newItem.priceChangePercentage24h;
                }
            };

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        Coin coin = getItem(position);

        holder.name.setText(
                coin.name + " (" + coin.symbol.toUpperCase(Locale.US) + ")"
        );

        holder.price.setText(
                String.format(Locale.US, "$%,.2f", coin.currentPrice)
        );

        holder.change.setText(
                String.format(Locale.US, "%.2f%%", coin.priceChangePercentage24h)
        );

        int color = coin.priceChangePercentage24h >= 0
                ? R.color.green
                : R.color.red;
        holder.change.setTextColor(
                holder.itemView.getContext().getColor(color)
        );

        Glide.with(holder.itemView)
                .load(coin.image)
                .placeholder(R.drawable.ic_coin_placeholder)
                .into(holder.icon);


        holder.itemView.setOnClickListener(v -> {
            v.setEnabled(false);
            listener.onCoinClick(coin);
            v.postDelayed(() -> v.setEnabled(true), 800);
        });
    }

    static class CoinViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name, price, change;

        CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.coinImage);
            name = itemView.findViewById(R.id.coinName);
            price = itemView.findViewById(R.id.coinPrice);
            change = itemView.findViewById(R.id.coinChange);
        }
    }
}
