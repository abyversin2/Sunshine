package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        ListView listViewForecast = (ListView) findViewById(R.id.listview_forecast);
        if (listViewForecast != null) {
            listViewForecast.setAdapter(arrayAdapter);
        }
    }

    public void refreshButtonHandler(View view) throws ExecutionException, InterruptedException {
        new ExternalServer(arrayAdapter).execute("Timisoara");
    }

}
