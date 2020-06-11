package org.insbaixcamp.projectem13.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.insbaixcamp.projectem13.Fragments.CalendarFragment;
import org.insbaixcamp.projectem13.Fragments.ConfirmFragment;

public class ViewPageAdapter extends FragmentPagerAdapter {

    public ViewPageAdapter(FragmentManager fm){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CalendarFragment.getInstance();
            case 1:
                return ConfirmFragment.getInstance();
        }
        return null;

    }


    @Override
    public int getCount() {
        return 2;
    }
}
