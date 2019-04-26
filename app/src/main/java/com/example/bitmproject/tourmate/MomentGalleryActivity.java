package com.example.bitmproject.tourmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bitmproject.tourmate.AdapterClass.ImageAdapter;
import com.example.bitmproject.tourmate.PosoClass.PictureClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MomentGalleryActivity extends AppCompatActivity {

    private String eventId;
    private String userId;
    private DatabaseReference databaseReference;
    private ArrayList<PictureClass> pictureClasses = new ArrayList<>();
    private ArrayList<String>photoUris = new ArrayList<>();
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_gallery);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Gallery");
        gridView = findViewById(R.id.gridView);


        eventId = getIntent().getStringExtra("eventId");
        userId = getIntent().getStringExtra("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Picture");
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int imagePosition = position;
                Toast.makeText(MomentGalleryActivity.this, ""+String.valueOf(imagePosition), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MomentGalleryActivity.this,PictureActivity.class);
                intent.putExtra("photoUri",photoUris);
                intent.putExtra("imagePosition",imagePosition);
                startActivity(intent);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MomentGalleryActivity.this);
                builder.setMessage("Do you want to delete this picture");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage(position);
                        getAllPictureLink();
                    }
                });
                builder.setNegativeButton("No",null);
                builder.show();

                return true;
            }
        });
    }

    private void deleteImage(int position) {
        PictureClass pictureClass =pictureClasses.get(position);
        String pictureId = pictureClass.getPictureId();
        databaseReference.child(userId).child(eventId).child(pictureId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MomentGalleryActivity.this, "picture deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MomentGalleryActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAllPictureLink();
    }

    private void getAllPictureLink() {
        databaseReference.child(userId).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if (dataSnapshot.getValue() !=null){
                    pictureClasses.clear();
                    photoUris.clear();
                    for (DataSnapshot pictureLink : dataSnapshot.getChildren()){
                        PictureClass pictureClass = pictureLink.getValue(PictureClass.class);
                        pictureClasses.add(pictureClass);
                        photoUris.add(pictureClass.getPhotoUrl());
                    }
                    gridView.setAdapter(new ImageAdapter(MomentGalleryActivity.this,photoUris));
                    Toast.makeText(MomentGalleryActivity.this, ""+String.valueOf(pictureClasses.size()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
