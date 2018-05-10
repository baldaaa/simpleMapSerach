package com.example.katia.mylocations.dataModel.factual;

/**
 * Created by jbt on 12/11/2016.
 * geo={"$circle":{"$center":[34.06021,-118.41828],"$meters": 5000}}
 */

public class FactualGeo {

    private FactualCircle $circle;

    public FactualGeo(FactualCircle $circle) {
        this.$circle = $circle;
    }

    public FactualCircle get$circle() {
        return $circle;
    }
}
