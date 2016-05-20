package com.tencent.mapcompare.mapfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;


/**
 * Created by wangxiaokun on 16/5/4.
 */
public class TencentVectorMapFragment extends MapFragment implements TencentMap.OnCameraChangeListener{
    private MapView mMapView;
    private TencentMap mTencentMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMapView = new MapView(getActivity()){
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouching = false;
                        break;
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(lp);
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isTouching = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouching = false;
                        break;
                }
                return false;
            }
        });
        mTencentMap = mMapView.getMap();
        mTencentMap.setOnCameraChangeListener(this);
        return mMapView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public com.tencent.mapcompare.mapfragment.CameraPosition getCurrentPosition() {
        return getPosition(mTencentMap.getCameraPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraChangedListener != null) {
            cameraChangedListener.onMapCameraChanging(getPosition(cameraPosition));
        }
    }

    @Override
    public void onCameraChangeFinished(CameraPosition cameraPosition) {
        if (cameraChangedListener != null) {
            cameraChangedListener.mapCameraChanged(getPosition(cameraPosition));
        }
    }

    @Override
    public void setCameraPosition(CameraChangeObject cameraChangeObject) {
        com.tencent.mapcompare.mapfragment.CameraPosition cameraPosition = cameraChangeObject.cameraPosition;
        CameraPosition cp = CameraPosition.builder().
                target(new LatLng(cameraPosition.getLatitude(), cameraPosition.getLongitude())).
                zoom((float) cameraPosition.getZoomLevel()).
                rotate((float)cameraPosition.getRotation()).
                skew((float)cameraPosition.getTilt()).
                build();
        if (cameraChangeObject.isChanging) {
            mTencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        } else {
            mTencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        }
    }

    @Override
    protected com.tencent.mapcompare.mapfragment.CameraPosition getPosition(Object cameraPosition) {
        com.tencent.mapcompare.mapfragment.CameraPosition position = new com.tencent.mapcompare.mapfragment.CameraPosition();
        position.setLatitude(((CameraPosition)cameraPosition).target.latitude);
        position.setLongitude(((CameraPosition)cameraPosition).target.longitude);
        position.setZoomLevel(((CameraPosition)cameraPosition).zoom);
        position.setRotation(((CameraPosition)cameraPosition).rotate);
        position.setTilt(((CameraPosition)cameraPosition).skew);
        return position;
    }
}
