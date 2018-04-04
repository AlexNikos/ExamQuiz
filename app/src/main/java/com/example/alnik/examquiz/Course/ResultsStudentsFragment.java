package com.example.alnik.examquiz.Course;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Teacher.ResultsActivity;
import com.example.alnik.examquiz.models.Test;
import com.example.alnik.examquiz.models.Time;
import com.example.alnik.examquiz.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsStudentsFragment extends Fragment {

    private RecyclerView ResultsStudentsRecycleView;

    FirebaseUser mCurrentUser;
    DatabaseReference userParticipationRef;
    View mView;


    public ResultsStudentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_results_students, container, false);

        ResultsStudentsRecycleView =mView.findViewById(R.id.ResultsStudentsRecycleView);
        ResultsStudentsRecycleView.hasFixedSize();
        ResultsStudentsRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        userParticipationRef = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests").child(Global.test.getId());

        try{
            userParticipationRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }


        return  mView;
    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Time, ResultsStudentViewHolder> ResultsStudentsAdapter = new FirebaseRecyclerAdapter<Time, ResultsStudentViewHolder>(
                Time.class,
                R.layout.results_student_test,
                ResultsStudentViewHolder.class,
                userParticipationRef
        ) {

            @Override
            protected void populateViewHolder(final ResultsStudentViewHolder viewHolder, Time model, int position) {

                String studentID = getRef(position).getKey();
                FirebaseDatabase.getInstance().getReference("Users").child(studentID).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = (String)dataSnapshot.getValue();
                        viewHolder.setName(name);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                GenericTypeIndicator<ArrayList<Long>> s = new GenericTypeIndicator<ArrayList<Long>>() {};
                FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<Long> marks = dataSnapshot.getValue(s);

                        long mark = 0;
                        long maxMark = marks.get(marks.size()-1);
                        for(long q: marks){

                            mark = mark + q;
                        }

                        mark = mark - maxMark;

                        viewHolder.setGrade(mark +"/" +maxMark);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(model.getTime()));
               // viewHolder.setGrade();


//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String studentID = getRef(position).getKey();
                        FirebaseDatabase.getInstance().getReference("Users").child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Global.student = dataSnapshot.getValue(User.class);
                                Toast.makeText(getContext(),"Button pressed", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), CheckTestActivity.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });

            }
        };

        ResultsStudentsRecycleView.setAdapter(ResultsStudentsAdapter);

    }




    public static class ResultsStudentViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView name;
        TextView timeP;
        TextView grade;

        public ResultsStudentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = itemView.findViewById(R.id.name);
            timeP = itemView.findViewById(R.id.time);
            grade = itemView.findViewById(R.id.grade);


        }

        public void setName(String title) {

            name.setText(title);
        }

        public void setTime(String time){

            timeP.setText(time);
        }

        public void setGrade(String time){

            grade.setText(time);
        }


    }

}
