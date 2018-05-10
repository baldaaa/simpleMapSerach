package com.example.katia.mylocations.tasks;

import android.os.AsyncTask;

import com.example.katia.mylocations.dataModel.factual.FactualCredentials;
import com.example.katia.mylocations.dataModel.factual.FactualGeo;
import com.example.katia.mylocations.dataModel.factual.FactualResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jbt on 12/12/2016.
 */

public class FactualSearchPlaceTask extends AsyncTask<Object, Void, FactualResponse> {

        static final String stringUrlGeo = "http://api.v3.factual.com/t/places?KEY=%s&geo=%s";
        static final String stringUrlFullText = "http://api.v3.factual.com/t/places?KEY=%s&q=%";

        @Override
        protected FactualResponse doInBackground(Object... params) {

            FactualResponse response = null;
            HttpURLConnection connection = null;
            BufferedReader reader;
            StringBuilder builder;
            String jsonGeo = "";

            Gson gson = new GsonBuilder().disableInnerClassSerialization().setPrettyPrinting().create();
            StringBuilder places = new StringBuilder();
            for (Object param : params) {
                if(param instanceof String) {
                    places.append(((String) param).replaceAll(" ", "%20"));
                    places.append(",");
                }else if(param instanceof FactualGeo){
                    jsonGeo = gson.toJson(param);
                }
            }

            try {
                URL url = new URL(String.format(stringUrlGeo,
                        new String[]{FactualCredentials.getAuthKey(),jsonGeo}));

                connection = (HttpURLConnection) url.openConnection();
                if (((int) (connection.getResponseCode() / 100)) != 2) {
                    return new FactualResponse(FactualResponse.StatusEnum.error, "Conection", connection.getResponseMessage());
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                builder = new StringBuilder();

                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }

                response = gson.fromJson(builder.toString(), FactualResponse.class);

            } catch (MalformedURLException e) {
                response = new FactualResponse(FactualResponse.StatusEnum.error, e.getMessage());
            } catch (IOException e) {
                response = new FactualResponse(FactualResponse.StatusEnum.error, e.getMessage());
            } catch (NumberFormatException e) {
                response = new FactualResponse(FactualResponse.StatusEnum.error, e.getMessage());
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return response;
        }

        @Override
        protected void onPostExecute(FactualResponse factualResponse) {
            //adapter.updateItems(factualResponse.response.data);
            //adapter.notifyDataSetChanged();
        }

}
