package com.example.alnik.examquiz.Anonymous;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.alnik.examquiz.Anonymous.QuestionersFragment;
import com.example.alnik.examquiz.Anonymous.QuestionsFragment;


public class RoomPagerAdapter extends FragmentPagerAdapter {

    public RoomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                QuestionsFragment mQuestionsFragment = new QuestionsFragment();
                return mQuestionsFragment;

            case 1:
                QuestionersFragment mQuestionersFragment = new QuestionersFragment();
                return  mQuestionersFragment;

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
                return "QUESTIONS";

            case 1:
                return "QUESTIONERS";

            default:
                return null;
        }

    }
}
