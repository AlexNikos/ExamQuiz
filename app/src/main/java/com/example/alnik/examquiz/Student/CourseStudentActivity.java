package com.example.alnik.examquiz.Student;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Anonymous.AnonymousActivity;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.StartActivity;
import com.example.alnik.examquiz.Teacher.TeacherActivity;
import com.example.alnik.examquiz.Teacher.TeacherPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CourseStudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String courseName, courseId;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private StudentPagerAdapter mStudentPagerAdapter;
    private TabLayout mTabLayout;

    private TextView nameView;
    private TextView emailView;

    Animation uptodown,downtoup;
    AppBarLayout studentAppbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Global.course.getName());

        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        studentAppbar = findViewById(R.id.studentAppbar);


        mViewPager = (ViewPager) findViewById(R.id.StudentViewPager);
        mStudentPagerAdapter = new StudentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mStudentPagerAdapter);

        studentAppbar.setAnimation(uptodown);
        mViewPager.setAnimation(downtoup);

        mTabLayout = (TabLayout) findViewById(R.id.studentTab);
        mTabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_fullname);
        nameView.setText(Global.currentUser.getFullname());
        emailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        emailView.setText(Global.currentUser.getEmail());
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
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.searchCourse) {
//
//
//
//            return true;
//        }

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

            deleteAccount();

        } else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                Global.currentUser = null;
                Global.test = null;
                Global.student = null;
                Global.course = null;
                Global.timeSubscripted = 0;
                Intent out = new Intent(CourseStudentActivity.this, LoginActivity.class);
                out.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(out);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteAccount() {

        ProgressDialog mRegDialog;

        mRegDialog = new ProgressDialog(this);
        mRegDialog.setMessage("Please wait.");
        mRegDialog.setCanceledOnTouchOutside(false);

        new AlertDialog.Builder(CourseStudentActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Your Account?")
                .setMessage("This action is permative!" +
                        "You can not recover your data if you continue")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mRegDialog.show();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mRegDialog.dismiss();
                                            Log.d("test", "User account deleted.");
                                            Toast.makeText(CourseStudentActivity.this, "Account successfully deleted.", Toast.LENGTH_LONG).show();
                                            Intent out = new Intent(CourseStudentActivity.this, LoginActivity.class);
                                            out.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(out);
                                            finish();
                                        }
                                    }
                                });

                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }
}
