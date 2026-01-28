package com.example.cryptotrackerapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptotrackerapp.R;
import com.example.cryptotrackerapp.data.model.WatchlistCoin;
import com.example.cryptotrackerapp.ui.adapter.WatchlistAdapter;
import com.example.cryptotrackerapp.ui.viewmodel.WatchlistViewModel;

public class WatchlistFragment extends Fragment {

    private WatchlistViewModel viewModel;
    private WatchlistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        RecyclerView recyclerView =
                view.findViewById(R.id.watchlistRecyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new WatchlistAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this)
                .get(WatchlistViewModel.class);

        viewModel.getWatchlist().observe(
                getViewLifecycleOwner(),
                coins -> adapter.submit(coins)
        );


        ItemTouchHelper helper =
                new ItemTouchHelper(touchCallback);

        helper.attachToRecyclerView(recyclerView);
    }

    private final ItemTouchHelper.SimpleCallback touchCallback =
            new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            ) {

                @Override
                public boolean onMove(
                        @NonNull RecyclerView rv,
                        @NonNull RecyclerView.ViewHolder from,
                        @NonNull RecyclerView.ViewHolder to
                ) {
                    adapter.swap(
                            from.getAdapterPosition(),
                            to.getAdapterPosition()
                    );

                    viewModel.updateOrder(adapter.getItems());
                    return true;
                }

                @Override
                public void onSwiped(
                        @NonNull RecyclerView.ViewHolder vh,
                        int direction
                ) {
                    WatchlistCoin coin =
                            adapter.getItem(vh.getAdapterPosition());

                    if (direction == ItemTouchHelper.LEFT) {

                        viewModel.remove(coin);
                    } else {

                        adapter.notifyItemChanged(vh.getAdapterPosition());
                    }

                }
            };
}
