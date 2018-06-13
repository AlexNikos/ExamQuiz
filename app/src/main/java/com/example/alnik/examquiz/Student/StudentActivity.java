package com.example.alnik.examquiz.Student;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Teacher.TeacherActivity;
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
    private DatabaseReference mCoursesCourseRef;
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
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Courses");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mCoursesCourseRef = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses");
        mCoursesRef = FirebaseDatabase.getInstance().getReference("Courses");
        mUsersCoursesRef = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Users").child(currentUser.getUid());

        try{
            mUsersRef.keepSynced(true);
            mUsersCoursesRef.keepSynced(true);
            mCoursesRef.keepSynced(true);
            mCoursesCourseRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        mCoursesRecycleView = findViewById(R.id.mCoursesRecycleView);
        mCoursesRecycleView.hasFixedSize();
        mCoursesRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_fullname);
        emailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);


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

        int id = item.getItemId();
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

        if (id == R.id.edit_profile) {
            // Handle the camera action
        } else if (id == R.id.delete_profile) {

        } else if (id == R.id.my_marks) {

        }  else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                Global.currentUser = null;
                Global.test = null;
                Global.student = null;
                Global.course = null;
                Global.timeSubscripted = 0;
                Intent accountIntent = new Intent(StudentActivity.this, LoginActivity.class );
                accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(accountIntent);
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
                String course_id = getRef(i).getKey();
                Log.d("test", course_id);

                mCoursesRef.child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("test", dataSnapshot.toString());
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
                        String course_id = getRef(i).getKey();

                        mCoursesRef.child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Course course = dataSnapshot.getValue(Course.class);
                                Global.course = course;
                                Log.d("test", "on click course name " +Global.course.getName());

                                FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses").child(Global.course.getId()).child(currentUser.getUid())
                                        .child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Global.timeSubscripted = (long)dataSnapshot.getValue();
                                        Log.d("test", "onDataChange: " +Global.timeSubscripted);
                                        startActivity(new Intent(StudentActivity.this, CourseStudentActivity.class));

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
                });

                courseViewHolder.singleStudentCourseOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String course_id = getRef(i).getKey();
                                PopupMenu popup = new PopupMenu(view.getContext(), view);
                                popup.inflate(R.menu.popup_student_course);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.unsubscribe:

                                                new AlertDialog.Builder(StudentActivity.this)
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .setTitle("Are you sure?")
                                                        .setMessage("Do you want to unsubscribe from this Course?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        mUsersCoursesRef.child(course_id).removeValue();
                                                                        mCoursesCourseRef.child(course_id).child(currentUser.getUid()).removeValue();
                                                                        Toast.makeText(getApplicationContext(), "You have been unsubscribed from this course.", Toast.LENGTH_LONG).show();


                                                                    }
                                                                }
                                                        )
                                                        .setNegativeButton("No", null)
                                                        .show();

                                                break;
                                            case R.id.info:

                                                mCoursesRef.child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        Log.d("testcourse", dataSnapshot.toString());
                                                        Course course = dataSnapshot.getValue(Course.class);
                                                        Log.d("testcourse", course.getName() +" " +course.getSite() +" " +course.getInfo());

                                                        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                                                        final View infoAlertBox = factory.inflate(R.layout.create_course, null);
                                                        final EditText courseName = infoAlertBox.findViewById(R.id.courseName);
                                                        final EditText courseInfo = infoAlertBox.findViewById(R.id.courseInfo);
                                                        final EditText courseSite = infoAlertBox.findViewById(R.id.courseSite);

                                                        courseName.setText(course.getName());
                                                        courseName.setEnabled(false);
                                                        courseInfo.setText(course.getInfo());
                                                        courseSite.setText(course.getSite());

                                                        new AlertDialog.Builder(StudentActivity.this)
                                                                .setView(infoAlertBox)
                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                .setTitle("Course Informations")
                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        ((ViewGroup) infoAlertBox.getParent()).removeView(infoAlertBox);

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
                                popup.show();



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
