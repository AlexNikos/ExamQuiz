package com.example.alnik.examquiz.Teacher;


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

import com.example.alnik.examquiz.Course.CourseActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherCourseFragment extends Fragment {


    private FirebaseUser currentUser;
    private DatabaseReference myRefUser;
    private DatabaseReference mCourses;


    private TextView nameView;
    private TextView emailView;
    private String fullName;

    private String courseName;
    private String courseInfo;
    private String courseSite;

    private FloatingActionButton CreateNewLesson;


    private RecyclerView coursesList;

    private View mMainView;


    public TeacherCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_teacher_courses, container, false);

        coursesList = mMainView.findViewById(R.id.teacher_lesson_recycleView);
        coursesList.setHasFixedSize(true);
        coursesList.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mCourses = FirebaseDatabase.getInstance().getReference("Courses").child(currentUser.getUid());


        CreateNewLesson = (FloatingActionButton) mMainView.findViewById(R.id.create_new_lesson);

//-----------------------------This is for multiLine editText on AlertDialBox--------------------------------------------------
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View courseAlertBox = factory.inflate(R.layout.create_course, null);
        final EditText courseName = courseAlertBox.findViewById(R.id.courseName);
        final EditText courseInfo = courseAlertBox.findViewById(R.id.courseInfo);
        final EditText courseSite = courseAlertBox.findViewById(R.id.courseSite);

//------------------------ New Course creation fab on click----------------------------------------------------------------------------------
        CreateNewLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Course")
                        .setMessage("Enter Course details:")
                        .setView(courseAlertBox)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        TeacherCourseFragment.this.courseName = courseName.getText().toString();
                                        TeacherCourseFragment.this.courseInfo = courseInfo.getText().toString();
                                        TeacherCourseFragment.this.courseSite = courseSite.getText().toString();

                                        if(TeacherCourseFragment.this.courseName.isEmpty()){

                                            Toast.makeText(getContext(),"Please enter a name", Toast.LENGTH_LONG).show();

                                        } else{

                                            mCourses.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(TeacherCourseFragment.this.courseName)) {
                                                        Toast.makeText(getContext(), "Course already exist!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        myRefUser.child("fullname").addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                String fullname = dataSnapshot.getValue().toString();
                                                                String courseId = mCourses.push().getKey();
                                                                Course newCourse = new Course(TeacherCourseFragment.this.courseName, currentUser.getUid().toString(), fullname, courseId, TeacherCourseFragment.this.courseInfo, TeacherCourseFragment.this.courseSite);
                                                                mCourses.child(TeacherCourseFragment.this.courseName).setValue(newCourse, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        Toast.makeText(getContext(), "Course Created", Toast.LENGTH_LONG).show();

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

                                        courseName.setText("");
                                        courseInfo.setText("");
                                        courseSite.setText("");
                                        ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                courseName.setText("");
                                courseInfo.setText("");
                                courseSite.setText("");
                                ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);


                            }
                        })
                        .show();
               alert.setCanceledOnTouchOutside(false);
            }
        });

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

//-------------------------------Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<Course, lessonViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, lessonViewHolder>(
                Course.class,
                R.layout.teacher_single_lesson,
                lessonViewHolder.class,
                mCourses
        ) {

            @Override
            protected void populateViewHolder(final lessonViewHolder viewHolder, Course model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setSubs(model.getSubscribers());

                final DatabaseReference courseRef= getRef(position);
                final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.teacherLessonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       //Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();
                        Intent startCourseActivity = new Intent(getActivity(), CourseActivity.class);
                        startCourseActivity.putExtra("courseName", postKey);
                        startActivity(startCourseActivity);


                    }
                });
//-----------------------------------popup menu for 3 dots------------------------------------------------------------
                viewHolder.teacherlessonOptionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.popup_menu);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        new AlertDialog.Builder(getContext())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Are you sure?")
                                        .setMessage("Do you want to delete this Course?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        Log.d("test", "populateViewHolder: " +postKey);

                                                        mCourses.child(postKey).removeValue();
                                                    }
                                                }
                                        )
                                        .setNegativeButton("No", null)
                                        .show();

                                        break;
                                    case R.id.edit:

                                        LayoutInflater factory = LayoutInflater.from(getContext());
                                        final View courseAlertBox = factory.inflate(R.layout.create_course, null);
                                        final EditText courseName = courseAlertBox.findViewById(R.id.courseName);
                                        final EditText courseInfo = courseAlertBox.findViewById(R.id.courseInfo);
                                        final EditText courseSite = courseAlertBox.findViewById(R.id.courseSite);

                                        mCourses.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Log.d("testcourse", dataSnapshot.toString());
                                                    final Course course = dataSnapshot.getValue(Course.class);
                                                    Log.d("testcourse", course.getName() +" " +course.getSite() +" " +course.getInfo());



                                                courseName.setText(course.getName());
                                                courseName.setEnabled(false);
                                                courseInfo.setText(course.getInfo());
                                                courseSite.setText(course.getSite());

                                                new AlertDialog.Builder(getContext())
                                                        .setIcon(android.R.drawable.ic_input_add)
                                                        .setTitle("Edit Course")
                                                        .setMessage("Enter Course details:")
                                                        .setView(courseAlertBox)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        String info = courseInfo.getText().toString();
                                                                        final String site = courseSite.getText().toString();

                                                                        mCourses.child(postKey).child("info").setValue(info, new DatabaseReference.CompletionListener() {
                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                        mCourses.child(postKey).child("site").setValue(site, new DatabaseReference.CompletionListener() {
                                                                                            @Override
                                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                                Toast.makeText(getContext(), "Course Updated", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });

                                                                        ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);
                                                                    }
                                                                }
                                                        )
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                ((ViewGroup) courseAlertBox.getParent()).removeView(courseAlertBox);

                                                            }
                                                        })
                                                        .show();

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
                        //displaying the popup
                        popup.show();

                    }
                });

            }


        };

        coursesList.setAdapter(firebaseRecyclerAdapter);

    }

//------------------------------------ViewHolder------------------------------------------------------
    public static class lessonViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView teacherLessonButton;
        ImageButton teacherlessonOptionsButton;
        TextView subs;

        View view;

        public lessonViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            teacherlessonOptionsButton = itemView.findViewById(R.id.teacherLessonOptionsButton);
            teacherLessonButton = itemView.findViewById(R.id.teacherLessonView);
            subs = itemView.findViewById(R.id.subs);
            view = itemView.findViewById(R.id.singleRowLesson);

        }

        public void setName(String name) {

            teacherLessonButton.setText(name);
        }

        public void setSubs(long subscribers){

            subs.setText(String.valueOf(subscribers));
        }


    }
}

