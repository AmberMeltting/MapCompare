package com.tencent.mapcompare.mapfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.mapcompare.util.Log;
import com.tencent.mapsdk.raster.model.CameraPosition;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.tencentmap.mapsdk.map.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

/**
 * Created by wangxiaokun on 16/5/4.
 */
public class TencentRasterMapFragment extends MapFragment implements TencentMap.OnMapCameraChangeListener{
    private MapView mMapView;
    private TencentMap mTencentMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMapView = new MapView(getActivity());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(lp);
        mMapView.onCreate(savedInstanceState);
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
        mTencentMap.setOnMapCameraChangeListener(this);
        Log.d("raster fragment create view");
        return mMapView;
    }

    @Override
    public com.tencent.mapcompare.mapfragment.CameraPosition getCurrentPosition() {
        com.tencent.mapcompare.mapfragment.CameraPosition position = new com.tencent.mapcompare.mapfragment.CameraPosition();
        position.setLatitude(mTencentMap.getMapCenter().getLatitude());
        position.setLongitude(mTencentMap.getMapCenter().getLongitude());
        position.setZoomLevel(mTencentMap.getZoomLevel());
        return position;
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroyView();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraChangedListener != null) {
            cameraChangedListener.onMapCameraChanging(getPosition(cameraPosition));
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (cameraChangedListener != null) {
            cameraChangedListener.mapCameraChanged(getPosition(cameraPosition));
        }
    }

    @Override
    public void setCameraPosition(CameraChangeObject cameraChangeObject) {
        com.tencent.mapcompare.mapfragment.CameraPosition cameraPosition = cameraChangeObject.cameraPosition;
        CameraPosition cp = new CameraPosition.Builder().
                target(new LatLng(cameraPosition.getLatitude(), cameraPosition.getLongitude())).
                zoom((float)(cameraPosition.getZoomLevel())).
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
        position.setLatitude(((CameraPosition)cameraPosition).getTarget().getLatitude());
        position.setLongitude(((CameraPosition)cameraPosition).getTarget().getLongitude());
        position.setZoomLevel(((CameraPosition)cameraPosition).getZoom());
        return position;
    }
}
