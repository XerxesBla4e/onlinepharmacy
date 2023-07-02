package com.example.fixit.Model;

public class MedicineModel {
    private String mName;
    private String mCategory;
    private String mPrice;
    private String mId;
    private String mTimestamp;
    private String mUid;
    private String mImage;

    public MedicineModel() {
    }

    public MedicineModel(String mName, String mCategory, String mPrice, String mId, String mTimestamp, String mUid, String mImage) {
        this.mName = mName;
        this.mCategory = mCategory;
        this.mPrice = mPrice;
        this.mId = mId;
        this.mTimestamp = mTimestamp;
        this.mUid = mUid;
        this.mImage = mImage;
    }

    public void setMImage(String mImage) {
        this.mImage = mImage;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public void setMCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public void setMPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public void setMTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public void setMUid(String mUid) {
        this.mUid = mUid;
    }

    public void seMImage(String mImage) {
        this.mImage = mImage;
    }

    public String getMName() {
        return mName;
    }

    public String getMCategory() {
        return mCategory;
    }

    public String getMPrice() {
        return mPrice;
    }

    public String getMId() {
        return mId;
    }

    public String getMTimestamp() {
        return mTimestamp;
    }

    public String getMUid() {
        return mUid;
    }

    public String getMImage() {
        return mImage;
    }
}
