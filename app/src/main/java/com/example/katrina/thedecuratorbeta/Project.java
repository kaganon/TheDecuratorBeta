package com.example.katrina.thedecuratorbeta;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
    private String title;
    private String budget;

    public Project() {
    }

    public Project(String title, String budget) {
        this.title = title;
        this.budget = budget;
    }

    protected Project(Parcel in) {
        title = in.readString();
        budget = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(budget);
    }
}
