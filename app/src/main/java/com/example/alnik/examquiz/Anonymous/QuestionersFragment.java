package com.example.alnik.examquiz.Anonymous;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.CustomDateTimePicker;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Test;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionersFragment extends Fragment {

    private FloatingActionButton createNewTest;
    private RecyclerView testRecycleView;
    private DatabaseReference testRef;
    private FirebaseUser currentUser;
    private CustomDateTimePicker dateTimePicker;
    private long startDate;
    private long endDate;
    private FirebaseRecyclerAdapter<Test, TestViewHolder> testRecyclerAdapter;
    private View mView;

    public QuestionersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_questioners, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        testRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questioners").child(Global.room.getId());

        try{
            testRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        createNewTest = mView.findViewById(R.id.create_new_questioner);
        createNewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewQuestionerActivity.class));

            }
        });

        testRecycleView = mView.findViewById(R.id.questionersRecycleView);
        testRecycleView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        testRecycleView.setLayoutManager(mLayoutManager);
        testRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    createNewTest.hide();
                } else {
                    createNewTest.show();
                }
            }
        });

        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();

        testRecyclerAdapter = new FirebaseRecyclerAdapter<Test, TestViewHolder>(
                Test.class,
                R.layout.single_test,
                TestViewHolder.class,
                testRef
        ) {

            @Override
            protected void populateViewHolder(final TestViewHolder viewHolder, Test model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setStartTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getStartDate()));
                viewHolder.setEndTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getEndDate()));


                String key= getRef(position).getKey();
//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getContext(),"Button pressed", Toast.LENGTH_LONG).show();
                        testRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Global.test = dataSnapshot.getValue(Test.class);
                                Log.d("test", "testID is  " +Global.test.getId());
                                startActivity(new Intent(getContext(), QuestionerResultsActivity.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                long currentDate = System.currentTimeMillis();
                if(model.getEndDate() > currentDate && model.getStartDate() <= currentDate){

                    //viewHolder.activeButton.setText("Active");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackground(getContext().getResources().getDrawable(R.drawable.active_background));
                } else {

                    //viewHolder.activeButton.setText("Inactive");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackground(getContext().getResources().getDrawable(R.drawable.inactive_background));


                }
//-----------------------------------popup menu for 3 dots------------------------------------------------------------
                viewHolder.optionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.inflate(R.menu.popup_menu_assisgment);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        new android.support.v7.app.AlertDialog.Builder(getContext())
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Are you sure?")
                                                .setMessage("Do you want to delete this Questioner?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                testRef.child(key).removeValue();
                                                            }
                                                        }
                                                )
                                                .setNegativeButton("No", null)
                                                .show();

                                        break;
                                    case R.id.editeStartTime:

                                        testRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Global.test = dataSnapshot.getValue(Test.class);
                                                Log.d("test", "testID is  " +Global.test.getId());

                                                dateTimePicker = new CustomDateTimePicker(getActivity(),
                                                        new CustomDateTimePicker.ICustomDateTimeListener() {

                                                            @Override
                                                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                                                              Date dateSelected, int year, String monthFullName,
                                                                              String monthShortName, int monthNumber, int date,
                                                                              String weekDayFullName, String weekDayShortName,
                                                                              int hour24, int hour12, int min, int sec,
                                                                              String AM_PM) {

                                                                startDate = dateSelected.getTime();
                                                                testRef.child(key).child("startDate").setValue(startDate);
                                                                Log.d("test", String.valueOf(startDate));

                                                            }

                                                            @Override
                                                            public void onCancel() {

                                                            }
                                                        });
                                                dateTimePicker.set24HourFormat(true);
                                                dateTimePicker.setDate(new Date(Global.test.getStartDate()));
                                                dateTimePicker.showDialog();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        break;
                                    case R.id.editEndTime:

                                        testRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Global.test = dataSnapshot.getValue(Test.class);
                                                Log.d("test", "testID is  " +Global.test.getId());

                                                dateTimePicker = new CustomDateTimePicker(getActivity(),
                                                        new CustomDateTimePicker.ICustomDateTimeListener() {

                                                            @Override
                                                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                                                              Date dateSelected, int year, String monthFullName,
                                                                              String monthShortName, int monthNumber, int date,
                                                                              String weekDayFullName, String weekDayShortName,
                                                                              int hour24, int hour12, int min, int sec,
                                                                              String AM_PM) {

                                                                endDate = dateSelected.getTime();
                                                                testRef.child(key).child("endDate").setValue(endDate);
                                                                Log.d("test", String.valueOf(endDate));

                                                            }

                                                            @Override
                                                            public void onCancel() {

                                                            }
                                                        });
                                                dateTimePicker.set24HourFormat(true);
                                                dateTimePicker.setDate(new Date(Global.test.getEndDate()));
                                                dateTimePicker.showDialog();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }
        };

        testRecycleView.setAdapter(testRecyclerAdapter);
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView testTitle;
        Button activeButton;
        TextView startTimeView;
        TextView endTimeView;
        CheckBox participation;
        ImageButton optionsButton;
        View view;

        public TestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            testTitle = itemView.findViewById(R.id.singleTestTitle);
            activeButton = itemView.findViewById(R.id.activeTestButton);
            startTimeView = itemView.findViewById(R.id.startTime);
            endTimeView = itemView.findViewById(R.id.endTime);
            participation = itemView.findViewById(R.id.participation);
            participation.setVisibility(View.GONE);
            optionsButton = itemView.findViewById(R.id.optionsButton);
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
