package com.example.camerafeatures;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import featuresutil.DataViewModel;
import featuresutil.SaveImageToGallary;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectOrDiscardSelfieFragment#} factory method to
 * create an instance of this fragment.
 */
public class SelectOrDiscardSelfieFragment extends Fragment {

    DataViewModel selfieDataViewModel;

    ImageView imagePreview;

    Button saveButton;

    Button discardButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_select_or_discard_selfie, container, false);

        selfieDataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        Bitmap selfieBitmap = selfieDataViewModel.getBitmapItem().getValue();


        if(selfieBitmap == null){
            Log.e("SelectOrDiscardSelfieFragment", "passed Bitmap is null");
        }

        imagePreview = view.findViewById(R.id.image_preview);
        imagePreview.setImageBitmap(selfieBitmap);

        saveButton = view.findViewById(R.id.save_Image);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeImageToEdit();

            }
        });

        discardButton = view.findViewById(R.id.discard_Image);

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardImageOption();
            }
        });

        return view;
    }

    // Called when save button is pressed
    void takeImageToEdit(){
        Bitmap imageBitmap = selfieDataViewModel.getBitmapItem().getValue();

        // Saving the image to the device
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            SaveImageToGallary.insertImagesSDKQAndGreater(requireContext(),imageBitmap);
        }else{
            SaveImageToGallary.insertImagesBelowSDKQ(imageBitmap);
        }

        // Passing the result to the caller activity
        Intent resultIntent = new Intent();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArray);
        resultIntent.putExtra("data", byteArray.toByteArray());

        requireActivity().setResult(RESULT_OK,resultIntent);

        // Closing the activity
        requireActivity().finish();
    }

    // Called when discard image is pressed
    void discardImageOption(){

        // Creating the builder for discard dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Setting the message for the AlertDialog
        builder.setMessage("Do you want to discard the image?");

        // Setting the title for the the AlertDialog
        builder.setTitle("Discard!");

        // Setting the cancelable(for showing the dialog when the user clicks outside the dialogbox
        builder.setCancelable(false);

        // Setting the action of the positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Setting the action of the negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Creating alert dialog
        AlertDialog alertDialog =  builder.create();

        alertDialog.show();

    }

}