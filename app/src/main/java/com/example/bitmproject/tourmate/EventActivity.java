package com.example.bitmproject.tourmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bitmproject.tourmate.AdapterClass.EventAdapter;
import com.example.bitmproject.tourmate.PosoClass.EventClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity implements EventAdapter.clickListener {

    private DatabaseReference databaseReference;
    private DatabaseReference eventReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView eventRv;
    private ArrayList<EventClass>eventClassArrayList;
    private EventAdapter eventAdapter;

    //for navigation Drawer
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tourWeatherTour:
                    startActivity(new Intent(EventActivity.this,WeatherActivity.class));
                    return true;
                case R.id.tourMapTour:
                    Toast.makeText(EventActivity.this, "tourmap", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EventActivity.this,MapActivity.class));
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //for navigation Drawer
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        eventRv = findViewById(R.id.eventsRv);
        eventClassArrayList = new ArrayList<>();




        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Event List");
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        eventReference = FirebaseDatabase.getInstance().getReference("Event");




    }


    /******* Floating Button for Add event*********/
    public void goNewEventPage(View view) {
            startActivity(new Intent(this,AddEventActivity.class));
    }

    /**************************************Menu item**********************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOutMenu:
                firebaseAuth.signOut();
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //update user password
//    private void updatePassword() {
//android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
//        final View view1 = LayoutInflater.from(this).inflate(R.layout.update_password,null,true);
//        builder.setView(view1);
//        builder.setCancelable(false);
//        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText passEt = view1.findViewById(R.id.passChEt);
//                EditText conEt = view1.findViewById(R.id.confirmChEt);
//                String pass = passEt.getText().toString();
//                String con = conEt.getText().toString();
//                if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(con)){
//                    if (pass.equals(con)){
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                        user.updatePassword(pass)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(EventActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                            Toast.makeText(EventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                });
//                    }
//                    else {
//                        Toast.makeText(EventActivity.this, "Email Password Doesn't match", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    Toast.makeText(EventActivity.this, "Please feel all the field", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel",null);
//        builder.show();
//    }
// delete Current user
    private void deleteUser() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(EventActivity.this,LoginActivity.class));
                            Toast.makeText(EventActivity.this, "Your Account is deleted", Toast.LENGTH_SHORT).show();
                            databaseReference.child(userId).removeValue();
                            eventReference.child(userId).removeValue();
                        }
                        else {
                            Toast.makeText(EventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*************on click recyclerView list item**************/
    @Override
    public void onClickEvent(EventClass eventClass) {

       Intent intent = new Intent(this,ProfileEventActivity.class);
       intent.putExtra("Event",eventClass);
       startActivity(intent);
    }

    /**************on long click recyclerView******************/
    @Override
    public void onLogClickEvent(final EventClass eventClass) {
        eventReference = FirebaseDatabase.getInstance().getReference("Event");
        final String userId = firebaseAuth.getCurrentUser().getUid();
        final String eventId = eventClass.getEventId();



        //khhkh
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(eventClass.getEventName());
        alertDialog.setMessage("Do you want to delete this Event??");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventReference.child(userId).child(eventId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(EventActivity.this, "Event removed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(EventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        alertDialog.setNegativeButton("No",null);

        alertDialog.show();
    }
    // create List in RecyclerView
    private void createEventList(String userId) {
        eventReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren() !=null){
                    eventClassArrayList.clear();
                    for (DataSnapshot events : dataSnapshot.getChildren()){
                        EventClass eventClass = events.getValue(EventClass.class);
                        eventClassArrayList.add(eventClass);
                    }
                    eventAdapter= new EventAdapter(EventActivity.this,eventClassArrayList,EventActivity.this);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(EventActivity.this,LinearLayoutManager.VERTICAL,false);
                    eventRv.setLayoutManager(manager);
                    eventRv.setAdapter(eventAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //method care running
        String userId = firebaseAuth.getCurrentUser().getUid();
        createEventList(userId);
    }
}
