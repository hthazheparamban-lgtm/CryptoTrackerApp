package com.example.cryptotrackerapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.model.WatchlistCoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchlistAdapter
        extends RecyclerView.Adapter<WatchlistAdapter.VH> {

    private final List<WatchlistCoin> items = new ArrayList<>();

    // ---------------- DATA ----------------

    public void submit(List<WatchlistCoin> coins) {
        items.clear();
        items.addAll(coins);
        notifyDataSetChanged();
    }

    public WatchlistCoin getItem(int position) {
        return items.get(position);
    }

    public List<WatchlistCoin> getItems() {
        return new ArrayList<>(items);
    }

    public void swap(int from, int to) {
        Collections.swap(items, from, to);
        notifyItemMoved(from, to);
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist_coin, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int position
    ) {
        WatchlistCoin c = items.get(position);

        h.name.setText(c.name);
        h.symbol.setText(c.symbol);
        h.price.setText("$" + c.price);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    static class VH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name, symbol, price;

        VH(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.coinImage);
            name = v.findViewById(R.id.coinName);
            symbol = v.findViewById(R.id.coinSymbol);
            price = v.findViewById(R.id.coinPrice);
        }
    }
}

