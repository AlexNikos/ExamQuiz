package com.example.alnik.examquiz.Course;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by alnik on 23-Feb-18.
 */

public class CoursePagerAdapter extends FragmentPagerAdapter {

    public CoursePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                DatabaseCourseFragment DatabaseCourseFragment = new DatabaseCourseFragment();
                return DatabaseCourseFragment;

            case 1:
                TestsCourseFragment TestsCourseFragment = new TestsCourseFragment();
                return  TestsCourseFragment;

            case 2:
                AnnouncementsCourseFragment AnnouncementsCourseFragment = new AnnouncementsCourseFragment();
                return AnnouncementsCourseFragment;

            case 3:
                SubsCourseFragment SubsCourseFragment = new SubsCourseFragment();
                return SubsCourseFragment;
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "QUESTIONS";

            case 1:
                return "ASSIGNMENTS";

            case 2:
                return "ANNOUNCEMENTS";

            case 3:
                return "SUBSCRIBERS";


            default:
                return null;
        }

    }
}