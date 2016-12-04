package com.example.kyle.joulieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import java.util.List;

import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttributePayload;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnPageChangeListener,
        DeviceFragment.OnListFragmentInteractionListener,
        UsageFragment.OnListFragmentInteractionListener,
        UsageOverviewFragment.OnFragmentInteractionListener,
        RuleFragment.OnListFragmentInteractionListener,
        IoTApi.onResultListener{

    private FragmentPagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    private static final int MYDEVICES_FRAGMENT = 0;
    private static final int MYUSAGE_FRAGMENT = 1;
    private static final int MYRULES_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (vpPager.getCurrentItem()){

                    case MYDEVICES_FRAGMENT:
                        Intent newDeviceIntent = new Intent(MainActivity.this, NewDeviceActivity.class);
                        startActivityForResult(newDeviceIntent, 1);
                        break;
                    case MYUSAGE_FRAGMENT:

                        break;
                    case MYRULES_FRAGMENT:
                        Intent newRuleIntent = new Intent(MainActivity.this, NewRuleActivity.class);
                        startActivityForResult(newRuleIntent, 1);
                        break;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setup view pager
        setupViewPager();

        //setup Tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vpPager);
        setupTabIcons();

        if (savedInstanceState == null) {
            IoTApi.getInstance().callListThings(this);
            DummyContent.populate(this);
        }
    }

    private void setupViewPager(){
        vpPager = (ViewPager) findViewById(R.id.main_viewpager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(MainActivity.this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(adapterViewPager.getPageTitle(vpPager.getCurrentItem()));
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_power_24dp);
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_trending_up_24dp);
        tabLayout.getTabAt(1).setText("");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_access_alarm_24dp);
        tabLayout.getTabAt(2).setText("");
    }

    public void toggleFab(boolean show){
        if(show){
            fab.show();
        } else {
            fab.hide();
        }
    }

    private void openSettingsActivity(){
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        notifyFragment();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            vpPager.setCurrentItem(MYDEVICES_FRAGMENT, true);
        } else if (id == R.id.nav_usage) {
            vpPager.setCurrentItem(MYUSAGE_FRAGMENT, true);
        } else if (id == R.id.nav_rules) {
            vpPager.setCurrentItem(MYRULES_FRAGMENT, true);
        } else if (id == R.id.nav_settings) {
            openSettingsActivity();
        } else if (id == R.id.nav_logout) {
            AccessToken.setCurrentAccessToken(null);
            onNavigateUp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void notifyFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment x: fragments) {
            if (x instanceof UsageFragment){
                ((UsageFragment) x).notifyAdapter();
            } else if (x instanceof RuleFragment) {
                ((RuleFragment) x).notifyAdapter();
            } else if (x instanceof DeviceFragment){
                ((DeviceFragment) x).notifyAdapter();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DummyContent.notify){
            notifyFragment();
            DummyContent.notify = false;
        }
    }

    @Override
    public void onListFragmentInteraction(Device item) {

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
            case MYDEVICES_FRAGMENT:
                toggleFab(true);
                break;
            case MYUSAGE_FRAGMENT:
                toggleFab(false);
                break;
            case MYRULES_FRAGMENT:
                toggleFab(true);
                break;
            default:
                toggleFab(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onListFragmentInteraction(Usage item) {
       //    UsageFragment myArtistsFragment  = (UsageFragment) getSupportFragmentManager().getFragments().get(MYARTISTS_FRAGMENT);
            //myArtistsFragment.
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
    public void onResult(ListThingsResult result) {
        List<ThingAttribute> test = result.getThings();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
                case MYDEVICES_FRAGMENT: // Fragment # 0 - This will show FirstFragment
                    return DeviceFragment.newInstance(1);
                case MYUSAGE_FRAGMENT: // Fragment # 0 - This will show FirstFragment different title
                    return UsageOverviewFragment.newInstance("","");
                case MYRULES_FRAGMENT: // Fragment # 1 - This will show SecondFragment
                    return RuleFragment.newInstance(1);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case MYDEVICES_FRAGMENT: // Fragment # 0 - This will show FirstFragment
                    return "Devices";
                case MYUSAGE_FRAGMENT: // Fragment # 0 - This will show FirstFragment different title
                    return "Usage";
                case MYRULES_FRAGMENT: // Fragment # 1 - This will show SecondFragment
                    return "Rules";
                default:
                    return "Joulie";
            }
        }

    }

}
