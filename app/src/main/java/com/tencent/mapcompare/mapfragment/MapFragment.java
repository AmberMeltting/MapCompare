package com.tencent.mapcompare.mapfragment;

import android.support.v4.app.Fragment;

import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;

/**
 * Created by wangxiaokun on 16/5/10.
 */
public abstract class MapFragment extends Fragment {

    protected boolean isTouching = false;


    public boolean isTouching() {
        return isTouching;
    }

    public void setTouching(boolean touching) {
        isTouching = touching;
    }

    protected MapCameraChangedListener cameraChangedListener;

    public void setOnCameraChangedListener (MapCameraChangedListener mapCameraChangedListener) {
        cameraChangedListener = mapCameraChangedListener;
    }

    /**
     * Get current camera position.
     * @return
     */
    public abstract CameraPosition getCurrentPosition();

    /**
     * Get the map camera position.
     * @param cameraPosition
     * @return
     */
    protected abstract CameraPosition getPosition(Object cameraPosition);

    /**
     * Set map position at the specified cameraChangeObject.
     * @param cameraChangeObject
     */
    public abstract void setCameraPosition(CameraChangeObject cameraChangeObject);
}
