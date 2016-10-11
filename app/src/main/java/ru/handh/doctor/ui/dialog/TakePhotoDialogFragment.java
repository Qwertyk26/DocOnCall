package ru.handh.doctor.ui.dialog;

import android.Manifest;

import android.app.Activity;
import android.app.Dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.Func;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.handh.doctor.R;
import ru.handh.doctor.event.TakePhotoCommandEvent;
import ru.handh.doctor.gcm.UploadImagesService;
import ru.handh.doctor.io.db.DBWork;
import ru.handh.doctor.io.db.Image;
import ru.handh.doctor.ui.calls.AdapterNurseImages;
import ru.handh.doctor.ui.calls.FragmentCallDetail;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.DialogClickListener;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.RecyclerItemClickListener;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by antonnikitin on 02.10.16.
 */

public class TakePhotoDialogFragment extends AppCompatDialogFragment {
    RecyclerView recycler_view;
    private Unbinder unbinder;
    @BindView(R.id.preview_surface)
    TextureView previewSurface;
    @BindView(R.id.take_photo_btn)
    Button takePhotoBtn;
    @BindView(R.id.frame_layout)
    FrameLayout contentLayout;
    @BindView(R.id.image_detail) FrameLayout imageDetailLayout;
    @BindView(R.id.skipBtn) Button skipBtn;
    @BindView(R.id.continueBtn) Button continueBtn;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.cancelBtn) Button cancelBtn;
    private ImagesPagerAdapter imagesPagerAdapter;
    private static final String[] REQUEST_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION_CODE = 233;
    private RxCamera camera;
    private static final String TAG = "TakePhotoDialogFragment";
    private AdapterNurseImages adapterNurseImages;
    private int height;
    private int width;
    private MainActivity ma = (MainActivity) getActivity();
    private int idCall;
    private String token;
    private UploadImageBroadcast uploadImageBroadcast;
    private Intent uploadImageIntent;
    private IntentFilter intentFilter;
    private DBWork dbWork;
    private DialogClickListener callback;

    public static TakePhotoDialogFragment newInstance(int idCall, String token) {
        Bundle args = new Bundle();
        args.putInt("idCall", idCall);
        args.putString("token", token);
        TakePhotoDialogFragment fragment = new TakePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getTargetFragment().getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        idCall = getArguments().getInt("idCall");
        token = getArguments().getString("token");
        uploadImageBroadcast = new UploadImageBroadcast();
        intentFilter = new IntentFilter(
                UploadImagesService.ACTION_UPDATE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        dbWork = new DBWork(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(uploadImageBroadcast, intentFilter);
        if (!checkPermission()) {
            requestPermission();
        } else {
            openCamera();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_take_photo, container, false);
        getDialog().setTitle("Назначение врача");
        unbinder = ButterKnife.bind(this, rootView);
        recycler_view = new RecyclerView(getContext());
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 150);
        recycler_view.setLayoutParams(params);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setHasFixedSize(true);
        previewSurface.setOpaque(false);
        contentLayout.addView(recycler_view);
        adapterNurseImages = new AdapterNurseImages(getContext(), dbWork.getImages(idCall));
        imagesPagerAdapter = new ImagesPagerAdapter(getContext(), dbWork.getImages(idCall));
        if (dbWork.getImages(idCall).size() != 0) {
            cancelBtn.setVisibility(View.VISIBLE);
            skipBtn.setVisibility(View.GONE);
            continueBtn.setVisibility(View.VISIBLE);
        }
        recycler_view.setAdapter(adapterNurseImages);
        viewPager.setAdapter(imagesPagerAdapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                contentLayout.setVisibility(View.GONE);
                imageDetailLayout.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(position);
                Button deleteButton = new Button(getContext());
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
                        int position = viewPager.getCurrentItem();
                        if (imagesPagerAdapter.bitmaps.get(position).isUploaded()) {
                            Toast.makeText(ma, getResources().getString(R.string.error_image_delete), Toast.LENGTH_SHORT).show();
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
        return rootView;
    }
    @OnClick(R.id.take_photo_btn)
    void takePhoto() {
        //Realm realm = Realm.getInstance(getContext());
        //final RealmQuery<Image> image = realm.where(Image.class).contains("userToken", token).equalTo("idCall", idCall);
        //if (image.count() < Constants.MAX_NURSE_PHOTO_COUNT || Constants.MAX_NURSE_PHOTO_COUNT == 0) {
            cancelBtn.setVisibility(View.VISIBLE);
            skipBtn.setVisibility(View.GONE);
            continueBtn.setVisibility(View.VISIBLE);
            requestTakePicture();
        //} else {
          //  Toast.makeText(getContext(), String.format("Нельзя прикрепить больше %d фотографий", Constants.MAX_NURSE_PHOTO_COUNT), Toast.LENGTH_SHORT).show();;
        //}
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(getTargetFragment().getActivity(), REQUEST_PERMISSIONS, REQUEST_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
    private void openCamera() {
        RxCameraConfig config = new RxCameraConfig.Builder()
                .useBackCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(width, height), false)
                .setHandleSurfaceEvent(true)
                .build();
        RxCamera.open(getContext(), config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d("isopen: " + rxCamera.isOpenCamera() + ", thread: " + Thread.currentThread());
                camera = rxCamera;
                return rxCamera.bindTexture(previewSurface);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d("isbindsurface: " + rxCamera.isBindSurface() + ", thread: " + Thread.currentThread());
                return rxCamera.startPreview();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RxCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("open camera error: " + e.getMessage());
            }

            @Override
            public void onNext(final RxCamera rxCamera) {
                camera = rxCamera;
                camera.action().flashAction(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.closeCamera();
        }
        dbWork.closeDB();
        getContext().unregisterReceiver(uploadImageBroadcast);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dbWork.closeDB();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    private boolean checkCamera() {
        if (camera == null || !camera.isOpenCamera()) {
            return false;
        }
        return true;
    }
    private void requestTakePicture() {
        if (!checkCamera()) {
            return;
        }
        camera.request().takePictureRequest(true, new Func() {
            @Override
            public void call() {
                Log.d(TAG, "Captured!");
            }
        }, height, width, ImageFormat.JPEG, true).subscribe(new Action1<RxCameraData>() {
            @Override
            public void call(RxCameraData rxCameraData) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(rxCameraData.cameraData, 0, rxCameraData.cameraData.length);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            rxCameraData.rotateMatrix, false);
                    File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"." +
                        getResources().getString(R.string.app_name));
                    if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(getResources().getString(R.string.app_name), "Oops! Failed create "
                                + getResources().getString(R.string.app_name) + " directory");
                        }
                    }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                String fileName = "IMG_" + timeStamp;
                File file = new File(mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    Image image = new Image();
                    image.setPath(file.getPath());
                    image.setName(fileName + ".jpg");
                    image.setIdCall(idCall);
                    image.setSize(String.valueOf(file.length()));
                    image.setUserToken(SharedPref.getTokenUser(getContext()));
                    image.setUploaded(false);
                    dbWork.setImages(image);
                    adapterNurseImages.addBitmap(image);
                    imagesPagerAdapter.addBitmap(image);
                    file.isHidden();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @OnClick(R.id.continueBtn)
    void continueClick() {
        uploadImageIntent = new Intent(getContext(), UploadImagesService.class);
        uploadImageIntent.putExtra("idCall", idCall);
        uploadImageIntent.putExtra("token", token);
        getContext().startService(uploadImageIntent);
        EventBus.getDefault().post(new TakePhotoCommandEvent("CONTINUE"));
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ma = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " Not MainActivity class instance");
        }
    }

    @OnClick(R.id.cancelBtn)
    void cancelClick() {
        getDialog().dismiss();
    }
    @OnClick(R.id.photo_btn_back)
    void backClick() {
        contentLayout.setVisibility(View.VISIBLE);
        imageDetailLayout.setVisibility(View.GONE);
    }
    @OnClick(R.id.skipBtn)
    void skipClick() {
        EventBus.getDefault().post(new TakePhotoCommandEvent("SKIP"));
        getDialog().dismiss();
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : REQUEST_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    class ImagesPagerAdapter extends PagerAdapter {
        RealmResults<Image> bitmaps;
        Context context;
        private LayoutInflater layoutInflater;

        public ImagesPagerAdapter(Context mContext, RealmResults<Image> mImages) {
            context = mContext;
            bitmaps = mImages;
        }
        public void udpateImages(RealmResults<Image> images) {
            bitmaps = images;
            notifyDataSetChanged();
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
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_image_pager, container, false);
            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.single_image);
            Image bitmap = bitmaps.get(position);
            Uri uri = Uri.parse(bitmap.getPath());
            imageViewPreview.setImageURI(uri);
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
                ma.showUploadProgress();
            } else if (!result) {
                ma.hideUploadProgress();
            }
        }
    }

}
