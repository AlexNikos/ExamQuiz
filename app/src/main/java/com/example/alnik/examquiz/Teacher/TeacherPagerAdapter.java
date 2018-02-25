package com.example.alnik.examquiz.Teacher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by alnik on 14-Feb-18.
 */

public class TeacherPagerAdapter extends FragmentPagerAdapter {

    public TeacherPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                TeacherCourseFragment CourseFragment = new TeacherCourseFragment();
                return CourseFragment;

            case 1:
                TeacherRoomFragment RoomsFragment = new TeacherRoomFragment();
                return  RoomsFragment;

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
