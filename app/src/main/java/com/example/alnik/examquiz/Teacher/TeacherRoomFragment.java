package com.example.alnik.examquiz.Teacher;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alnik.examquiz.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherRoomFragment extends Fragment {


    public TeacherRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_room, container, false);
    }

}
