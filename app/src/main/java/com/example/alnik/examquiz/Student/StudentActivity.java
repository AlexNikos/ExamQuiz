package com.example.alnik.examquiz.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Course;
import com.example.alnik.examquiz.models.Time;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersRef;
    private DatabaseReference mUsersCoursesRef;
    private DatabaseReference mCoursesRef;


    EditText lessonNameInput;
    TextView nameView;
    TextView emailView;

    String lessonName;
    String fullName;

    RecyclerView mCoursesRecycleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Courses");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mUsersCoursesRef = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Users").child(currentUser.getUid());
        mCoursesRef = FirebaseDatabase.getInstance().getReference("Courses");

        mCoursesRecycleView = findViewById(R.id.mCoursesRecycleView);
        mCoursesRecycleView.hasFixedSize();
        mCoursesRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        FloatingActionButton addNewLesson = (FloatingActionButton) findViewById(R.id.add_new_lesson);

        lessonNameInput = new EditText(StudentActivity.this);
        lessonNameInput.setSingleLine(true);

        addNewLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(StudentActivity.this)
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Add New Lesson")
                        .setMessage("Enter Lesson name:")
                        .setView(lessonNameInput)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        lessonName = lessonNameInput.getText().toString();
                                        lessonNameInput.setText("");
                                        Log.d("test", lessonName);
                                        ((ViewGroup) lessonNameInput.getParent()).removeView(lessonNameInput);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nameView);
        emailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emailView);


        mUsersRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = dataSnapshot.getValue().toString();

                mUsersRef.child("surname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        fullName = fullName +" " +dataSnapshot.getValue().toString();
                        nameView.setText(fullName);
                        emailView.setText(currentUser.getEmail().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.sign_out) {
//            FirebaseAuth.getInstance().signOut();
//            if (mAuth == null){
//                startActivity(new Intent(StudentActivity.this, LoginActivity.class));
//                finish();
//            }
//            return true;
//        }

        if(id == R.id.searchCourse){

            Intent i = new Intent(getApplicationContext(), SearchCourseActivity.class);
            startActivity(i);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                startActivity(new Intent(StudentActivity.this, LoginActivity.class));
                finish();
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Time, StudentCoursesViewHolder> mCoursesRecycleViewAdapter = new FirebaseRecyclerAdapter<Time, StudentCoursesViewHolder>(

                Time.class,
                R.layout.single_student_course,
                StudentCoursesViewHolder.class,
                mUsersCoursesRef


        ) {
            @Override
            protected void populateViewHolder(final StudentCoursesViewHolder courseViewHolder, Time subs, int i) {

                courseViewHolder.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(subs.getTime()));

                final String course_id = getRef(i).getKey();
                Log.d("test", course_id);
                //final String[] courseName = new String[1];

                mCoursesRef.child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("test", dataSnapshot.toString());
                        //courseName[0] = dataSnapshot.child("name").getValue().toString();
                        Course course = dataSnapshot.getValue(Course.class);
                        Global.course = course;

                        courseViewHolder.setName(course.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                courseViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent toCourse = new Intent();
                        //toCourse.putExtra("course_id", course_id);
                        //toCourse.putExtra("courseName", courseName[0]);
                        startActivity(new Intent(StudentActivity.this, CourseStudentActivity.class));
                    }
                });

                courseViewHolder.singleStudentCourseOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {



                            }
                        });
            }
        };

        mCoursesRecycleView.setAdapter(mCoursesRecycleViewAdapter);


    }


    public static class StudentCoursesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView singleSudentSubscriptionTime;
        TextView singleSudentCourseName;
        ImageButton singleStudentCourseOptions;

        public StudentCoursesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            singleSudentSubscriptionTime = mView.findViewById(R.id.singleSudentSubscriptionTime);
            singleSudentCourseName = mView.findViewById(R.id.singleSudentCourseName);
            singleStudentCourseOptions = mView.findViewById(R.id.singleStudentCourseOptions);

        }

        public void setDate(String time){

            singleSudentSubscriptionTime.setText(time);

        }

        public void setName(String name){

            singleSudentCourseName.setText(name);

        }

    }
}
