package ru.handh.doctor.ui.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmResults;
import ru.handh.doctor.R;
import ru.handh.doctor.event.ShowHideProgressEvent;
import ru.handh.doctor.event.TakePhotoCommandEvent;
import ru.handh.doctor.gcm.UploadImagesService;
import ru.handh.doctor.io.db.DBWork;
import ru.handh.doctor.io.db.Image;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.RecyclerItemClickListener;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by antonnikitin on 20.10.16.
 */

public class ActivityTakePhoto extends AppCompatActivity implements CameraHostProvider {
    RecyclerView recycler_view;
    private Unbinder unbinder;
    @BindView(R.id.camera_view)
    CameraView cameraView;
    @BindView(R.id.take_photo_btn)
    Button takePhotoBtn;
    @BindView(R.id.frame_layout)
    FrameLayout contentLayout;
    @BindView(R.id.image_detail) FrameLayout imageDetailLayout;
    @BindView(R.id.skipBtn) Button skipBtn;
    @BindView(R.id.continueBtn) Button continueBtn;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.photo_btn_back)
    ImageButton backBtn;
    @BindView(R.id.cancelBtn) Button cancelBtn;
    private ImagesPagerAdapter imagesPagerAdapter;
    private static final String TAG = "ActivityTakePhoto";
    private AdapterNurseImages adapterNurseImages;
    private Intent uploadImageIntent;
    private IntentFilter intentFilter;
    private DBWork dbWork;
    private int destWidth;
    private int destHeight;
    org.apache.log4j.Logger log;
    private int idCall;
    private String token;
    private UploadImageBroadcast uploadImageBroadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        Display display = getWindowManager().getDefaultDisplay();
        unbinder = ButterKnife.bind(this);
        idCall = getIntent().getIntExtra("idCall", 0);
        token = getIntent().getStringExtra("tokenUser");
        uploadImageBroadcast = new UploadImageBroadcast();
        intentFilter = new IntentFilter(
                UploadImagesService.ACTION_UPDATE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        dbWork = new DBWork(this);
        log = Log4jHelper.getLogger(TAG);
        recycler_view = new RecyclerView(this);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150);
        recycler_view.setLayoutParams(params);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setHasFixedSize(true);
        contentLayout.addView(recycler_view);
        adapterNurseImages = new AdapterNurseImages(this, dbWork.getImages(idCall));
        imagesPagerAdapter = new ImagesPagerAdapter(this, dbWork.getImages(idCall));
        if (dbWork.getImages(idCall).size() != 0) {
            cancelBtn.setVisibility(View.VISIBLE);
            skipBtn.setVisibility(View.GONE);
            continueBtn.setVisibility(View.VISIBLE);
        }
        recycler_view.setAdapter(adapterNurseImages);
        viewPager.setAdapter(imagesPagerAdapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                log.info(TAG + " image item clicked " + position);
                contentLayout.setVisibility(View.GONE);
                imageDetailLayout.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(position);
                Button deleteButton = new Button(ActivityTakePhoto.this);
                FrameLayout.LayoutParams params =
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                deleteButton.setLayoutParams(params);
                deleteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                deleteButton.setTextColor(getResources().getColor(android.R.color.white));
                deleteButton.setText(R.string.delete_action);
                imageDetailLayout.addView(deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        log.info(TAG + " delete image clicked " + position);
                        int position = viewPager.getCurrentItem();
                        if (imagesPagerAdapter.bitmaps.get(position).isUploaded()) {
                            Toast.makeText(ActivityTakePhoto.this, getResources().getString(R.string.error_image_delete), Toast.LENGTH_SHORT).show();
                        } else {
                            dbWork.deleteImage(position, idCall);
                            adapterNurseImages.notifyDataSetChanged();
                            imagesPagerAdapter.notifyDataSetChanged();
                            if (imagesPagerAdapter.bitmaps.size() == 0) {
                                contentLayout.setVisibility(View.VISIBLE);
                                imageDetailLayout.setVisibility(View.GONE);
                                continueBtn.setVisibility(View.GONE);
                                skipBtn.setVisibility(View.VISIBLE);
                                cancelBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBtn.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.GONE);
                continueBtn.setVisibility(View.VISIBLE);
                cameraView.autoFocus();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageIntent = new Intent(ActivityTakePhoto.this, UploadImagesService.class);
                uploadImageIntent.putExtra("idCall", idCall);
                uploadImageIntent.putExtra("token", token);
                startService(uploadImageIntent);
                EventBus.getDefault().post(new TakePhotoCommandEvent("CONTINUE"));
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbWork.getImages(idCall).size() > 0) {
                    uploadImageIntent = new Intent(ActivityTakePhoto.this, UploadImagesService.class);
                    uploadImageIntent.putExtra("idCall", idCall);
                    uploadImageIntent.putExtra("token", token);
                    startService(uploadImageIntent);
                    finish();
                } else {
                    finish();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentLayout.setVisibility(View.VISIBLE);
                imageDetailLayout.setVisibility(View.GONE);
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbWork.getImages(idCall).size() != 0) {
                    uploadImageIntent = new Intent(ActivityTakePhoto.this, UploadImagesService.class);
                    uploadImageIntent.putExtra("idCall", idCall);
                    uploadImageIntent.putExtra("token", token);
                    startService(uploadImageIntent);
                    EventBus.getDefault().post(new TakePhotoCommandEvent("SKIP"));
                    finish();
                } else {
                    EventBus.getDefault().post(new TakePhotoCommandEvent("SKIP"));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
        registerReceiver(uploadImageBroadcast, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbWork.closeDB();
        unregisterReceiver(uploadImageBroadcast);
    }

    @Override
    public CameraHost getCameraHost() {
        return new MyCameraHost(this);
    }

    class MyCameraHost extends SimpleCameraHost {

        private Camera.Size previewSize;

        public MyCameraHost(Context ctxt) {
            super(ctxt);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        @Override
        public Camera.ShutterCallback getShutterCallback() {
            return null;
        }

        @Override
        public void onAutoFocus(boolean success, final Camera _camera) {
            super.onAutoFocus(success, _camera);
            Camera.Parameters parameters = _camera.getParameters();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                parameters.set("orientation", "portrait");
                parameters.set("rotation",90);
            } else {
                parameters.set("orientation", "landscape");
                parameters.set("rotation", 90);
            }
            parameters.set("orientation", "portrait");
            _camera.setParameters(parameters);
            if (success) {
                _camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        File mediaStorageDir = new File(
                                Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "." +
                                getResources().getString(R.string.app_name));
                        if (!mediaStorageDir.exists()) {
                            if (!mediaStorageDir.mkdirs()) {
                                log.error(String.format(getResources().getString(R.string.app_name) + "Oops! Failed create "
                                        + getResources().getString(R.string.app_name) + " directory"));
                            }
                        }
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String fileName = "IMG_" + timeStamp;
                        File file = new File(mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
                        try {
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            final BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = calculateInSampleSize(options, destWidth, destHeight);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            options.inJustDecodeBounds = false;
                            fos.close();
                            Image image = new Image();
                            image.setPath(file.getPath());
                            image.setName(fileName + ".jpg");
                            image.setIdCall(idCall);
                            image.setSize(String.valueOf(file.length()));
                            image.setUserToken(SharedPref.getTokenUser(ActivityTakePhoto.this));
                            image.setUploaded(false);
                            dbWork.setImages(image);
                            adapterNurseImages.addBitmap(image);
                            imagesPagerAdapter.addBitmap(image);
                            file.isHidden();
                            _camera.stopPreview();
                            _camera.startPreview();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onCameraFail(FailureReason reason) {
            super.onCameraFail(reason);
            Toast.makeText(ActivityTakePhoto.this, "Не удалось получить доступ к камере", Toast.LENGTH_SHORT).show();
            takePhotoBtn.setEnabled(false);
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return previewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            previewSize = parameters1.getPreviewSize();
            return parameters1;
        }
    }
    class ImagesPagerAdapter extends PagerAdapter {
        RealmResults<Image> bitmaps;
        Context context;
        private LayoutInflater layoutInflater;

        public ImagesPagerAdapter(Context mContext, RealmResults<Image> mImages) {
            context = mContext;
            bitmaps = mImages;
        }
        public void addBitmap(Image images) {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return bitmaps.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_image_pager, container, false);
            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.single_image);
            File file = new File(bitmaps.get(position).getPath());
            Bitmap bitmap = decodeBitmapFromFile(file);
            imageViewPreview.setImageBitmap(bitmap);
            container.addView(view);

            return view;
        }
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public int getItemPosition(Object object) {
            if (bitmaps.contains((View) object)) {
                return bitmaps.indexOf((View) object);
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class UploadImageBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra("status", false);
            if (result) {
                EventBus.getDefault().post(new ShowHideProgressEvent("SHOW"));
            } else if (!result) {
                EventBus.getDefault().post(new ShowHideProgressEvent("HIDE"));
            }
        }
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
   public Bitmap decodeBitmapFromFile(File imageFile) {
       BitmapFactory.Options bmOptions = new BitmapFactory.Options();
       bmOptions.inJustDecodeBounds = true;
       BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
       bmOptions.inSampleSize = calculateInSampleSize(bmOptions, 200, 200);
       bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
       bmOptions.inJustDecodeBounds = false;
       return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
   }
}
