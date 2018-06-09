package com.kevinersoy.flickrsearch.models;

import com.google.gson.annotations.SerializedName;

public class GetPhotosResponse {

    @SerializedName("photos")
    PhotoMeta mPhotoMeta;

    @SerializedName("stat")
    String mStat;

    public PhotoMeta getPhotoMeta() {
        return mPhotoMeta;
    }

    public void setPhotoMeta(PhotoMeta photoMeta) {
        mPhotoMeta = photoMeta;
    }

    public String getStat() {
        return mStat;
    }

    public void setStat(String stat) {
        mStat = stat;
    }
}
