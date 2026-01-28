package com.example.cryptotrackerapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Sparkline {

    @SerializedName("price")
    public List<Float> price;
}
