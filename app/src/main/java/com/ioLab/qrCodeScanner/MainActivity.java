package com.ioLab.qrCodeScanner;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        HistoryFragment.OnHistoryChangedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    android.support.v4.app.FragmentManager mFragmentManager;
    android.support.v4.app.FragmentTransaction mFragmentTransaction;
    final String LOG_TAG = "ioLabLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup the DrawerLayout and NavigationView
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view) ;

        //Inflating the TabFragment as the first Fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(this);

// another variant with listener
//           mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                mDrawerLayout.closeDrawers();
//                if (menuItem.getItemId() == R.id.nav_item_sent) {
//                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.containerView, new SentFragment()).commit();
//                }
//                return false;
//            }
//        });

         //Setup Drawer Toggle of the Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        Log.d(LOG_TAG, "MainActivity onCreate");
    }

    //ToDo Setup click events on the Navigation Drawer View Items.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_camara) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHistoryChange() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // get link to HistoryFgagment by ID
        TabFragment tabFragment = (TabFragment) fragmentManager
                .findFragmentById(R.id.containerView);
        HistoryFragment historyFragment = (HistoryFragment) tabFragment.getChildFragmentManager()
                .findFragmentById(R.id.viewpager);
        // Refreshing listView with changed data
        historyFragment.refreshHistoryList();
    }
}
