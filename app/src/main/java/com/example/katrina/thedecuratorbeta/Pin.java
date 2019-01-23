package com.example.katrina.thedecuratorbeta;

import android.os.Parcel;
import android.os.Parcelable;

public class Pin implements Parcelable {
    private String price;
    private String imgUrl;
    private String id;

    public Pin() {
    }

    public Pin(String price, String imgUrl, String id) {
        this.price = price;
        this.imgUrl = imgUrl;
        this.id = id;
    }

    protected Pin(Parcel in) {
        price = in.readString();
        imgUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel in) {
            return new Pin(in);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(price);
        dest.writeString(imgUrl);
        dest.writeString(id);
    }
}
