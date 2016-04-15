package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        final ListView listViewForecast = (ListView) findViewById(R.id.listview_forecast);
        if (listViewForecast != null) {
            listViewForecast.setAdapter(arrayAdapter);
            listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemText = arrayAdapter.getItem(position);
                    startActivity(new Intent(getApplicationContext(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, itemText));
                }
            });
        }

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        addDrawerItems();
        setupDrawer();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mActivityTitle.equals("") ? getLocation() : mActivityTitle);
        }
        new ExternalServer(arrayAdapter).execute(getLocation());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDrawerItems() {
        String[] osArray = { "Actualizare...", "Vremea Timisoara", "Vremea Bucuresti", "Setari"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        new ExternalServer(arrayAdapter).execute(getLocation());
                        break;
                    case 1:
                        new ExternalServer(arrayAdapter).execute("Timisoara");
                        mActivityTitle = "Timisoara";
                        setLocation(mActivityTitle);
                        break;
                    case 2:
                        new ExternalServer(arrayAdapter).execute("Bucuresti");
                        mActivityTitle = "Bucuresti";
                        setLocation(mActivityTitle);
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
            }
        });
    }

    private String getLocation(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
    }

    private void setLocation(String location){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("location", location);
        editor.apply();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Setari");
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mActivityTitle.equals("") ? getLocation() : mActivityTitle);
                }
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

}
