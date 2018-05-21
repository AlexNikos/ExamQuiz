package com.example.alnik.examquiz.Course;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Teacher.TeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager mViewPager;
    private CoursePagerAdapter mCoursePagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar toolbar;
//    private TabItem databaseTab;
//    private TabItem testsTab;
//    private TabItem notificationsTab;
//    private TabItem subsTab;


    private DatabaseReference myRefUser;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private DatabaseReference multipleRef;
    private DatabaseReference trueFalseRef;
    private DatabaseReference shortAnswerRef;
    private DatabaseReference allQuestionsRef;

    private TextView nameView;
    private TextView emailView;
    private String fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        toolbar = (Toolbar) findViewById(R.id.courseToolbar);
        setSupportActionBar(toolbar);

//        databaseTab = findViewById(R.id.DatabaseTab);
////        testsTab = findViewById(R.id.TestsTab);
////        notificationsTab = findViewById(R.id.NotificationsTab);
////        subsTab = findViewById(R.id.SubsTab);

        //Intent intent = getIntent();
        //courseName = intent.getExtras().getString("courseName");
        //courseID = intent.getExtras().getString("courseID");
        //courseName = Global.course.getName();
        //courseID = Global.course.getId();
        getSupportActionBar().setTitle(Global.course.getName());

        multipleRef = FirebaseDatabase.getInstance().getReference("Questions").child(Global.course.getId()).child("MultipleChoice");
        trueFalseRef = FirebaseDatabase.getInstance().getReference("Questions").child(Global.course.getId()).child("TrueFalse");
        shortAnswerRef = FirebaseDatabase.getInstance().getReference("Questions").child(Global.course.getId()).child("ShortAnswer");
        allQuestionsRef = FirebaseDatabase.getInstance().getReference("Questions").child(Global.course.getId());

        try{
            multipleRef.keepSynced(true);
            trueFalseRef.keepSynced(true);
            shortAnswerRef.keepSynced(true);
            allQuestionsRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }


        mViewPager = (ViewPager) findViewById(R.id.CourseViewPager);
        mCoursePagerAdapter = new CoursePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mCoursePagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == 0){

                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_database);
                }
                if(position == 1){

                    toolbar.getMenu().clear();

                }
                if(position == 2){

                    toolbar.getMenu().clear();

                }
                if(position == 3){

                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_subs);

                    FirebaseDatabase.getInstance().getReference("Requests").child("Courses").child(Global.course.getId())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long count = dataSnapshot.getChildrenCount();
                                    if(count == 0){

                                        toolbar.getMenu().getItem(0).setTitle("");
                                        toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_notification);

                                    } else if(count == 1 ){

                                        toolbar.getMenu().getItem(0).setTitle(count +" new request");
                                        toolbar.getMenu().getItem(0).setIcon(R.drawable.notifynew);

                                    } else{

                                        toolbar.getMenu().getItem(0).setTitle(count +" new requests");
                                        toolbar.getMenu().getItem(0).setIcon(R.drawable.notifynew);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.courseTab);
        mTabLayout.setupWithViewPager(mViewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewCourse);
        navigationView.setNavigationItemSelectedListener(this);

        nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_fullname);
        emailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);

        myRefUser.child("fullname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = dataSnapshot.getValue().toString();
                nameView.setText(fullName);
                emailView.setText(currentUser.getEmail().toString());
                Log.d("test", fullName);
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
        getMenuInflater().inflate(R.menu.menu_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.subs_requests){

            startActivity(new Intent(CourseActivity.this, RequestsActivity.class));

        }

        if(id == R.id.subs_clear_all){

            Toast.makeText(getApplicationContext(),"Settings Pressed!", Toast.LENGTH_LONG).show();
            return true;
        }

        if(id == R.id.multipleChoiceClear){

            multipleRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Toast.makeText(getApplicationContext(), "All Multiple Choice Questions Cleared!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        if(id == R.id.trueFalseClear){

            trueFalseRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Toast.makeText(getApplicationContext(), "All True False Questions Cleared!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        if(id == R.id.shortAnswerClear){

            shortAnswerRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Toast.makeText(getApplicationContext(), "All Short Answer Questions Cleared!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        if(id == R.id.clearAllQuestions){

            allQuestionsRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Toast.makeText(getApplicationContext(), "All Questions Cleared!", Toast.LENGTH_LONG).show();
                    }
                }
            });

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

        } else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                Global.currentUser = null;
                Global.test = null;
                Global.student = null;
                Global.course = null;
                Global.timeSubscripted = 0;
                startActivity(new Intent(CourseActivity.this, LoginActivity.class));
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
