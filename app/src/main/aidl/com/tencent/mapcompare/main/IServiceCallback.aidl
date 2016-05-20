// ICameraChangeCallback.aidl
package com.tencent.mapcompare.main;

// Declare any non-default types here with import statements
import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.mapcompare.mapfragment.CameraPosition;
//parcelable CameraChangeObject;

interface IServiceCallback {
    /**
     * When the map camera draged by finger, will call this method.
     */
    void onGetCameraChangeObject(in CameraChangeObject cameraChangeObject);

    /**
     * When devices connected, will send the server device map camera.
     */
    CameraPosition getMapCenter();

    /**
     * When devices connected success, will call this.
     */
    void onConnectSuccess(in CameraChangeObject cameraChangeObject);
}
