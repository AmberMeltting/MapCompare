package com.tencent.mapcompare.mapfragment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangxiaokun on 16/5/10.
 */
public class CameraPosition implements Parcelable {
    private double latitude;
    private double longitude;
    private double zoomLevel;
    private double rotation;
    private double tilt;

    public static final CameraPosition.Creator<CameraPosition> CREATOR =
            new CameraPosition.Creator<CameraPosition>() {
                @Override
                public CameraPosition createFromParcel(Parcel source) {
                    return new CameraPosition(source);
                }

                @Override
                public CameraPosition[] newArray(int size) {
                    return new CameraPosition[size];
                }
            };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getTilt() {
        return tilt;
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    public CameraPosition(){};

    public CameraPosition(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(zoomLevel);
        dest.writeDouble(rotation);
        dest.writeDouble(tilt);
    }

    public void readFromParcel(Parcel source) {
        latitude = source.readDouble();
        longitude = source.readDouble();
        zoomLevel = source.readDouble();
        rotation = source.readDouble();
        tilt = source.readDouble();
    }
}
