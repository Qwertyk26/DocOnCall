package ru.handh.doctor.io.db;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import retrofit.Response;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.ModelDoctor;
import ru.handh.doctor.utils.ImageViewTopCrop;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by sergey on 07.12.15.
 * работа с базой
 */
public class DBWork {

    private Context context;
    private Realm realm;

    public DBWork(Context context) {
        this.context = context;
        RealmConfiguration config = new RealmConfiguration
                .Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
    }

    private Realm getRealm() {
        if (this.realm != null) {
            return this.realm;
        } else {
            return Realm.getInstance(context);
        }
    }

    public void setImages(final Image image) {
        Realm realm = getRealm();
        realm.beginTransaction();
        Number _maxNumber = realm.where(Image.class).max("id");
        int nextID = _maxNumber == null ? 1 : _maxNumber.intValue() + 1;
        Image tempImage = new Image();
        tempImage.setId(nextID);
        tempImage.setIdCall(image.getIdCall());
        tempImage.setSize(image.getSize());
        tempImage.setName(image.getName());
        tempImage.setUploaded(image.isUploaded());
        tempImage.setUserToken(image.getUserToken());
        tempImage.setPath(image.getPath());
        realm.copyToRealmOrUpdate(tempImage);
        realm.commitTransaction();
    }

    public RealmResults<Image> getImages(int idCall) {
        RealmQuery<Image> image = realm.where(Image.class).contains("userToken", SharedPref.getTokenUser(context)).equalTo("idCall", idCall);
        RealmResults<Image> images = image.findAll();

        return images;
    }
    public void deleteImage(final int position, int idCall) {
        RealmQuery<Image> image = realm.where(Image.class).equalTo("idCall", idCall);
        final RealmResults<Image> images = image.findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                images.get(position).removeFromRealm();
            }
        });
    }
    public void updateImage(final int position) {
        RealmQuery<Image> images =  realm.where(Image.class).contains("userToken", SharedPref.getTokenUser(context));
        final RealmResults<Image> image = images.findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                image.get(position).setUploaded(true);
            }
        });
    }
    public void doctorWriteDatabase(Response<ModelDoctor> response) {
        ModelDoctor.Doctor modelDoc = response.body().data.doctor;

        Doctor docBase = new Doctor();
        docBase.setDoctorId(modelDoc.getDoctorId());
        docBase.setName(modelDoc.getName());
        docBase.setSurname(modelDoc.getSurname());
        docBase.setMiddleName(modelDoc.getMiddleName());
        docBase.setRating(modelDoc.getRating());
        docBase.setDescription(modelDoc.getDescription());
        docBase.setDocTimeworkId(modelDoc.docTimeworkId);

        ModelDoctor.Address modelAddress = modelDoc.getAddress();

        if(modelAddress != null){
            Address address = new Address();
            address.setId(modelDoc.getAddress().getId());
            address.setAddress(modelDoc.getAddress().getAddress());
            address.setCity(modelDoc.getAddress().getCity());
            address.setStreet(modelDoc.getAddress().getStreet());
            address.setBuilding(modelDoc.getAddress().getBuilding());
            address.setHouse(modelDoc.getAddress().getHouse());
            address.setPorch(modelDoc.getAddress().getPorch());
            address.setFlat(modelDoc.getAddress().getFlat());
            address.setFloor(modelDoc.getAddress().getFloor());
            address.setIntercom(modelDoc.getAddress().getIntercom());
            address.setStructure(modelDoc.getAddress().getStructure());
            address.setLongitude(modelDoc.getAddress().getLongitude());
            address.setLatitude(modelDoc.getAddress().getLatitude());

            docBase.setAddress(address);
        }

        RealmList<SpecializationDoc> specializationDocList = new RealmList<>();
        for (int i = 0; i < modelDoc.getSpecializations().size(); i++) {
            SpecializationDoc sd = new SpecializationDoc();
            sd.setId(modelDoc.getSpecializations().get(i).id);
            sd.setName(modelDoc.getSpecializations().get(i).name);
            specializationDocList.add(sd);
        }
        docBase.setSpecializations(specializationDocList);


        RealmList<Image> images = new RealmList<>();
        for (int i = 0; i < modelDoc.getImages().size(); i++) {
            Image image = new Image();
            image.setPath(modelDoc.getImages().get(i).getPath());
            image.setSize(modelDoc.getImages().get(i).getSize());
            images.add(image);
        }
        docBase.setImages(images);

        RealmList<Image> imagesSquare = new RealmList<>();
        for (int i = 0; i < modelDoc.getImages_square().size(); i++) {
            Image image = new Image();
            image.setPath(modelDoc.getImages_square().get(i).getPath());
            image.setSize(modelDoc.getImages_square().get(i).getSize());
            imagesSquare.add(image);
        }
        docBase.setImages_square(imagesSquare);

        Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(docBase);
        realm.commitTransaction();
    }

    public void setDocDataToInterface(ImageView photo, TextView name, TextView surname, TextView middleName) {
        Realm realm = getRealm();
        RealmQuery<Doctor> query = realm.where(Doctor.class);
        RealmResults<Doctor> doctors = query.findAll();

        if (doctors.get(0).getImages_square().size() != 0) {
            Picasso.with(context)
                    .load(doctors.get(0).getImages_square().get(0).getPath())
                    //.placeholder(R.drawable.simple_photo_doctor)
                    //.error(R.drawable.simple_photo_doctor)
                    .into(photo);
        }

        if(name == null)
            return;

        if (surname == null) {
            name.setText(doctors.get(0).getSurname() + " " + doctors.get(0).getName() + " " + doctors.get(0).getMiddleName());
        } else {
            name.setText(doctors.get(0).getName());
            surname.setText(doctors.get(0).getSurname());
            middleName.setText(doctors.get(0).getMiddleName());
        }

    }

    public Doctor getDoctor()
    {
        Realm realm = getRealm();
        RealmQuery<Doctor> query = realm.where(Doctor.class);
        RealmResults<Doctor> doctors = query.findAll();
        return doctors.get(0);
    }

    public boolean isHourly() {
        Realm realm = getRealm();
        RealmQuery<Doctor> query = realm.where(Doctor.class);
        RealmResults<Doctor> doctors = query.findAll();

        if(doctors.size()>0) {
            return doctors.get(0).isDocTimeworkId();
        }
        return false;
    }

    public void closeDB() {
        Realm realm = getRealm();
        if (!realm.isClosed())
            realm.close();
    }
}
