package com.example.final_bitm;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bitmproject.tourmate.PosoClass.EventClass;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameET, startLoactionET, destinationET, departureDateEt, budgetET;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private int year, month, day;
    private Calendar calendar;
    private String eventId;
    private String userId ;
    private Button addEventBtn;
    DatabaseReference eventReference;
    EventClass event;

    /******************for geo felcing**************/
    private static final int GEO_FENCE_CODE = 10;
    private static final int GEOFENCE_PENDIND_CODE = 111;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    double lat;
    double lng;
    ArrayList<Geofence>geofences=new ArrayList<>();
    LatLng latLng;
    Address address;
    GeofencingClient geofencingClient;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameET = findViewById(R.id.eventNameEt);
        startLoactionET = findViewById(R.id.startLocationEt);
        destinationET = findViewById(R.id.destinationEt);
        departureDateEt = findViewById(R.id.departureDateEt);
        budgetET = findViewById(R.id.budgetET);
        addEventBtn = findViewById(R.id.createEventBtn);
        geofencingClient = LocationServices.getGeofencingClient(AddEventActivity.this);
        pendingIntent = null;
        firebaseAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        eventReference = FirebaseDatabase.getInstance().getReference("Event");


        eventId = getIntent().getStringExtra("eventId");
        userId = getIntent().getStringExtra("userId");
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(userId)) {
            eventReference.child(userId).child(eventId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        event = dataSnapshot.getValue(EventClass.class);
                        addEventBtn.setText("Update");
                        eventNameET.setText(event.getEventName());
                        startLoactionET.setText(event.getStartingLocation());
                        destinationET.setText(event.getDestination());
                        departureDateEt.setText(event.getDepartureDate());
                        budgetET.setText(String.valueOf(event.getBudget()));
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, "Value not gated", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Event");
        databaseReference.keepSynced(true);

        /*client = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        if(!checkInternetConnection(this)){
            startLoactionET.setFocusable(true);
            startLoactionET.setClickable(false);
            destinationET.setFocusable(true);
            destinationET.setClickable(false);
        }else {
            startLoactionET.setFocusable(false);
            startLoactionET.setClickable(true);
            destinationET.setFocusable(false);
            destinationET.setClickable(true);
        }*/
    }
 /*   void getLastLocation(){
        //get last location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1010);
            return;
        }
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    lat = lastLocation.getLatitude();
                    lng = lastLocation.getLongitude();
                    latLng = new LatLng(lat, lng);
                    address=getAddress(lat,lng);
                }
            }
        });
    }*/

    //get Address
/*    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (Exception e){}
        return null;
    }*/
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1010&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            registerGeofence();
            getLastLocation();
        }else {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }*/
    /**************Create Event Button****************/
    public void createNewEvent(View view) {


        String eventName = eventNameET.getText().toString();
        String startLocation = startLoactionET.getText().toString();
        String destination = destinationET.getText().toString();
        String departureDate = departureDateEt.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM, yyyy");
        String  startingDate = sdf.format(new Date());
        String budgetch = budgetET.getText().toString();
        double budget = Double.valueOf(budgetET.getText().toString());


        if (!TextUtils.isEmpty(eventName) && !TextUtils.isEmpty(startLocation) && !TextUtils.isEmpty(destination) &&
                !TextUtils.isEmpty(departureDate) && !TextUtils.isEmpty(budgetch))
        {
            //all the editText is filled up
            if (event != null){
                String eventId = event.getEventId();
                updateEventItem(eventId,eventName,startLocation,destination,departureDate,startingDate,budget);
                final EventClass eventClass = new EventClass(eventId,eventName,startLocation,destination,departureDate,startingDate,budget);
                Intent intent = new Intent(this,ProfileEventActivity.class);
                intent.putExtra("Event",eventClass);
                startActivity(intent);
            }
            else {
                createNewEventItem(eventName,startLocation,destination,departureDate,startingDate,budget);
            }



        }
        else {
            Toast.makeText(this, "Please Fill all the Field", Toast.LENGTH_SHORT).show();
        }




    }




    /*********click on depratureDate Edit text***************/
    public void depratureDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,datelistener,year,month,day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
    DatePickerDialog.OnDateSetListener datelistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM, yyyy");
            calendar.set(year,month,dayOfMonth);
            String finalDate = sdf.format(calendar.getTime());
            departureDateEt.setText(finalDate);


        }
    };

    private void createNewEventItem(String eventName,String startLocation, String destination,
                                    String departureDate,String startingDate,double budget) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String eventId = databaseReference.push().getKey();
        EventClass eventClass = new EventClass(eventId,eventName,startLocation,destination,departureDate,startingDate,budget);
        databaseReference.child(userId).child(eventId).setValue(eventClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
                    AddEventActivity.this.finish();
                    startActivity(new Intent(AddEventActivity.this,EventActivity.class));
                }
                else {
                    Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateEventItem(String eventId, String eventName, String startLocation, String destination,
                                 String departureDate, String startingDate, double budget) {
        String userId = firebaseAuth.getCurrentUser().getUid();

        if (!event.getEventName().equals(eventName)){
            databaseReference.child(userId).child(eventId).child("eventName").setValue(eventName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "Event Name Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(!event.getStartingLocation().equals(startLocation)){

            databaseReference.child(userId).child(eventId).child("startingLocation").setValue(startLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "Starting location Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(!event.getDestination().equals(destination)){

            databaseReference.child(userId).child(eventId).child("destination").setValue(destination).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "Destination Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(!event.getDepartureDate().equals(departureDate)){

            databaseReference.child(userId).child(eventId).child("departureDate").setValue(departureDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "departure date Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(event.getBudget()!=budget){

            databaseReference.child(userId).child(eventId).child("budget").setValue(budget).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AddEventActivity.this, "budget Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AddEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

 /*   public void getLocation(View view) {
        int req=234577;
        switch (view.getId()){
            case R.id.startLocationEt:
                req=2020;
                break;
            case R.id.destinationEt:
                req=3030;
                break;
        }
        AutocompleteFilter typeFilter;
        try {
            address = getAddress(lat, lng);
            if (address != null) {
                typeFilter = new AutocompleteFilter.Builder()
                        .setCountry(address.getCountryCode())
                        .build();
            } else {
                typeFilter = new AutocompleteFilter.Builder()
                        .build();
            }

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(AddEventActivity.this);
            startActivityForResult(intent, req);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }*/
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2020) {
            if (resultCode == RESULT_OK) {
                Place place=PlaceAutocomplete.getPlace(AddEventActivity.this,data);
                startLoactionET.setText(place.getName());
            }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("",status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == 3030) {
            if (resultCode == RESULT_OK) {
                Place place=PlaceAutocomplete.getPlace(AddEventActivity.this,data);
                destinationET.setText(place.getName());
                String address="Selected area";
                address=place.getAddress().toString();
                Geofence geofence = new Geofence.Builder().setRequestId(address)
                        .setCircularRegion(place.getLatLng().latitude, place.getLatLng().longitude, 200)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setExpirationDuration(6 * 60 * 60 * 1000)
                        .build();
                geofences.add(geofence);
                registerGeofence();
                Toast.makeText(AddEventActivity.this, "Added to Geofenced Area", Toast.LENGTH_SHORT).show();
            }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("",status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/
    //----------------------------GeoFence-----------------------------
/*    private void registerGeofence() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddEventActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GEO_FENCE_CODE);
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(),getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddEventActivity.this, "Geofence Added", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddEventActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("error", task.getException().getMessage());
                }
            }
        });
    }*/
   /* private GeofencingRequest getGeofencingRequest(){
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
            Intent intent = new Intent(AddEventActivity.this,GeofencePendingIntentService.class);
            pendingIntent = PendingIntent.getService(this,GEOFENCE_PENDIND_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
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
    }*/
}
