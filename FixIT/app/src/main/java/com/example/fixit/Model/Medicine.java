package com.example.fixit.Model;
public class Medicine {
    private String mId;
    private String mName;
    private String mCategory;
    private double mPrice;

    private String mTimestamp;
    private String mUid;
    private String mImage;

    // Constructors, getters, and setters

    public Medicine() {
        // Default constructor required for Firestore deserialization
    }

    public Medicine(String mId, String mName, String mCategory, double mPrice, String mTimestamp, String mUid, String mImage) {
        this.mId = mId;
        this.mName = mName;
        this.mCategory = mCategory;
        this.mPrice = mPrice;
        this.mTimestamp = mTimestamp;
        this.mUid = mUid;
        this.mImage = mImage;
    }

    public String getMId() {
        return mId;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public String getMCategory() {
        return mCategory;
    }

    public void setMCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public double getMPrice() {
        return mPrice;
    }

    public void setMPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public String getMTimestamp() {
        return mTimestamp;
    }

    public void setMTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getMUid() {
        return mUid;
    }

    public void setMUid(String mUid) {
        this.mUid = mUid;
    }

    public String getMImage() {
        return mImage;
    }

    public void setMImage(String mImage) {
        this.mImage = mImage;
    }
}
