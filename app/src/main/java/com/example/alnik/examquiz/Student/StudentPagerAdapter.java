package com.example.alnik.examquiz.Student;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.alnik.examquiz.Teacher.TeacherCourseFragment;
import com.example.alnik.examquiz.Teacher.TeacherRoomFragment;

/**
 * Created by alnik on 15-Mar-18.
 */


public class StudentPagerAdapter extends FragmentPagerAdapter {

    public StudentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                AnnouncementsStudentFragment AnnouncementsStudentFragment = new AnnouncementsStudentFragment();
                return AnnouncementsStudentFragment;

            case 1:
                TestsStudentFragment TestsStudentFragment = new TestsStudentFragment();
                return  TestsStudentFragment;

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
                return "ANNOUNCEMENTS";

            case 1:
                return "TESTS";

            default:
                return null;
        }

    }
}

