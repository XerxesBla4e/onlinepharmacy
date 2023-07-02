package com.example.fixit.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Orders implements Parcelable {
    private String orderID;
    private String orderTime;
    private String orderStatus;
    private String orderBy;

    public Orders() {
    }

    public Orders(String orderID, String orderTime, String orderStatus, String orderBy) {
        this.orderID = orderID;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.orderBy = orderBy;
    }

    protected Orders(Parcel in) {
        orderID = in.readString();
        orderTime = in.readString();
        orderStatus = in.readString();
        orderBy = in.readString();
    }

    public static final Creator<Orders> CREATOR = new Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(orderID);
        parcel.writeString(orderTime);
        parcel.writeString(orderStatus);
        parcel.writeString(orderBy);
    }
}
