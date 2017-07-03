package com.gensee.player.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    private ArrayList<String> dataList = new ArrayList<String>();

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int arg0) {
        return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < dataList.size() && dataList.get(position) != null) {
            return dataList.get(position);
        } else {
            return "";
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position,
                               Object object) {
        super.setPrimaryItem(container, position, object);
    }

    public void setList(ArrayList<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }
}
