package com.example.final_bitm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PictureActivity extends AppCompatActivity {

    ArrayList<String> photoUri = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        photoUri = getIntent().getStringArrayListExtra("photoUri");
        int imagePosition = getIntent().getIntExtra("imagePosition",0);
        Toast.makeText(this, ""+ String.valueOf(imagePosition), Toast.LENGTH_SHORT).show();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String url="";
        try {
            url=photoUri.get(imagePosition);
        }catch (NumberFormatException e){

        }

        Picasso.with(this).load(url).into(imageView);

    }
}
