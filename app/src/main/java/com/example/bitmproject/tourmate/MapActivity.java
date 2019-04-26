package com.example.bitmproject.tourmate;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.quinny898.library.persistentsearch.SearchBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int GEO_FENCE_CODE = 10;
    private static final int GEOFENCE_PENDIND_CODE = 111;
    GoogleMap map;
    Address address;
    GoogleMapOptions options;
    public static final int PERMISSION_REQUEST = 1;
    ArrayList<MyItem> clusterItems = new ArrayList<>();
    ArrayList<MyItem> nearByItems = new ArrayList<>();
    ClusterManager<MyItem> clusterManager;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    double lat, lng;
    LatLng latLng;
    Place place;
    private String origin;
    private String destination;
    private final int AUTOCOMPLTE_REQUEST = 2;
    SearchBox search;
    FloatingActionButton direction;
    ImageView Drawer;
    AutocompleteFilter typeFilter;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    String urlString;
    double nearByLat;
    double nearByLng;
    double radius;
    String type;
    double desLng;
    double desLat;
    View bottomSheetHeaderColor;
    String key;
    private BottomSheetBehavior mBottomSheetBehavior;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String nextPage;
    int responceCode;
    String nearByString;
    boolean remove;
    boolean dir;
    GeoDataClient mGeoDataClient;
    AlertDialog.Builder alert;
    MyItem item;
    boolean check;
    GeofencingClient geofencingClient;
    PendingIntent pendingIntent;
    ArrayList<Geofence> geofences = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //bottom navigation
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tourProjectMap:
                        startActivity(new Intent(MapActivity.this,EventActivity.class));
                        return true;
                    case R.id.tourWeatherMap:
                        startActivity(new Intent(MapActivity.this,WeatherActivity.class));
                        return true;
                }
                return false;
            }
        };
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        geofencingClient = LocationServices.getGeofencingClient(MapActivity.this);
        pendingIntent = null;
        //places = new ArrayList<>();
        direction = findViewById(R.id.direction);
        direction.setVisibility(View.INVISIBLE);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        View bottomSheet = findViewById(R.id.bottom_sheet);
       // bottomSheetHeaderColor = findViewById(R.id.color);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        nearByLat = 181;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();
        editor.commit();
        //Create retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //service = retrofit.create(NearByPlacesService.class);
        key = getString(R.string.google_near_by_places_api);


        //navigation drawer
        Drawer = findViewById(R.id.drawer_logo);

        Drawer.setVisibility(View.INVISIBLE);

        //map fragment
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true).compassEnabled(true).mapType(GoogleMap.MAP_TYPE_TERRAIN);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING
                        || mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetHeaderColor.setBackgroundColor(getResources().getColor(R.color.darkSky));
                } else {
                    bottomSheetHeaderColor.setBackgroundColor(getResources().getColor(R.color.bluish_gray));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.map = googleMap;

        clusterManager = new ClusterManager<MyItem>(MapActivity.this, googleMap);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        clusterManager.clearItems();

        //set my location enabled
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
            return;
        }

        //get last location
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    lat = lastLocation.getLatitude();
                    lng = lastLocation.getLongitude();
                    latLng = new LatLng(lat, lng);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    address = getAddress(lat, lng);
                    if (address != null) {
                        clusterItems.add(new MyItem(lat, lng, address.getLocality(), address.getAdminArea()));
                    } else clusterItems.add(new MyItem(lat, lng));
                    clusterManager.addItems(clusterItems);
                }
            }
        });


        //Disable Map Toolbar:
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Address geoAddress=getAddress(latLng.latitude,latLng.longitude);
                String address="Selected area";
                if(geoAddress!=null){
                    address=geoAddress.getAddressLine(0);
                }
                Geofence geofence = new Geofence.Builder().setRequestId(address)
                        .setCircularRegion(latLng.latitude, latLng.longitude, 200)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setExpirationDuration(6 * 60 * 60 * 1000)
                        .build();
                geofences.add(geofence);
                if(geofences.size()>0)
                    registerGeofence();
                Toast.makeText(MapActivity.this, "Added to Geofenced Area", Toast.LENGTH_SHORT).show();
            }
        });

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.map = googleMap;
    }

    private void registerGeofence() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GEO_FENCE_CODE);
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(),getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MapActivity.this, "Geofence Added", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MapActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("error", task.getException().getMessage());
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_REQUEST&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            onMapReady(map);
            if(geofences.size()>0)
                registerGeofence();
            check=true;
        }else {
            check=false;
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        GeofencingRequest request=builder.build();
        return request;
    }
    private PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            return pendingIntent;
        }else{
            Intent intent = new Intent(MapActivity.this,GeofencePendingIntentService.class);
            pendingIntent = PendingIntent.getService(this,GEOFENCE_PENDIND_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
    }

    //get Address
    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (Exception e){}
        return null;
    }

    //get searched place
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                desLat = place.getLatLng().latitude;
                desLng = place.getLatLng().longitude;
                destination = "" + desLat + "," + desLng;
                nearByLng=desLng;
                nearByLat=desLat;
                nearByString=place.getAddress().toString();
                origin = "" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                map.clear();
                clusterItems.clear();
                clusterManager.clearItems();
                if(address!=null) {
                    clusterItems.add(new MyItem(lat, lng, address.getLocality(), address.getAdminArea()));
                }
                else clusterItems.add(new MyItem(lat,lng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(desLat, desLng), 12));
                clusterItems.add(new MyItem(desLat, desLng, place.getName().toString(), place.getAddress().toString()));
                clusterManager.addItems(clusterItems);
                clusterManager.cluster();
                dir=true;
                //direction.setVisibility(View.VISIBLE);
            }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("",status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}