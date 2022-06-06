package com.example.camerafeatures;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.theartofdev.edmodo.cropper.CropImageView;

import featuresutil.DataViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CropImageFragment#} factory method to
 * create an instance of this fragment.
 */
public class CropImageFragment extends Fragment {



    CropImageView previewImageView;

    Button buttonCropImage;

    DataViewModel childDataViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crop_image, container, false);

        // Creating the instance of the DataViewModel for this activity
        childDataViewModel = new ViewModelProvider(requireActivity())
                .get(DataViewModel.class);

        Bitmap initialImageBitmap = childDataViewModel.getBitmapItem().getValue();

        // Setting up the cropimageview
        previewImageView = (CropImageView) view.findViewById(R.id.crop_image_view);
        previewImageView.setGuidelines(CropImageView.Guidelines.OFF);
        previewImageView.setImageBitmap(initialImageBitmap);
        previewImageView.setCropRect(new Rect(0, 0, 1000*2, 1500*2));

        buttonCropImage = (Button) view.findViewById(R.id.crop_button_select_image);

        buttonCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });
        return view;
    }

    // Called to crop the image
    void cropImage(){
        Bitmap bitmap = previewImageView.getCroppedImage();


        childDataViewModel.setBitmapItem(bitmap);

        requireActivity().getSupportFragmentManager().popBackStack();
    }
}