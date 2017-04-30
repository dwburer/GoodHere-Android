package com.danielburer.goodhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup app ID and secret for communication with Django
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.server_api_url), "http://1ec42240.ngrok.io/api/v1/");
        editor.putString(getString(R.string.client_id_key), "bEgw6lU7JwWKsqpR947DXocYVtxer57VIC5WwwDi");
        editor.putString(getString(R.string.client_secret_key), "9zG0IlrEEXIdo0YBp6otvaM8ZJqWQ3gYf4Xc6eg4z2GKPjS9HdSGP0c0xXcQK895L3mKrGmd1L3y7ZPflQiSnEk2dlUxdl63yV9CNaya2kKGp78FYvyFchIFVZOFEs8t");
        editor.putBoolean(getString(R.string.client_authenticated_key), false);
        editor.apply();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main_tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_tab_search));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_tab_nearby));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_tab_profile));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        adapter.setContext(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Figure out what this does????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//      getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Figure out what this does????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}