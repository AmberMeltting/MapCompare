package com.tencent.mapcompare.mapfragment;

/**
 * Created by wangxiaokun on 16/5/10.
 */
public interface MapCameraChangedListener {
    public void onMapCameraChanging(CameraPosition cameraPosition);
    public void mapCameraChanged(CameraPosition cameraPosition);
}
