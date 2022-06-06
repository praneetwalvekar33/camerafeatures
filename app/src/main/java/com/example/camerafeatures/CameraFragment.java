package com.example.camerafeatures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;

import featuresutil.DataViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // Camera lifecycle is bind to the lifecycle of the fragment
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private ImageCapture imageCapture;

    PreviewView cameraPreviewView;

    Button selfieButton;

    DataViewModel selfieDataViewModel;

    CameraSelector cameraSelector;

    Preview preview;

    ProcessCameraProvider cameraProvider;

    Camera camera;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =  inflater.inflate(R.layout.fragment_camera, container, false);

         cameraPreviewView = view.findViewById(R.id.camera_stream_preview_view);

         cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity());

         // Adding a listener to cameraProvider
         cameraProviderFuture.addListener(()->{
             try{


                 // Camera provider made available
                 cameraProvider = cameraProviderFuture.get();

                 // Set up the view finder use case to display camera preview
                 preview = new Preview.Builder().build();



                 // Choose the camera by lens facing
                 cameraSelector = new CameraSelector.Builder()
                         .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                         .build();

                 cameraProvider.unbindAll();

                 // Set up the image capture use case to take picture
                 imageCapture = new ImageCapture.Builder()
                         .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                         .setTargetRotation(view.getDisplay().getRotation())
                         .build();

                 // Attach use case to the camera with LifeCycleOwner
                 camera = cameraProvider.bindToLifecycle(( this),
                 cameraSelector, imageCapture,preview);
                 // Connect the preview to the preview view
                 preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());



         }catch(Exception e){
                e.printStackTrace();
             }
         }, ContextCompat.getMainExecutor(requireActivity()));


         // Instance of DataViewModel for this activity
         selfieDataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

         selfieButton = view.findViewById(R.id.selfie_button);

         selfieButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 clickPicture();

                 selfieDataViewModel.getBitmapItem().observe(CameraFragment.this,bitmap->{
                     cameraProvider.unbindAll();
                     getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                             .replace(R.id.fragment_container_view_camera,
                                     SelectOrDiscardSelfieFragment.class, null).addToBackStack(null)
                             .commit();

                 });
             }
         });

        return view;
    }

    void clickPicture(){

        // Taking the picture
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireActivity()),
                new ImageCapture.OnImageCapturedCallback(){
            // Called when the image is captured successfully
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.e("CameraFragment","Image Captured: Successful");
                Bitmap selfieBitmap = toBitmap(image);

                if(selfieBitmap == null){
                    Log.e("CameraFragment", "passed bitmap is null");
                }
                selfieDataViewModel.setBitmapItem(selfieBitmap);

                image.close();
            }

            // Called when there is error while capturing the image
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
                Log.e("CameraFragment","Image Captured: Unsuccessful");
            }
        });
    }

    // Called to convert the ImageProxy into bitmap
    Bitmap toBitmap(ImageProxy image){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] byteArray = new byte[buffer.capacity()];
        buffer.get(byteArray);
        byte[] clonedBytes = byteArray.clone();
        return BitmapFactory.decodeByteArray(clonedBytes,0,clonedBytes.length);
    }

}