package ru.handh.doctor.ui.calls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import ru.handh.doctor.R;
import ru.handh.doctor.io.db.DBWork;
import ru.handh.doctor.io.db.Image;
import ru.handh.doctor.ui.history.AdapterHistory;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.SquareImage;
import ru.handh.doctor.utils.Utils;

/**
 * Created by antonnikitin on 03.10.16.
 */

public class AdapterNurseImages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    RealmResults<Image> bitmaps;
    DBWork dbWork;

    public AdapterNurseImages(Context mContext, RealmResults<Image> mBitmaps) {
        this.context = mContext;
        bitmaps = mBitmaps;
        dbWork = new DBWork(context);
    }
    public void udpateImages(RealmResults<Image> images) {
        bitmaps = images;
        notifyDataSetChanged();
    }
    public void addBitmap(Image image) {
        notifyItemInserted(bitmaps.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nurse_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = new File(bitmaps.get(position).getPath());
        Bitmap bitmap = decodeBitmapFromFile(file);
        ((ViewHolder) holder).nurseImage.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_nurse)
        SquareImage nurseImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
