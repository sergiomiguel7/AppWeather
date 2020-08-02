package com.example.appweather.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.appweather.views.LocationFragment;

import java.util.List;

public class LocationFragmentController extends FragmentStatePagerAdapter {

    private List<LocationFragment> fragments;


    public LocationFragmentController(@NonNull FragmentManager fm, List<LocationFragment> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    public void addFragment(LocationFragment pf) {
        this.fragments.add(pf);
    }

    public void removeFragment(int index){
        fragments.remove(index);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

}
