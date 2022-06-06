package com.example.camerafeatures;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import featuresutil.DataViewModel;

public class EditImageActivity extends AppCompatActivity {

    Bitmap initialImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        Intent intent = getIntent();

        // Used to get the image from calling activity
        Bitmap imageBitmap = null;
        if(intent.hasExtra("image_Array")) {
            imageBitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra(
                    "image_Array"), 0 , intent.getByteArrayExtra("image_Array")
            .length);
        }

        initialImageBitmap = imageBitmap;

        // Creating the instance of DataViewModel for this activity
        DataViewModel dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        dataViewModel.setBitmapItem(initialImageBitmap);

        // Creating the fragment in the activity
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .add(R.id.fragment_container_view_crop_window, EditImageFragment.class, null)
                .commit();
    }

    @Override
    public void onBackPressed(){

        if(getSupportFragmentManager().getBackStackEntryCount()>0) {

            getSupportFragmentManager().popBackStackImmediate();
        }else{
            super.onBackPressed();
        }

   }
}