package com.tencent.mapcompare.bluetoothobject;

import android.os.Parcel;
import android.os.Parcelable;

import com.tencent.mapcompare.mapfragment.CameraPosition;

/**
 * Created by wangxiaokun on 16/5/12.
 */
public class CameraChangeObject implements Parcelable {
    public boolean isChanging;
    public CameraPosition cameraPosition;

    public static final CameraChangeObject.Creator<CameraChangeObject> CREATOR =
            new CameraChangeObject.Creator<CameraChangeObject>() {

                @Override
                public CameraChangeObject createFromParcel(Parcel source) {
                    return new CameraChangeObject(source);
                }

                @Override
                public CameraChangeObject[] newArray(int size) {
                    return new CameraChangeObject[size];
                }
            };

    public CameraChangeObject() {}

    public CameraChangeObject(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte)(isChanging ? 0x01 : 0x00));
        dest.writeValue(cameraPosition);
    }

    public void readFromParcel(Parcel source) {
        isChanging = source.readByte() != 0;
        cameraPosition = (CameraPosition) source.readValue(CameraPosition.class.getClassLoader());
    }
}
