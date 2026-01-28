package com.example.cryptotrackerapp.ui.adapter;

import com.robinhood.spark.SparkAdapter;
import java.util.List;

public class CoinSparkAdapter extends SparkAdapter {

    private final List<Float> data;

    public CoinSparkAdapter(List<Float> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int index) {
        return data.get(index);
    }

    @Override
    public float getY(int index) {
        return data.get(index);
    }
}
