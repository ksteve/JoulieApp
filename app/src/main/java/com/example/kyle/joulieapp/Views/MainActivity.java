//Names: Amshar Basheer, Grigory Kozyrev, Kyle Stevenson
//Project Name: JoulieApp
//File Name: MainActivity.java
//Date: 2016-12-06
//Description: This is the main activity for the app. It contains the code for managing the tabs / fragments.

package com.example.kyle.joulieapp.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.example.kyle.joulieapp.Contracts.MainContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Presenters.MainPresenter;
import com.example.kyle.joulieapp.Utils.CredentialsManager;
import com.example.kyle.joulieapp.Utils.Tools;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnPageChangeListener,
        DeviceFragment.OnDeviceFragmentInteractionListener,
        UsageOverviewFragment.OnFragmentInteractionListener,
        RuleFragment.OnListFragmentInteractionListener,
        MainContract.View {

    private FragmentPagerAdapter adapterViewPager;
    private MainContract.Presenter mainPresenter;
    private ViewPager vpPager;
    private Toolbar toolbar;
    private TextView connection;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private View coordinator;
    private BroadcastReceiver mConnectivityChangedReceiver;

    private static final int MYUSAGE_FRAGMENT = 0;
    private static final int MYRULES_FRAGMENT = 1;
    private static final int MYDEVICES_FRAGMENT = 2;

    //Method Name: onCreate
    //Parameters: Bundle savedInstanceState
    //Return: void
    //Description: sets up the layout for the tabs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 2016-12-04 check if authenticated

        //setup token for firebase cloud messaging
       // String token = FirebaseInstanceId.getInstance().getToken();
        mainPresenter = new MainPresenter(this, PreferenceManager.getDefaultSharedPreferences(this), this);
     //   mainPresenter.testConnection();
       // registerNetworkChanges();

        coordinator = findViewById(R.id.coordinator);
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connection = (TextView) findViewById(R.id.connection);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (vpPager.getCurrentItem()){

                    case MYUSAGE_FRAGMENT:
                        //updateUsageData();
                        break;
                    case MYRULES_FRAGMENT:
                        Intent newRuleIntent = new Intent(MainActivity.this, NewRuleActivity.class);
                        startActivityForResult(newRuleIntent, MYRULES_FRAGMENT);
                        break;
                    case MYDEVICES_FRAGMENT:
                        Intent newDeviceIntent = new Intent(MainActivity.this, NewDeviceActivity.class);
                        startActivityForResult(newDeviceIntent, MYDEVICES_FRAGMENT);
                        break;
                }
            }
        });
        toggleFab(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set profile picture
        CircleImageView profilePic = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_pic);
        HashMap<String, String> userProfile = CredentialsManager.getUserProfile(getApplicationContext());
        new Tools.DownloadImageTask(profilePic).execute(userProfile.get(getString(R.string.user_picture)));

       // profilePic.setImageDrawable(Tools.LoadImageFromWebOperations(userProfile.get(getString(R.string.user_picture))));

        //set email
        TextView profileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profile_email);
        profileEmail.setText(userProfile.get(getString(R.string.user_email)));

        //setup view pager
        setupViewPager();

        //setup Tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vpPager);
        setupTabIcons();
        registerNetworkChanges();
    }

    //Method Name: setupViewPager
    //Parameters: void
    //Return: void
    //Description: sets up the view pager
    private void setupViewPager(){
        vpPager = (ViewPager) findViewById(R.id.main_viewpager);
        vpPager.setPageMargin(50);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(MainActivity.this);
        vpPager.setOffscreenPageLimit(3);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(adapterViewPager.getPageTitle(vpPager.getCurrentItem()));
    }

    //Method Name: setupTabIcons
    //Parameters: void
    //Return: void
    //Description: sets up the tab icons
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_trending_up_24dp);
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_access_alarm_24dp);
        tabLayout.getTabAt(1).setText("");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_power_24dp);
        tabLayout.getTabAt(2).setText("");
    }

    private void registerNetworkChanges(){
        // TODO: 2017-04-08 maybe move this to presenter
        mConnectivityChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // int status = NetworkUtil.getConnectivityStatusString(context);
           //     mainPresenter.checkConnection();
            }
        };

        //setup the main activity to be notified when network changes occur
        MainActivity.this.registerReceiver(
                mConnectivityChangedReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    //Method Name: toggleFab
    //Parameters: boolean show
    //Return: void
    //Description: toggles Fab
    public void toggleFab(boolean show){
        if(show){
            fab.show();
        } else {
            fab.hide();
        }
    }

    //Method Name: openSettingsActivity
    //Parameters: void
    //Return: void
    //Description: opens the settings activity
    private void openSettingsActivity(){
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    //Method Name: onActivityResult
    //Parameters: int requestCode, int resultCode, Intent data
    //Return: void
    //Description: called upon adding a device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);

        if(data != null && data.hasExtra("message")) {
            String result = data.getStringExtra("message");
            Snackbar snackbar = Snackbar.make(coordinator, result, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
      notifyFragments();
    }

    //Method Name: onBackPressed
    //Parameters: void
    //Return: void
    //Description: called when back pressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
          //  super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Method Name: onOptionsItemSelected
    //Parameters: MenuItem item
    //Return: boolean
    //Description: Handle action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method Name: onNavigationItemSelected
    //Parameters: MenuItem item
    //Return: boolean
    //Description: changes view fragment
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_usage) {
            vpPager.setCurrentItem(MYUSAGE_FRAGMENT, true);
        } else if (id == R.id.nav_rules) {
            vpPager.setCurrentItem(MYRULES_FRAGMENT, true);
        } else if (id == R.id.nav_devices) {
            vpPager.setCurrentItem(MYDEVICES_FRAGMENT, true);
        } else if (id == R.id.nav_settings) {
            openSettingsActivity();
        } else if (id == R.id.nav_reset_server) {
            mainPresenter.ResetLocalServer();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void resetLocalServer(){
//        ApiService apiService = ApiClient
//                .getInstance(getApplicationContext())
//                .getApiService();
//
//        Call call = apiService.resetServer();
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//
//            }
//        }
//    }

    private void logout() {
        CredentialsManager.deleteCredentials(getApplicationContext());
        CredentialsManager.deleteUserProfile(getApplicationContext());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    //Method Name: notifyFragment
    //Parameters: void
    //Return: void
    //Description: notifies fragment
    public void notifyFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof UsageOverviewFragment){
                //((UsageOverviewFragment) x).notifyAdapter();
            } else if (x instanceof RuleFragment) {
                ((RuleFragment) x).notifyAdapter();
            } else if (x instanceof DeviceFragment){
              //  ((DeviceFragment) x).refreshList();
            }
        }
    }

    public void notifyRules(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof RuleFragment) {
                ((RuleFragment) x).notifyAdapter();
            }
        }
    }

    public void notifyDevices(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof DeviceFragment) {
                ((DeviceFragment) x).notifyAdapter();
            }
        }
    }

    //Method Name: updateUsageData
    //Parameters: void
    //Return: void
    //Description: updates usage data
    public void updateUsageData(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof UsageOverviewFragment){
                ((UsageOverviewFragment) x).updateUsageData();
            }
        }
    }

    //Method Name: onResume
    //Parameters: void
    //Return: void
    //Description: called when app resumes
    @Override
    protected void onResume() {
        super.onResume();
        if(DummyContent.notify){
            notifyFragments();
            DummyContent.notify = false;
        }
        mainPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mainPresenter.onPause();
    }

    @Override
    public void onListFragmentInteraction(Device item) {
        Intent deviceDetailIntent = new Intent(MainActivity.this, DeviceDetailActivity.class);
        deviceDetailIntent.putExtra("index",DummyContent.MY_DEVICES.indexOf(item));
        startActivity(deviceDetailIntent);
    }

    @Override
    public void OnDevicesLoaded() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof UsageOverviewFragment){
                ((UsageOverviewFragment) x).getPresenter().loadUsages(false);
            } else if (x instanceof RuleFragment) {
                ((RuleFragment) x).getPresenter().loadRules(false);
            }
        }
    }

    // Page Change Listener Methods

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(adapterViewPager.getPageTitle(position));

        switch (position){
            case MYUSAGE_FRAGMENT:
                toggleFab(false);
                //fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_refresh_black_24dp));
                break;
            case MYRULES_FRAGMENT:
                toggleFab(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_add_black_24dp));
                break;
            case MYDEVICES_FRAGMENT:
                toggleFab(true);
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_add_black_24dp));
                break;
            default:
                //fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_add_black_24dp));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onListFragmentInteraction(Rule item) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", vpPager.getCurrentItem());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vpPager.setCurrentItem(savedInstanceState.getInt("currentPage"));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showConnectionChanged(final int connectionType) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(connectionType == ApiClient.CLOUD) {
                    connection.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLighter));
                    connection.setText("Cloud");                }
                else if(connectionType == ApiClient.LOCAL){
                    connection.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    connection.setText("Local");
                }
                else{
                    connection.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    connection.setText("No Connection");
                }
            }
        });
    }

//    @Override
//    public void refreshLists() {
//        notifyFragments();
//    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mainPresenter = presenter;
    }

    @Override
    public void hideTitle() {

    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showRequestFailed(String message) {

    }

    @Override
    public void showRequestSuccess(String message) {

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MYUSAGE_FRAGMENT: // Fragment # 0 - This will show First Fragment different title
                    return UsageOverviewFragment.newInstance("","");
                case MYRULES_FRAGMENT: // Fragment # 1 - This will show Second Fragment
                    return RuleFragment.newInstance(1);
                case MYDEVICES_FRAGMENT: // Fragment # 2 - This will show Third Fragment
                    return DeviceFragment.newInstance(1);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case MYUSAGE_FRAGMENT: // Fragment # 0 - This will show FirstFragment
                    return "Usage";
                case MYRULES_FRAGMENT: // Fragment # 1 - This will show SecondFragment
                    return "Rules";
                case MYDEVICES_FRAGMENT: // Fragment # 2 - This will show ThirdFragment
                    return "Devices";
                default:
                    return "Joulie";
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.this.registerReceiver(
                mConnectivityChangedReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
