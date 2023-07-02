package com.example.fixit.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DoctorModel implements Parcelable {
    private String name;
    private String location;
    private String specialization;
    private String contact;
    private String did;
    private String timestamp;
    private String uid;
    private String image;

    public DoctorModel() {
        // Default constructor required for Firebase
    }

    public DoctorModel(String name, String location, String specialization, String contact, String did, String timestamp, String uid, String image) {
        this.name = name;
        this.location = location;
        this.specialization = specialization;
        this.contact = contact;
        this.did = did;
        this.timestamp = timestamp;
        this.uid = uid;
        this.image = image;
    }

    protected DoctorModel(Parcel in) {
        name = in.readString();
        location = in.readString();
        specialization = in.readString();
        contact = in.readString();
        did = in.readString();
        timestamp = in.readString();
        uid = in.readString();
        image = in.readString();
    }

    public static final Creator<DoctorModel> CREATOR = new Creator<DoctorModel>() {
        @Override
        public DoctorModel createFromParcel(Parcel in) {
            return new DoctorModel(in);
        }

        @Override
        public DoctorModel[] newArray(int size) {
            return new DoctorModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getContact() {
        return contact;
    }

    public String getDid() {
        return did;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeString(specialization);
        parcel.writeString(contact);
        parcel.writeString(did);
        parcel.writeString(timestamp);
        parcel.writeString(uid);
        parcel.writeString(image);
    }

    // You can also include setters if needed

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
