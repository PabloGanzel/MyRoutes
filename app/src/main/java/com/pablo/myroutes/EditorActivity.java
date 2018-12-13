package com.pablo.myroutes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras().getString("type").equals("RoutingDaysListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutingDaysListFragment.newInstance(), "RoutingDaysListFragment").commit();
        } else if (getIntent().getExtras().getString("type").equals("RoutesListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, RoutesListFragment.newInstance(-1), "RoutesListFragment").commit();
        } else if (getIntent().getExtras().getString("type").equals("AddressListFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, AddressListFragment.newInstance(), "AddressListFragment").commit();
        }
    }

}
