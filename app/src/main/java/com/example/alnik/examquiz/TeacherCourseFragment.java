package com.example.alnik.examquiz;


import android.content.DialogInterface;
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

import com.example.alnik.examquiz.models.course;
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


    //private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    //private FirebaseDatabase database;
    private DatabaseReference myRefUser;
    private DatabaseReference mCourses;

    private TextView nameView;
    private TextView emailView;
    private String fullName;

    private String courseName;
    private String courseInfo;
    private String courseSite;


    private RecyclerView coursesList;

    private View mMainView;


    public TeacherCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_teacher_lessons, container, false);

        coursesList = mMainView.findViewById(R.id.teacher_lesson_recycleView);
        coursesList.setHasFixedSize(true);
        coursesList.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mCourses = FirebaseDatabase.getInstance().getReference("Courses").child(currentUser.getUid());

        FloatingActionButton CreateNewLesson = (FloatingActionButton) mMainView.findViewById(R.id.create_new_lesson);

//-----------------------------This is for multiLine editText on AlertDialBox--------------------------------------------------
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View create = factory.inflate(R.layout.create_course, null);
        final EditText courseName = create.findViewById(R.id.courseName);
        final EditText courseInfo = create.findViewById(R.id.courseInfo);
        final EditText courseSite = create.findViewById(R.id.courseSite);

//------------------------ New course cretion fab on click----------------------------------------------------------------------------------
        CreateNewLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Course")
                        .setMessage("Enter Course details:")
                        .setView(create)
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
                                                                course newCourse = new course(TeacherCourseFragment.this.courseName, currentUser.getUid().toString(), fullname, courseId, TeacherCourseFragment.this.courseInfo, TeacherCourseFragment.this.courseSite);
                                                                mCourses.child(TeacherCourseFragment.this.courseName/*courseId*/).setValue(newCourse, new DatabaseReference.CompletionListener() {
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
                                        ((ViewGroup) create.getParent()).removeView(create);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                courseName.setText("");
                                courseInfo.setText("");
                                courseSite.setText("");
                                ((ViewGroup) create.getParent()).removeView(create);

                            }
                        })
                        .show();
            }
        });

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

//-------------------------------Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<course, lessonViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<course, lessonViewHolder>(
                course.class,
                R.layout.teacher_single_lesson,
                lessonViewHolder.class,
                mCourses
        ) {

            @Override
            protected void populateViewHolder(final lessonViewHolder viewHolder, course model, int position) {
                viewHolder.setName(model.getName());

                final DatabaseReference gameRef= getRef(position);
                final String postKey = gameRef.getKey();

//---------------------------------action on click a course----------------------------------------------------------
                viewHolder.teacherLessonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();

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
                                        .setMessage("Do you want to delete this course?")
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

        View view;

        public lessonViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            teacherlessonOptionsButton = itemView.findViewById(R.id.teacherLessonOptionsButton);
            teacherLessonButton = itemView.findViewById(R.id.teacherLessonView);
            view = itemView.findViewById(R.id.singleRowLesson);

        }

        public void setName(String name) {

            teacherLessonButton.setText(name);

        }


    }
}

