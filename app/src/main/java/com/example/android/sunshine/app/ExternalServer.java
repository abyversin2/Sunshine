package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by Albertv on 4/12/2016.
 */

public class ExternalServer extends AsyncTask<String, Void, String[]> {

    private ArrayAdapter<String> arrayAdapter;

    public ExternalServer(ArrayAdapter<String> arrayAdapter){
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonRecived = null;
        try {
            final String WEATHER_SERVER = "http://api.openweathermap.org/data/2.5/forecast/daily";
            final String API_ID = "b19f892d7399d14e2f622cce286bd577";
            final String KEY_PARAMETER = "appid";
            Uri buildUri = Uri.parse(WEATHER_SERVER).buildUpon()
                    .appendQueryParameter(KEY_PARAMETER, API_ID)
                    .appendQueryParameter("q", params[0])
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", "7")
                    .build();
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // empty in pula mea
                return null;
            }
            jsonRecived = buffer.toString();
        } catch (IOException e) {
            Log.e("ExternalServer", "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ExternalServer", "Error closing stream", e);
                }
            }
        }
        return this.getWeatherDataFromJson(jsonRecived, 7);
    }

    @Override
    protected void onPostExecute(String[] results){
        if(results != null){
            arrayAdapter.clear();
            for (String value : results){
                arrayAdapter.add(value);
            }
        }
    }


    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) {
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        final String OWM_CITY = "city";

        JSONObject forecastJson = null;
        String[] resultStrs = new String[numDays];
        try {
            forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = null;
            weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject city = forecastJson.getJSONObject(OWM_CITY);

            Time dayTime = new Time();
            dayTime.setToNow();
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            dayTime = new Time();
            for (int i = 0; i < (weatherArray != null ? weatherArray.length() : 0); i++) {
                String day;
                String description;
                String highAndLow;
                JSONObject dayForecast = weatherArray.getJSONObject(i);
                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = city.getString("name") + " - " + day + " - " + description + " - " + highAndLow;
            }
        } catch (JSONException e) {
            Log.e("ExternalServer", "Parse error ", e);
        }
        return resultStrs;
    }

    private String getReadableDateString(long time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        return "max. temp. " + roundedHigh + " / " + "min. temp. " + roundedLow;
    }
}

