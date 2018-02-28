package com.example.alnik.examquiz.Course;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alnik.examquiz.NewTestActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.StartActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestsCourseFragment extends Fragment {

    private Button button;

    private View mView;


    public TestsCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tests_course, container, false);


        button = mView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), NewTestActivity.class));
            }
        });

        return mView;
    }

}
