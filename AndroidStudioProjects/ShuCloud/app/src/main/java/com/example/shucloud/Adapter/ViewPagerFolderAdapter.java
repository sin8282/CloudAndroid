package com.example.shucloud.Adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.shucloud.FragmentImageFolder;
import com.example.shucloud.FragmentVideoFolder;

public class ViewPagerFolderAdapter extends FragmentStateAdapter {
    final private View folderViewPager;

    public ViewPagerFolderAdapter(View folderViewPager, @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.folderViewPager = folderViewPager;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return FragmentVideoFolder.newInstance();
            default:
                return FragmentImageFolder.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
