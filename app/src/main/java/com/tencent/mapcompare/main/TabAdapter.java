package com.tencent.mapcompare.main;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.tencent.mapcompare.mapfragment.MapFragment;

import java.util.List;

/**
 * Created by wangxiaokun on 16/5/4.
 */
public class TabAdapter extends FragmentStatePagerAdapter {

    private List<MapFragment> mapFragments;

    public TabAdapter(FragmentManager fm, List<MapFragment> fragments) {
        super(fm);

        mapFragments = fragments;
    }

    @Override
    public MapFragment getItem(int position) {
        return mapFragments.get(position);
    }

    @Override
    public int getCount() {
        return mapFragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mapFragments.get(position).onDestroy();
    }
}
