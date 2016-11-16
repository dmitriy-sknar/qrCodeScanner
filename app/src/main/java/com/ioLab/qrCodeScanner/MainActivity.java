package com.ioLab.qrCodeScanner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.ioLab.qrCodeScanner.fragment.AppFragmentPagerAdapter;
import com.ioLab.qrCodeScanner.utils.History;
import com.ioLab.qrCodeScanner.utils.HistoryChangeEvent;
import com.ioLab.qrCodeScanner.utils.MyQRCode;
import com.ioLab.qrCodeScanner.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    final String LOG_TAG = "ioLabLog";
    public final static String POSITION = "POSITION";

    private History history;
    private List<MyQRCode> myQRCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initTabs();
        initNavView();

        if(savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        }

        Log.d(LOG_TAG, "MainActivity onCreate");
    }

    private void initNavView() {
        //Setup Drawer Toggle Button on the Toolbar to open NavDrayer with "sandwich" button
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //listener for menu item click
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initTabs() {
        //         Set an Apater for the View Pager
        viewPager.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(), this));
        //Todo verify work without runnable
//         Dose not work without the runnable.
//         Maybe a Support Library Bug.
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        //Inflating the TabFragment as the first Fragment
//        mFragmentManager = getSupportFragmentManager();
//        mFragmentTransaction = mFragmentManager.beginTransaction();
//        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
    }

    private void initViews() {
        //Setup the DrawerLayout, NavigationView, Toolbar...
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    //ToDo Setup click events on the Navigation Drawer View Items.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks.
//        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerLayout.closeDrawers();

        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        }
        else if (id == R.id.nav_share_last_code) {
            history = new History(this);
            myQRCodes = history.getAllCodesFromDB();
            MyQRCode qrCode = myQRCodes.get(myQRCodes.size()-1);

            Date dateOfScanning = qrCode.getDateOfScanning();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm",
                    this.getResources().getConfiguration().locale);
            String date = dateFormat.format(dateOfScanning);

            Utils.shareCode(this,
                    qrCode.getName(),
                    qrCode.getCodeType(),
                    date);
        }
        else if (id == R.id.nav_history) {
            viewPager.setCurrentItem(1);
        }
        else if (id == R.id.nav_clear_history) {
            History history = new History(getApplicationContext());
            List<MyQRCode> list =  history.getAllCodesFromDB();
            for(MyQRCode code : list){
                Utils.delete(code.getPath());
            }
            history.clearDB();
            EventBus.getDefault().postSticky(new HistoryChangeEvent());

            String historyCleared = getResources().getString(R.string.history_cleared);
            Snackbar.make(mDrawerLayout, historyCleared, Snackbar.LENGTH_LONG).show();
        }
//        else if (id == R.id.nav_share) {
//
//        }
//        else if (id == R.id.nav_send) {
//
//        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

}
