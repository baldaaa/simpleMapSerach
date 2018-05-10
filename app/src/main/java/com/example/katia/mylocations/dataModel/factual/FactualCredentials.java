package com.example.katia.mylocations.dataModel.factual;

/**
 * Created by katia on 03/12/2016.
 */

public class FactualCredentials {
    private static final String authKey = "0zXeDKXw3a9HWowL8rTEff8ciuf5oA8UQQcdUQkP";
    private static final String authSecret = "zlFBiRjSNrfFn0yqWOoCUTPUfruKFdKJ3lYCrea1";
    private static final String authDomain = "*.factual.com";

    public static String getAuthKey() {
        return authKey;
    }

    public static String getAuthSecret() {
        return authSecret;
    }

    public static String getAuthDomain() {
        return authDomain;
    }
}
