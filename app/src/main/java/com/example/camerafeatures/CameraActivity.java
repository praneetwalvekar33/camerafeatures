package com.example.camerafeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class CameraActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        // Creating the fragment in the activity
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .add(R.id.fragment_container_view_camera, CameraFragment.class, null)
                .commit();
    }
}