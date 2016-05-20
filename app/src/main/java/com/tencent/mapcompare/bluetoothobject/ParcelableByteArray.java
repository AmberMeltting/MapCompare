package com.tencent.mapcompare.bluetoothobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangxiaokun on 16/5/16.
 */
public class ParcelableByteArray implements Parcelable {

    private byte[] mbytes;

    public static final Parcelable.Creator<ParcelableByteArray> CREATOR =
            new Parcelable.Creator<ParcelableByteArray>() {

                @Override
                public ParcelableByteArray createFromParcel(Parcel source) {
                    return new ParcelableByteArray(source);
                }

                @Override
                public ParcelableByteArray[] newArray(int size) {
                    return new ParcelableByteArray[size];
                }
            };

    public ParcelableByteArray(byte[] data) {
        mbytes = data;
    }

    public ParcelableByteArray(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(mbytes);
        dest.writeInt(mbytes.length);
    }

    public void readFromParcel(Parcel source) {
        mbytes = new byte[source.readInt()];
        source.readByteArray(mbytes);
    }

}
