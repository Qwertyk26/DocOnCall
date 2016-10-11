package ru.handh.doctor;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import ru.handh.doctor.utils.Constants;

/**
 * Created by sgirn on 11.11.2015.
 * родитель всех активити
 */
public class ParentActivity extends AppCompatActivity {

   //public Retrofit retrofit;
    protected ViewFlipper viewFlipper;
//    private Gson gson = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.API_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
    }


    public void showData() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_CONTENT) {
            removeFlipAnim();
            viewFlipper.setDisplayedChild(Constants.VIEW_CONTENT);
        }
    }

    public void showProgress() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_PROGRESS) {
            removeFlipAnim();
            viewFlipper.setDisplayedChild(Constants.VIEW_PROGRESS);
        }
    }

    public void showError() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_ERROR) {
            removeFlipAnim();
            viewFlipper.setDisplayedChild(Constants.VIEW_ERROR);
        }
    }

    private void removeFlipAnim() {
        viewFlipper.setFlipInterval(0);
        viewFlipper.setInAnimation(null);
        viewFlipper.setOutAnimation(null);
    }
}
