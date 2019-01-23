package com.example.katrina.thedecuratorbeta;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId;

    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    protected User(Parcel in) {
        userId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
    }
}
