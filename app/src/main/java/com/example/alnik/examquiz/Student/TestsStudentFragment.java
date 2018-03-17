package com.example.alnik.examquiz.Student;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alnik.examquiz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestsStudentFragment extends Fragment {

    View mView;


    public TestsStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tests_student, container, false);




        return mView;
    }

}
