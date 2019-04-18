package  com.example.final_bitm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;


public class WeatherActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private LocationRequest request;
    private double lat;
    double lat1 = 0;
    private double lon;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabPagerAdapter tabPagerAdapter;
    private int count = 14;
    public static String unit = "metric";
    public static MenuItem menuItem;
    MaterialSearchView searchView;
    private String query1;
    AppBarLayout appBarLayout;
    ArrayList<String> cities;
    static String[] cty;
    int itm;
    CurrentWeatherFragment currentWeatherFragment;
    ForcastWeatherFragment forcastWeatherFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode > 0) {
            Intent intent = new Intent(WeatherActivity.this, WeatherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else return;
    }

    //bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tourProjectWeather:
                    startActivity(new Intent(WeatherActivity.this,EventActivity.class));
                    return true;
                case R.id.tourMapWeather:
                    startActivity(new Intent(WeatherActivity.this,MapActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        //toolbar.setSubtitle("Subtitle");

        //for navigation Drawer
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        setSupportActionBar(toolbar);
        final CitySource citySource = new CitySource(WeatherActivity.this);
        cities = citySource.getCities();
        appBarLayout = findViewById(R.id.invisible);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Current Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Weather Forecast"));
        client = LocationServices.getFusedLocationProviderClient(this);


        if (tabPagerAdapter != null)
            tabPagerAdapter.notifyDataSetChanged();
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {

                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    //if (checkInternetConnection(WeatherActivity.this)) {
                    if (viewPager != null) {
                        itm = viewPager.getCurrentItem();
                        setTabLayout(lat, lon, unit, count, "0");
                        viewPager.setCurrentItem(itm);
                    } else setTabLayout(lat, lon, unit, count, "0");
                    try {
                        menuItem.setVisible(false);
                    } catch (NullPointerException e) {
                    }

                    //}
                    //Toast.makeText(WeatherActivity.this,"lat="+lat+" lon="+lon,Toast.LENGTH_SHORT).show();
                }
            }

        };

        request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15 * 60 * 1000)
                .setFastestInterval(15 * 30 * 1000);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        client.requestLocationUpdates(request, locationCallback, null);
        client.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            int itm;
                            if (viewPager != null) {
                                itm = viewPager.getCurrentItem();
                                setTabLayout(lat, lon, unit, count, "0");
                                viewPager.setCurrentItem(itm);
                            } else
                                setTabLayout(lat, lon, unit, count, "0");

                        }
                    }
                });
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(getCt(cities));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query1 = query;
                if (!containsCaseInsensitive(query, cities)) {
                    CityModelClass cityModelClass = new CityModelClass(query);
                    CitySource source = new CitySource(WeatherActivity.this);
                    source.incertCityData(cityModelClass);
                }


                ////**************for get data from database*************///
                cities = citySource.getCities();
                searchView.setSuggestions(getCt(cities));
                if (checkInternetConnection(WeatherActivity.this)) {
                    lat1 = 181;
                    itm = viewPager.getCurrentItem();
                    setTabLayout(lat1, lon, unit, count, query);
                    viewPager.setCurrentItem(itm);
                    tabPagerAdapter.notifyDataSetChanged();
                    menuItem.setVisible(true);
                    searchView.closeSearch();
                    searchView.setSuggestions(getCt(cities));
                    onResume();
                } else {
                    searchView.closeSearch();
                    searchView.setSuggestions(getCt(cities));
                    Toast.makeText(WeatherActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        searchView.setHint("City name, Country short name");
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onSearchViewClosed() {
                toolbar.setVisibility(View.VISIBLE);
            }

        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tabPagerAdapter != null) {
            tabPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem Currentloc = menu.findItem(R.id.currentLocation);

        menuItem = Currentloc;
        if (lat1 == 181)
            Currentloc.setVisible(true);

        MenuItem Unit = menu.findItem(R.id.unitChange);
        if (unit.equals("imperial")) {
            Unit.setTitle("Celsius");
        } else {
            Unit.setTitle("Fahrenheit");
        }

        MenuItem Count = menu.findItem(R.id.countChange);
        if (count == 14) {
            Count.setTitle("7 days");
        } else {
            Count.setTitle("14 days");
        }
        Count.setVisible(false);
        MenuItem item = menu.findItem(R.id.action_search);
        try {
            searchView.setMenuItem(menu.findItem(R.id.action_search));
        } catch (Exception e) {
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (viewPager != null) {
            if (viewPager.getCurrentItem() == 1) {
                menu.findItem(R.id.countChange).setVisible(true);
            } else
                menu.findItem(R.id.countChange).setVisible(false);

        }
        if (lat1 != 181)
            menu.findItem(R.id.currentLocation).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public void setTabLayout(double lat, double lon, String unit, int count, String city) {

        currentWeatherFragment = CurrentWeatherFragment.newInstance(lat, lon, unit, city);
        forcastWeatherFragment = ForcastWeatherFragment.newInstance(lat, lon, unit, count, city);
        viewPager = findViewById(R.id.viewPager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                currentWeatherFragment, forcastWeatherFragment);
        viewPager.setAdapter(tabPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void changeCount(MenuItem item) {
        int itm = viewPager.getCurrentItem();
        setCount();
        viewPager.setCurrentItem(itm);
        if (count == 14) {
            item.setTitle("7 days");
        } else {
            item.setTitle("14 days");
        }

    }

    public void changeUnit(MenuItem item) {
        int itm = viewPager.getCurrentItem();
        setUnit();
        viewPager.setCurrentItem(itm);
        if (unit.equals("imperial")) {
            item.setTitle("Celsius");
        } else {
            item.setTitle("Fahrenheit");
        }
    }

    public void lastLoc(MenuItem item) {
        if (checkInternetConnection(this)) {
            lat1 = 0;
            int itm = viewPager.getCurrentItem();
            setTabLayout(lat, lon, unit, count, "0");
            viewPager.setCurrentItem(itm);
            item.setVisible(false);
        } else Toast.makeText(WeatherActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
    }

    public void setCount() {
        if (count == 7) {
            count = 14;
        } else {
            count = 7;
        }
        WeatherAdapter.count = count;
        forcastWeatherFragment.notifyAdaopterDataChange();
    }

    public void setUnit() {
        if (unit.equals("imperial")) {

            unit = "metric";
            if (lat1 == 181) {
                setTabLayout(lat1, lon, unit, count, query1);

            } else {
                setTabLayout(lat, lon, unit, count, "0");

            }
        } else {

            unit = "imperial";
            if (lat1 == 181) {
                setTabLayout(lat1, lon, unit, count, query1);
            } else {
                setTabLayout(lat, lon, unit, count, "0");

            }
        }
    }

    String[] getCt(ArrayList<String> cities) {
        cty = cities.toArray(new String[cities.size()]);
        return cty;
    }

    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsCaseInsensitive(String s, ArrayList<String> l) {
        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

}