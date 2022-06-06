package com.example.camerafeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import featuresutil.DataViewModel;

public class MainActivity extends AppCompatActivity {

    DataViewModel selfieDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Creating instance of DataViewModel class for this activity
        selfieDataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        // Creating the fragment inside the activity
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .add(R.id.main_activity_fragment_container, SelectionScreenFragment.class, null)
                .commit();

    }
}