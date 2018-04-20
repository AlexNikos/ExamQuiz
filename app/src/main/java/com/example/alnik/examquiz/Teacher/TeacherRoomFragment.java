package com.example.alnik.examquiz.Teacher;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Anonymous.RoomActivity;
import com.example.alnik.examquiz.Course.CourseActivity;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Course;
import com.example.alnik.examquiz.models.Room;
import com.example.alnik.examquiz.models.Time;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherRoomFragment extends Fragment {

    private FirebaseUser currentUser;
    private DatabaseReference myRefUser;
    private DatabaseReference mRooms;
    private DatabaseReference userRoomOwnership;
    private DatabaseReference roomRoomOwnership;

    private DatabaseReference roomQuestionmark;
    private DatabaseReference roomQuestions;

    private String roomName;


    private FloatingActionButton CreateNewRoom;
    private RecyclerView roomsList;
    private View mMainView;

    public TeacherRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_teacher_room, container, false);
        roomsList = mMainView.findViewById(R.id.roomRecycleView);
        roomsList.setHasFixedSize(true);
        roomsList.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mRooms = FirebaseDatabase.getInstance().getReference("Anonymous").child("Rooms");
        userRoomOwnership = FirebaseDatabase.getInstance().getReference("Anonymous").child("RoomOwnership").child("User");
        roomRoomOwnership  = FirebaseDatabase.getInstance().getReference("Anonymous").child("RoomOwnership").child("Room");

        CreateNewRoom = (FloatingActionButton) mMainView.findViewById(R.id.create_new_room);

//-----------------------------This is for multiLine editText on AlertDialBox--------------------------------------------------
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View courseAlertBox = factory.inflate(R.layout.create_room, null);
        final EditText roomNameView = courseAlertBox.findViewById(R.id.roomName);

//------------------------ New Course creation fab on click----------------------------------------------------------------------------------
        CreateNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Room")
                        .setMessage("Enter Room name:")
                        .setView(courseAlertBox)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        roomName = roomNameView.getText().toString();

                                        if(roomName.isEmpty()){

                                            Toast.makeText(getContext(),"Please enter a name", Toast.LENGTH_LONG).show();

                                        } else{

                                            roomRoomOwnership.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Log.d("test", "datasnapshot in order: " +dataSnapshot.toString());
                                                    if(dataSnapshot.hasChild(roomName)){
                                                        Toast.makeText(getContext(), "Room already exist!", Toast.LENGTH_LONG).show();

                                                    } else{
                                                        Toast.makeText(getContext(), "Room not exist!", Toast.LENGTH_LONG).show();
                                                        myRefUser.child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                String fullname = dataSnapshot.getValue().toString();
                                                                String roomId = mRooms.push().getKey();
                                                                Room newRoom = new Room(roomName, currentUser.getUid().toString(), fullname, roomId);
                                                                mRooms.child(roomId).setValue(newRoom, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                                        if(databaseError == null) {
                                                                            long time = System.currentTimeMillis();
                                                                            userRoomOwnership.child(currentUser.getUid()).child(newRoom.getId()).setValue(new Time(time));
                                                                            roomRoomOwnership.child(newRoom.getName()).child(currentUser.getUid()).setValue(new Time(time));
                                                                            Toast.makeText(getContext(), "Room Created", Toast.LENGTH_LONG).show();

                                                                        }
                                                                    }
                                                                });
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

                                        roomNameView.setText("");
                                        ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                roomNameView.setText("");
                                ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
                            }
                        })
                        .show();
                alert.setCanceledOnTouchOutside(false);
            }
        });

        return mMainView;
    }

    private void roomNameExistes() {


    }

    @Override
    public void onStart() {
        super.onStart();

//-------------------------------Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<Time, roomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Time, roomViewHolder>(
                Time.class,
                R.layout.single_teacher_lesson,
                roomViewHolder.class,
                userRoomOwnership.child(currentUser.getUid())
        ) {

            @Override
            protected void populateViewHolder(final roomViewHolder viewHolder, Time model, int position) {


                final DatabaseReference roomRef= getRef(position);
                final String postKey = roomRef.getKey();

                mRooms.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Room mRoom = dataSnapshot.getValue(Room.class);
                        Log.d("test", "room: " +dataSnapshot.toString());
                        viewHolder.setName(mRoom.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.subs.setVisibility(View.GONE);
                viewHolder.subsText.setVisibility(View.GONE);


//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.teacherLessonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();
                        mRooms.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("test", dataSnapshot.toString());
                                Global.room = dataSnapshot.getValue(Room.class);

                                Toast.makeText(getContext(), "Room to enter", Toast.LENGTH_LONG).show();
                                Intent startCourseActivity = new Intent(getActivity(), RoomActivity.class);
                                startActivity(startCourseActivity);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
//-----------------------------------popup menu for 3 dots------------------------------------------------------------
                viewHolder.teacherlessonOptionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRooms.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("test", dataSnapshot.toString());
                                Global.room = dataSnapshot.getValue(Room.class);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.inflate(R.menu.popup_menu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        new AlertDialog.Builder(getContext())
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Are you sure?")
                                                .setMessage("Do you want to delete this Room?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                Log.d("test", "populateViewHolder: " +postKey);
                                                                mRooms.child(Global.room.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        if(databaseError == null){

                                                                            //roomQuestionmark = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questionmarks").child(Global.room.getId());
                                                                            //roomQuestions = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId());

                                                                            userRoomOwnership.child(currentUser.getUid()).child(Global.room.getId()).removeValue();
                                                                            roomRoomOwnership.child(Global.room.getName()).child(Global.room.getOwnersID()).removeValue();
                                                                            //roomQuestions.removeValue();
                                                                            //roomQuestionmark.removeValue();
                                                                        }
                                                                    }
                                                                });

                                                                Toast.makeText(getContext(), "Room deleted.", Toast.LENGTH_LONG).show();

                                                            }
                                                        }
                                                )
                                                .setNegativeButton("No", null)
                                                .show();

                                        break;
