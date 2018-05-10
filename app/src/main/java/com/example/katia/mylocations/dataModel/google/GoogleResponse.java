package com.example.katia.mylocations.dataModel.google;

/**
 * Created by jbt on 12/12/2016.
 */

public class GoogleResponse {
    private StatusEnum status;
    private GooglePlace[] results;
    private GooglePlace result;
    private String next_page_token;
    private String error_message;

    public GooglePlace getResult() {
        return result;
    }

    public void setResult(GooglePlace result) {
        this.result = result;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public GooglePlace[] getResults() {
        return results;
    }

    public void setResults(GooglePlace[] results) {
        this.results = results;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public enum StatusEnum{
        OK,
        ZERO_RESULTS,
        OVER_QUERY_LIMIT,
        REQUEST_DENIED,
        INVALID_REQUEST;
    }
}
