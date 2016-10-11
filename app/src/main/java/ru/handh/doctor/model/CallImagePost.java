package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by drybochkin on 07.10.2016.
     CallImageSetRequest {
        token (string): Токен пользователя ,
        idCall (integer): Идентификатор заказа ,
        imageName (string): Имя исходного файла ,
        imageData (string): base64 изображения
     }
 */

public class CallImagePost implements Parcelable {
    public transient static final Parcelable.Creator<CallImagePost> CREATOR = new Parcelable.Creator<CallImagePost>() {
        public CallImagePost createFromParcel(Parcel source) {
            return new CallImagePost(source);
        }

        public CallImagePost[] newArray(int size) {
            return new CallImagePost[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeInt(this.idCall);
        dest.writeString(this.imageName);
        dest.writeString(this.imageData);
    }

    protected CallImagePost(Parcel in) {
        this.token = in.readString();
        this.idCall = in.readInt();
        this.imageName = in.readString();
        this.imageData = in.readString();
    }

    private String token;
    private int idCall;
    private String imageName;
    private String imageData;

    public CallImagePost(String token, int idCall, String imageName, String imageData) {
        this.token = token;
        this.idCall = idCall;
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

}
