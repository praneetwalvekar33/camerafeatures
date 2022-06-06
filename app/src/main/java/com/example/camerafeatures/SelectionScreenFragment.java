package com.example.camerafeatures;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectionScreenFragment#} factory method to
 * create an instance of this fragment.
 */
public class SelectionScreenFragment extends Fragment {

    Button selectImageFromGallaryButton;

    Button selectImageFromCameraButton;

    ImageView previewImageView;

    // Called to get edited image from the EditImageActivity
    ActivityResultLauncher<Intent> mGetImageFromEditActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){

                        Bitmap imageBitmap = null;
                        if(result.getData().hasExtra("data")) {

                            // Converting the obtained ByteArray to bitmap
                            imageBitmap = BitmapFactory.decodeByteArray(result.getData().getByteArrayExtra(
                                    "data"), 0 , result.getData().getByteArrayExtra("data")
                                    .length);
                        }

                        previewImageView.setImageBitmap(imageBitmap);
                    }

                    if(result.getResultCode() != Activity.RESULT_OK){
                        Log.e("SelectionScreenFragment", "Result has not passed and" +
                                " result code is " + result.getResultCode());
                    }
                }
            }
    );

    // Called when the image has to select from gallary and the native camera
    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){

                        // When image is captured from native camera
                        Intent editImageIntent = new Intent(requireActivity(),
                                EditImageActivity.class);

                        Bitmap imageBitmap = null;
                        if(result.getData().hasExtra("data")) {

                            imageBitmap = BitmapFactory.decodeByteArray(result.getData().getByteArrayExtra(
                                    "data"), 0 , result.getData().getByteArrayExtra("data")
                                    .length);


                            previewImageView.setImageBitmap(imageBitmap);
                        }else{

                            // When the image taken from gallary
                            Uri imageUri = result.getData().getData();

                            imageBitmap = uriToBitmap(imageUri);
                        }

                        // Converting the Bitmap to ByteArray so that it can be passed to the next activity
                        ByteArrayOutputStream bitmapArray = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bitmapArray);
                        editImageIntent.putExtra("image_Array",bitmapArray.toByteArray());

                        mGetImageFromEditActivity.launch(editImageIntent);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selection_screen, container, false);

        selectImageFromGallaryButton = (Button) view.findViewById(R.id.button_select_image_from_gallary);
        selectImageFromGallaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelector();
            }

        });


        previewImageView = (ImageView) view.findViewById(R.id.image_view_display);

        selectImageFromCameraButton = (Button) view.findViewById(R.id.button_select_image_from_camera);
        selectImageFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingInAppCamera();
            }
        });

        return view;
    }


    // Called to capture image from native camera
    protected void usingInAppCamera(){
        Intent intent = new Intent(requireActivity(),CameraActivity.class);

        mGetContent.launch(intent);

    }

    // Called to select image from gallary
    protected void imageSelector(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        mGetContent.launch(intent);

    }

    // Called to convert Uri to Bitmap
    Bitmap uriToBitmap(Uri uri){

        ContentResolver contentResolver = requireActivity().getContentResolver();
        Bitmap bitmap = null;
        try {
            // Creating a bitmap from the Uri of the image
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source src = ImageDecoder.createSource(contentResolver, uri);
                bitmap = ImageDecoder.decodeBitmap(src);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }

}