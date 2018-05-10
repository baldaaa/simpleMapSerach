package com.example.katia.mylocations.dataModel.factual;

/**
 * Created by jbt on 12/11/2016.
 *  {"$center":[34.06021,-118.41828],"$meters": 5000}
 */
public class FactualCircle {

    private double[] $center;
    private int $meters;

    public FactualCircle(double[] $center, int $meters) {
        this.$center = $center;
        this.$meters = $meters;
    }

    public double[] get$center() {
        return $center;
    }

    public int get$meters() {
        return $meters;
    }
}
