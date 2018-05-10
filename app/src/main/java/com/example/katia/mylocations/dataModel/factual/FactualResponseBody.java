package com.example.katia.mylocations.dataModel.factual;

import com.google.gson.annotations.SerializedName;

/**
 * Created by katia on 03/12/2016.
 */
public class FactualResponseBody {
    public FactualPlace[] data;
    @SerializedName("included_rows")
    public int includedRows;

}
