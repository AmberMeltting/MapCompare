// IMCAidlInterface.aidl
package com.tencent.mapcompare.main;

// Declare any non-default types here with import statements

import com.tencent.mapcompare.bluetoothobject.ParcelableByteArray;
import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.mapcompare.main.IServiceCallback;

interface IMCAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

//    void getObject(inout CameraPosition cameraPosition);

    void getCameraChangeObejct(in CameraChangeObject cameraChangeObject);
    void connectToDevice(in BluetoothDevice device);
    void setServiceCallback(in IServiceCallback callback);
}
