package featuresutil;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageToGallary {

    public static void saveImage(){}

    @TargetApi(Build.VERSION_CODES.Q)
    public static void insertImagesSDKQAndGreater(Context context, Bitmap imageBitmap) {
        String fileName = createFileName();

        ContentResolver contentResolver = context.getContentResolver();


        // Adding details of the image file in the ContentValue variable
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);


        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);


        OutputStream fileOutputStream = null;
        try {
            fileOutputStream = contentResolver.openOutputStream(imageUri);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,90, fileOutputStream);
            fileOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        contentValues.clear();

        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
        contentResolver.update(imageUri, contentValues, null, null);
    }

    public static void insertImagesBelowSDKQ(Bitmap imageBitmap) {
        String fileName = createFileName();

        File file = new File(Environment.DIRECTORY_PICTURES, fileName);

        try {
            // Creating a bitmap from the Uri of the image
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String createFileName(){
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";

        return fileName;
    }
}
