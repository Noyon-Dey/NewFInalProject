package com.example.bitmproject.tourmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitmproject.tourmate.AdapterClass.CostAdapter;
import com.example.bitmproject.tourmate.AdapterClass.ExpandableListAdapter;
import com.example.bitmproject.tourmate.PosoClass.EventClass;
import com.example.bitmproject.tourmate.PosoClass.ExpenseClass;
import com.example.bitmproject.tourmate.PosoClass.PictureClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProfileEventActivity extends AppCompatActivity implements CostAdapter.ClickListener {

    private DatabaseReference eventReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference expenseReference;
    private DatabaseReference pictureRef;
    private String eventId;
    private String userId;
    private double eventBudget;


    //for expandable list View
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private ProgressBar progressBar;
    private TextView tourAreaTv,budgetStatusTv;
    private EventClass event;

    private android.support.v7.widget.Toolbar toolbar;

    private double budged;
    private double haveMoney;

    //firebase Stroage
    StorageReference firebaseStorage;
    private static final int STORAGE_REQUEST_CODE = 5;
    private static final int CAMERA_REQUEST_CODE = 6;
    String captureImagePath ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_event);

        /***************Opening Activity***************/

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        progressBar = findViewById(R.id.amountProgress);
        tourAreaTv = findViewById(R.id.tourAreaTv);
        budgetStatusTv = findViewById(R.id.budgetStatusTv);
        expandableListView = findViewById(R.id.expandableListView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = (EventClass) getIntent().getSerializableExtra("Event");
        eventId = event.getEventId();
        eventBudget = event.getBudget();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        //firebase Stroage
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        prepareListData();
        adapter = new ExpandableListAdapter(this,listDataHeader,listDataChild);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //Expandable ListView Click Method
                onClickExpandableListView(groupPosition,childPosition);


                return false;
            }
        });

        eventReference = FirebaseDatabase.getInstance().getReference("Event");
        eventReference.keepSynced(true);
        expenseReference = FirebaseDatabase.getInstance().getReference("Event").child(userId).child(eventId);
        expenseReference.keepSynced(true);
        pictureRef = FirebaseDatabase.getInstance().getReference("Picture").child(userId).child(eventId);







    }

    // for adding expense thorough alert dialog
    private void addExpense(String costTittle, double costAmount, String finalDate) {

            String expenseId =  expenseReference.push().getKey();

        ExpenseClass expenseClass = new ExpenseClass(userId,eventId,expenseId,costAmount,costTittle,finalDate);

        expenseReference.child("Expense").child(expenseId).setValue(expenseClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileEventActivity.this, "data imported", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ProfileEventActivity.this, "data not imported", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // get all basic info of this event
    private void showEventBasicInformation() {

        eventReference.child(userId).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                    String  eventName = eventClass.getEventName();
                    String staringLocation = eventClass.getStartingLocation();
                    String destinationLocation = eventClass.getDestination();
                    budged = eventClass.getBudget();
                    toolbar.setTitle(eventName);
                    tourAreaTv.setText(staringLocation+" To "+destinationLocation);
                    ////////////Anoter method //////////
                    showProgressInProgressBer(budged);
                }
                else {
                    Toast.makeText(ProfileEventActivity.this, "Value not gated", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileEventActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // For Updating ProgressBer
    private void showProgressInProgressBer(final double budged) {

        expenseReference.child("Expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){

                    // expense is already Added in Database

                    double totalCost =0;
                    for (DataSnapshot expens : dataSnapshot.getChildren()){
                        ExpenseClass expenseClass = expens.getValue(ExpenseClass.class);
                        double newCost = expenseClass.getExpenseAmount();
                        totalCost+=newCost;
                    }
                    if (totalCost>0){
                        double progress = (100*totalCost)/budged;
                        int progressInt = (int)progress;
                        progressBar.setProgress(progressInt);
                        haveMoney = budged -totalCost;
                        String hMS = String.valueOf(haveMoney);
                        budgetStatusTv.setText("You have "+hMS+" taka out of "+ budged +" taka");
                    }

                }
                else {
                    // Expense is not Aready Added in Activity
                    haveMoney = ProfileEventActivity.this.budged;
                    budgetStatusTv.setText("You have "+ ProfileEventActivity.this.budged +" taka out of "+ ProfileEventActivity.this.budged +" taka");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileEventActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Expandable ListView
    // Prepare Data For Expandable List View
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding Head data
        listDataHeader.add("Expenditure");
        listDataHeader.add("Moments");
        listDataHeader.add("More on Event..");

        // Adding child data
        List<String> expenditure = new ArrayList<String>();
        expenditure.add("Add New Expense");
        expenditure.add("View All Expense");
        expenditure.add("Add more Budget");

        List<String> moments = new ArrayList<String>();
        moments.add("Take a Photo");
        moments.add("Pick Image From Gallery");
        moments.add("View All Moments");

        List<String> moreOnEvent = new ArrayList<String>();
        moreOnEvent.add("Edit Event");
        moreOnEvent.add("Delete Event");

        listDataChild.put(listDataHeader.get(0), expenditure); // Header, Child data
        listDataChild.put(listDataHeader.get(1), moments);
        listDataChild.put(listDataHeader.get(2), moreOnEvent);
    }

    //Expandable ListView Click Method
    // For get Spection Position Of item Click
    private void onClickExpandableListView(int groupPosition, int childPosition) {

        switch (groupPosition){
            case 0:
                //Expenditure
                switch (childPosition){
                    case 0:
                        //Add New Expense
                        addNewExpense();
                        break;
                    case 1:
                        // View All Expense
                        costItemList();
                        break;
                    case 2:
                        //Add More Budget
                        addMoreBudget();
                        break;
                }
                break;
            case 1:
                //Moment
                switch (childPosition){
                    case 0:
                        //Take Photo
                        takePhoto();
                        break;
                    case 1:
                        //Pick Image From Gallery
                        pickImageFromGallery();
                        break;
                    case 2:
                        //View All Moments
                        viewAllMoments();
                        break;
                }
                break;
            case 2:
                switch (childPosition){
                    case 0:
                        //Edit Event
                        editEvent();
                        break;
                    case 1:
                        //Delete Event
                        deleteEvent();
                        break;

                }
                break;
        }
    }

    private void viewAllMoments() {
        Intent intent = new Intent(this,MomentGalleryActivity.class);
        intent.putExtra("eventId",eventId);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    public void takePhoto() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},101);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.bitmproject.tourmate.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        captureImagePath = image.getAbsolutePath();
        return image;
    }

    private void pickImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,STORAGE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK){
            // FOR PICK IMAGE FORM GALLERY
            Uri uri = data.getData();
            StorageReference path = firebaseStorage.child(userId).child(eventId).child("Photos").child(uri.getLastPathSegment());
            path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri uri = taskSnapshot.getDownloadUrl();
                    String photoUri = String.valueOf(uri);
                    String pictureId = pictureRef.push().getKey();
                    PictureClass picture = new PictureClass(userId,eventId,pictureId,photoUri);
                    pictureRef.child(pictureId).setValue(picture).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileEventActivity.this, "Photo Uploded", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ProfileEventActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ){
            // FOR PICK IMAGE FROM CAMERA
            /*Bundle extras = data.getExtras();
            Log.e("photo", "onActivityResult: "+captureImagePath);
*/
            Uri uri = Uri.fromFile(new File(captureImagePath));
            StorageReference riversRef = firebaseStorage.child(userId).child(eventId).child("Photos/"+uri.getLastPathSegment());
            riversRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    String photoUri = String.valueOf(uri);
                    String pictureId = pictureRef.push().getKey();
                    PictureClass picture = new PictureClass(userId,eventId,pictureId,photoUri);
                    pictureRef.child(pictureId).setValue(picture).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileEventActivity.this, "Photo Uploded", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ProfileEventActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });


        }

        else {
            Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
        }

    }

    //Expandable ListView Click Method
    //Add New Expense
    //Show Alert Dialog And check is it emptay
    private void addNewExpense() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater =LayoutInflater.from(this);

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.expense_dialog,null,true);
        final EditText costTittleEt = layout.findViewById(R.id.costTittleEt);
        final EditText costAmountEt = layout.findViewById(R.id.costAmountEt);
        builder.setView(layout);
        builder.setPositiveButton("Add Expense", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //geting current Date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd, MMM, yyyy 'at' HH:mm a");
                String finalDate = format.format(calendar.getTime());

                String costTittle =costTittleEt.getText().toString();
                String costam = costAmountEt.getText().toString();
                double costAmount = Double.parseDouble(costAmountEt.getText().toString());
                if (!TextUtils.isEmpty(costTittle) && !TextUtils.isEmpty(costam)){

                    if (haveMoney>=costAmount){
                        // if alert dialog is filled up call
                        addExpense(costTittle, costAmount,finalDate);
                    }
                    else {
                        addMoreBudget();
                    }

                }
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }


    //cost list Method
    private void costItemList() {
        expenseReference.child("Expense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<ExpenseClass>expenses = new ArrayList<>();
                    for (DataSnapshot costList : dataSnapshot.getChildren()){
                        ExpenseClass expenseClass = costList.getValue(ExpenseClass.class);
                        expenses.add(expenseClass);
                    }
                    if (!expenses.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEventActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(ProfileEventActivity.this);
                        View view = inflater.inflate(R.layout.cost_layout,null,true);
                        final RecyclerView recyclerView = view.findViewById(R.id.costRv);
                        builder.setView(view);
                        String tost = expenses.get(0).getExpenseTittle();
                        Toast.makeText(ProfileEventActivity.this, ""+tost, Toast.LENGTH_SHORT).show();
                        CostAdapter costAdapter = new CostAdapter(ProfileEventActivity.this,expenses,ProfileEventActivity.this);
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(ProfileEventActivity.this,LinearLayoutManager.VERTICAL,false);
                        recyclerView.setLayoutManager(manager);
                        recyclerView.setAdapter(costAdapter);
                        builder.setNegativeButton("Close",null);
                        builder.show();
                    }
                    else {
                        Toast.makeText(ProfileEventActivity.this, "No Cost Added", Toast.LENGTH_SHORT).show();
                    }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileEventActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMoreBudget() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater =LayoutInflater.from(this);

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_budget,null,true);
        final EditText newAmountEt = layout.findViewById(R.id.newAmmount);
        
        builder.setView(layout);
        builder.setPositiveButton("Add Budget", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String newAmount = newAmountEt.getText().toString();
                double addedBudget = Double.valueOf(newAmountEt.getText().toString());
                double newBudget = budged +addedBudget;
                if (!TextUtils.isEmpty(newAmount)){

                    eventReference.child(userId).child(eventId).child("budget").setValue(newBudget).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileEventActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(ProfileEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel",null);
        builder.show();

    }
    private void deleteEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this event");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventReference.child(userId).child(eventId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(ProfileEventActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ProfileEventActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(ProfileEventActivity.this,EventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }
    private void editEvent() {
        Intent intent = new Intent(ProfileEventActivity.this,AddEventActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("eventId",eventId);
        startActivity(intent);

    }


    @Override
    public void onClickCostItem(ExpenseClass expenseClass) {

    }

    @Override
    public void onLogClickCostItem(ExpenseClass expenseClass) {
        final String  costId = expenseClass.getExpenseId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove this cost ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                expenseReference.child("Expense").child(costId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProfileEventActivity.this, "Cost Deleted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ProfileEventActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*********Mehtod are running on start activity**********/
        //showing event basic information
        showEventBasicInformation();

    }
}
