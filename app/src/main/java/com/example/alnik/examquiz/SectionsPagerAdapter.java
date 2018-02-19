package com.example.alnik.examquiz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by alnik on 14-Feb-18.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                TeacherCourseFragment requestsFragment = new TeacherCourseFragment();
                return requestsFragment;

            case 1:
                TeacherRoomFragment chatsFragment = new TeacherRoomFragment();
                return  chatsFragment;

            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "COURSES";

            case 1:
                return "ROOMS";

            default:
                return null;
        }

    }
}
