package com.suzhou.concept.lil.ui.my.rank;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class RankNewAdapter extends FragmentStateAdapter {

    private List<Fragment> list;

    public RankNewAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> list) {
        super(fragmentActivity);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return list.get(i);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
