package featuresutil;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> bitmapItem = new MutableLiveData<>();



    public void setBitmapItem(Bitmap bitmap){
        bitmapItem.setValue(bitmap);
    }

    public LiveData<Bitmap> getBitmapItem(){
        return bitmapItem;
    }
}
