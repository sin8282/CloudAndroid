package com.example.shucloud.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shucloud.ManagingView;
import com.example.shucloud.PhoneStorageView;
import com.example.shucloud.DriveStorageView;
import com.example.shucloud.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public List<Fragment> fragments;
    private FragmentManager fragmentManager;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragments= new ArrayList<>();
        fragments.add(PhoneStorageView.newInstance());
        fragments.add(DriveStorageView.newInstance());
        fragments.add(ManagingView.newInstance());
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }


    @Override
    public int getItemCount() {
        return fragments.size();
    }

}


/*
    @Override
    public long getItemId(int position) {
        Fragment fragment = fragments.get(position);
        long id = getIDForFragment(fragment);
        return id;
    }

    @Override
    public boolean containsItem(long itemId) {
        for (Fragment fragment : fragments) {
            if (getIDForFragment(fragment) == itemId)
                return true;
        }
        return false;
    }

    private long getIDForFragment(Fragment fragment)
    {
        if (fragment.toString() == fragments.get(1).toString())
            return 200;
        if (fragment.toString() == fragments.get(2).toString())
            return 300;

        if (fragment.toString() == fragments.get(0).toString())
           return 100;
        else
           return 101;

    }

    public void replaceFragment(int index, Fragment fragment) {
        fragments.set(index, fragment);
        notifyDataSetChanged();
    }*/