//                                    case R.id.edit:
//
//                                        LayoutInflater factory = LayoutInflater.from(getContext());
//                                        final View courseAlertBox = factory.inflate(R.layout.create_course, null);
//                                        final EditText courseName = courseAlertBox.findViewById(R.id.courseName);
//                                        final EditText courseInfo = courseAlertBox.findViewById(R.id.courseInfo);
//                                        final EditText courseSite = courseAlertBox.findViewById(R.id.courseSite);
//
//                                        mCourses.child(Global.course.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                Log.d("testcourse", dataSnapshot.toString());
//                                                final Course course = dataSnapshot.getValue(Course.class);
//                                                Log.d("testcourse", course.getName() +" " +course.getSite() +" " +course.getInfo());
//
//
//
//                                                courseName.setText(course.getName());
//                                                courseName.setEnabled(false);
//                                                courseInfo.setText(course.getInfo());
//                                                courseSite.setText(course.getSite());
//
//                                                new AlertDialog.Builder(getContext())
//                                                        .setIcon(android.R.drawable.ic_input_add)
//                                                        .setTitle("Edit Course")
//                                                        .setMessage("Enter Course details:")
//                                                        .setView(courseAlertBox)
//                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                        String info = courseInfo.getText().toString();
//                                                                        String site = courseSite.getText().toString();
//                                                                        Global.course.setInfo(info);
//                                                                        Global.course.setSite(site);
//
//                                                                        mCourses.child(Global.course.getId()).setValue(Global.course, new DatabaseReference.CompletionListener() {
//                                                                            @Override
//                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                                                if(databaseError == null){
//                                                                                    //userCourseOwnership.child(currentUser.getUid()).child(Global.course.getId()).setValue(Global.course);
//                                                                                    //courseCourseOwnership.child(Global.course.getName()).child(Global.course.getOwnersID()).setValue(Global.course);
//                                                                                    Toast.makeText(getContext(), "Course Updated", Toast.LENGTH_LONG).show();
//
//                                                                                }
//                                                                            }
//                                                                        });
//
////                                                                        mCourses.child(model.getId()).child("info").setValue(info, new DatabaseReference.CompletionListener() {
////                                                                                    @Override
////                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                                                                                        mCourses.child(model.getId()).child("site").setValue(site, new DatabaseReference.CompletionListener() {
////                                                                                            @Override
////                                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                                                                                                Toast.makeText(getContext(), "Course Updated", Toast.LENGTH_LONG).show();
////
////                                                                                            }
////                                                                                        });
////                                                                                    }
////                                                                                });
//
//                                                                        ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
//                                                                    }
//                                                                }
//                                                        )
//                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
//
//                                                            }
//                                                        })
//                                                        .show();
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//
//                                        });
//
//
//                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();

                    }
                });

            }
        };

        roomsList.setAdapter(firebaseRecyclerAdapter);
    }

    //------------------------------------ViewHolder------------------------------------------------------
    public static class roomViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView teacherLessonButton;
        ImageButton teacherlessonOptionsButton;
        TextView subs;
        TextView subsText;

        View view;

        public roomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            teacherlessonOptionsButton = itemView.findViewById(R.id.teacherLessonOptionsButton);
            teacherLessonButton = itemView.findViewById(R.id.teacherLessonView);
            subs = itemView.findViewById(R.id.subs);
            subsText = itemView.findViewById(R.id.subsText);
            view = itemView.findViewById(R.id.singleRowLesson);

        }

        public void setName(String name) {

            teacherLessonButton.setText(name);
        }

    }
}
