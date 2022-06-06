package com.example.camerafeatures;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import featuresutil.DataViewModel;
import featuresutil.SaveImageToGallary;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditImageFragment} factory method to
 * create an instance of this fragment.
 */
public class EditImageFragment extends Fragment {


    ImageView previewImageView;

    Button buttonSaveImage;

    Button buttonCropImage;

    Button buttonUndoImage;

    Button buttonRotateImage;

    Bitmap initialImageBitmap;

    DataViewModel dataViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        // Creating the instance of DataViewModel for this activity
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        initialImageBitmap = dataViewModel.getBitmapItem().getValue();

        previewImageView = (ImageView) view.findViewById(R.id.crop_image_view);
        previewImageView.setImageBitmap(initialImageBitmap);
        try {
            storeBitmapToCache(initialImageBitmap);
        }catch(Exception e){
            e.printStackTrace();
        }

        buttonSaveImage = (Button) view.findViewById(R.id.save_button_select_image);
        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEditedImage();
            }
        });

        buttonCropImage = (Button) view.findViewById(R.id.crop_button_select_image);
        buttonCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();

            }
        });

        buttonRotateImage = (Button) view.findViewById(R.id.rotate_button_select_image);
        buttonRotateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        buttonUndoImage = (Button) view.findViewById(R.id.undo_button_select_image);
        buttonUndoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoImageChanges();
            }
        });

        return view;
    }

    // Called to save the image to the gallary and passing the edited image to the calling activity
    void saveEditedImage(){

        // Saves image to the gallary
        Bitmap imageBitmap = getBitmapFromCache();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            SaveImageToGallary.insertImagesSDKQAndGreater(requireContext(),imageBitmap);
        }else{
            SaveImageToGallary.insertImagesBelowSDKQ(imageBitmap);
        }

        // Passing the edited image as a result to calling activity
        Intent resultIntent = new Intent();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        imageBitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArray);
        resultIntent.putExtra("data", byteArray.toByteArray());

        Toast.makeText(getActivity(), "Image saved to gallary", Toast.LENGTH_SHORT).show();
        requireActivity().setResult(RESULT_OK,resultIntent);
        requireActivity().finish();

    }

    // Called tp crop the image by passing and calling the CropImageFragment
    void cropImage(){

        Bitmap imageBitmap = getBitmapFromCache();

        dataViewModel.setBitmapItem(imageBitmap);

        getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_crop_window, CropImageFragment.class, null)
                .addToBackStack(null).commit();

        Bitmap croppedImage = dataViewModel.getBitmapItem().getValue();
        previewImageView.setImageBitmap(croppedImage);



        try{
            storeBitmapToCache(croppedImage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Undo the changes to the original image passed to the bitmap
    void undoImageChanges(){
        previewImageView.setImageBitmap(initialImageBitmap);

        try{
            storeBitmapToCache(initialImageBitmap);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Called to rotate the image
    void rotateImage(){

        Bitmap imageBitmap = getBitmapFromCache();

        Matrix rotateMatrix = new Matrix();

        rotateMatrix.postRotate( 90);

        Bitmap rotatedImageBitmap = Bitmap.createBitmap(imageBitmap,0,0,
                imageBitmap.getWidth(), imageBitmap.getHeight(),rotateMatrix,false);

        previewImageView.setImageBitmap(rotatedImageBitmap);

        try{
            storeBitmapToCache(rotatedImageBitmap);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Called to store the bitmap of the image to the cache of the app
    void storeBitmapToCache(Bitmap imageBitmap) throws IOException {

        String fileName = "edited_image.jpg";
        File file = new File(requireActivity().getCacheDir(),fileName);
        FileOutputStream output = new FileOutputStream(file);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,90,output);
        output.flush();
        output.close();
    }

    // Called to retrive the bitmap of the image from the cache of the app
    Bitmap getBitmapFromCache(){
        File cacheFile = new File(requireActivity().getCacheDir(), "edited_image.jpg");
        Bitmap imageBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());

        return imageBitmap;
    }
}