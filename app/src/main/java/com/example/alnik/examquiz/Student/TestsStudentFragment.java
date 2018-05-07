package com.example.alnik.examquiz.Student;


import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Course.CheckTestActivity;
import com.example.alnik.examquiz.Course.TestsCourseFragment;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Test;
import com.example.alnik.examquiz.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestsStudentFragment extends Fragment {

    View mView;

    private DatabaseReference testRef;
    private FirebaseUser currentUser;
    private DatabaseReference testTestsParticipation;
    private DatabaseReference testUsersParticipation;

    private RecyclerView testsStudentRecucleView;

    public TestsStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tests_student, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        testRef = FirebaseDatabase.getInstance().getReference("Test").child(Global.course.getId());
        testUsersParticipation = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Users").child(currentUser.getUid());
        testTestsParticipation = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests");

        try{
            testRef.keepSynced(true);
            testUsersParticipation.keepSynced(true);
            testTestsParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        testsStudentRecucleView = mView.findViewById(R.id.testsStudentRecucleView);
        testsStudentRecucleView.hasFixedSize();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        testsStudentRecucleView.setLayoutManager(mLayoutManager);


        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Test, testStudentViewHolder> testRecyclerAdapter = new FirebaseRecyclerAdapter<Test, testStudentViewHolder>(
                Test.class,
                R.layout.single_test,
                testStudentViewHolder.class,
                testRef.orderByChild("endDate").startAt(Global.timeSubscripted)
        ) {

            @Override
            protected void populateViewHolder(final testStudentViewHolder viewHolder, Test model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setStartTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getStartDate()));
                viewHolder.setEndTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getEndDate()));


                String key= getRef(position).getKey();
//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        testUsersParticipation.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    testRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Global.test = dataSnapshot.getValue(Test.class);
                                            Log.d("test", "testID is  " +Global.test.getId());
                                            Toast.makeText(getContext(), "Your results for " +Global.test.getTitle() , Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getContext(), StudentScoreActivity.class));

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {

                                    testRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Global.test = dataSnapshot.getValue(Test.class);
                                            Log.d("test", "testID is  " +Global.test.getId());

                                            long currentTime = System.currentTimeMillis();
                                            if(Global.test.getStartDate() > currentTime){

                                                Toast.makeText(getContext(), "Not Available yet!", Toast.LENGTH_LONG).show();

                                            } else if(Global.test.getEndDate() < currentTime){

                                                Toast.makeText(getContext(), "Assignement has ended!", Toast.LENGTH_LONG).show();


                                            } else{

                                                startActivity(new Intent(getContext(), RunningTestActivity.class));

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                long currentDate = System.currentTimeMillis();
                if(model.getEndDate() > currentDate && model.getStartDate() <= currentDate){

                    viewHolder.activeButton.setText("Active");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.GREEN);
                } else {

                    viewHolder.activeButton.setText("Inactive");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.RED);

                }

            }
        };

        testsStudentRecucleView.setAdapter(testRecyclerAdapter);

    }




    public static class testStudentViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView testTitle;
        Button activeButton;
        TextView startTimeView;
        TextView endTimeView;
        View view;

        public testStudentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            testTitle = itemView.findViewById(R.id.singleTestTitle);
            activeButton = itemView.findViewById(R.id.activeTestButton);
            startTimeView = itemView.findViewById(R.id.startTime);
            endTimeView = itemView.findViewById(R.id.endTime);
            view = itemView.findViewById(R.id.singleTestView);


        }

        public void setTitle(String title) {

            testTitle.setText(title);
        }

        public void setStartTime(String time){

            startTimeView.setText(time);
        }

        public void setEndTime(String time){

            endTimeView.setText(time);
        }


    }

}
