package com.example.alnik.examquiz.Anonymous;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionersFragment extends Fragment {

    private FloatingActionButton createNewTest;
    private RecyclerView testRecycleView;
    private DatabaseReference testRef;
    private FirebaseUser currentUser;
    private FirebaseRecyclerAdapter<Test, TestViewHolder> testRecyclerAdapter;
    private View mView;


    public QuestionersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

                    viewHolder.activeButton.setText("Active");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.GREEN);
                } else {

                    viewHolder.activeButton.setText("Inactive");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.RED);


                }
//-----------------------------------popup menu for 3 dots------------------------------------------------------------
//                viewHolder.options.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        //creating a popup menu
//                        PopupMenu popup = new PopupMenu(view.getContext(), view);
//                        popup.inflate(R.menu.popup_menu);
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.delete:
//
//                                        new android.support.v7.app.AlertDialog.Builder(getContext())
//                                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                                .setTitle("Are you sure?")
//                                                .setMessage("Do you want to delete this Announcement?")
//                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                notificationRef.child(key).removeValue();
//                                                            }
//                                                        }
//                                                )
//                                                .setNegativeButton("No", null)
//                                                .show();
//
//                                        break;
//                                    case R.id.edit:
//
//                                        LayoutInflater factory = LayoutInflater.from(getContext());
//                                        final View notification = factory.inflate(R.layout.notification_create, null);
//                                        final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
//                                        final EditText notificationBody = notification.findViewById(R.id.notificationBody);
//
//                                        notificationRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                final Test mTest = dataSnapshot.getValue(Test.class);
//
//                                                notificationTitle.setText(mTest.getTitle());
//                                                notificationBody.setText(mN.getBody());
//
//
//                                                new android.support.v7.app.AlertDialog.Builder(getContext())
//                                                        .setIcon(android.R.drawable.ic_input_add)
//                                                        .setTitle("Edit Announcement")
//                                                        .setView(notification)
//                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                        String title = notificationTitle.getText().toString();
//                                                                        String body = notificationBody.getText().toString();
//                                                                        mNotification.setTitle(title);
//                                                                        mNotification.setBody(body);
//
//                                                                        notificationRef.child(key).setValue(mNotification, new DatabaseReference.CompletionListener() {
//                                                                            @Override
//                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                                                                                if(databaseError == null){
//                                                                                    Toast.makeText(getContext(), "Announcement Edited", Toast.LENGTH_LONG).show();
//
//                                                                                } else {
//                                                                                    Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
//
//                                                                                }
//
//                                                                            }
//                                                                        });
//
//                                                                        ((ViewGroup) notification.getParent()).removeView(notification);
//                                                                    }
//                                                                }
//                                                        )
//                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                ((ViewGroup) notification.getParent()).removeView(notification);
//
//                                                            }
//                                                        })
//                                                        .show();
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                        break;
//                                }
//                                return false;
//                            }
//                        });
//                        popup.show();
//                    }
//                });
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
        View view;

        public TestViewHolder(View itemView) {
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
