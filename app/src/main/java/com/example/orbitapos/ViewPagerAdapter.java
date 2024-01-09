package com.example.orbitapos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Log.d("ViewPagerAdapter", "Creating Catalog fragment");
                return new Catalog();
            case 1:
                Log.d("ViewPagerAdapter", "Creating Calculator fragment");
                return new Calculator();
            default:
                Log.e("ViewPagerAdapter", "Invalid position");
                return new Calculator();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
