package com.example.alnik.examquiz.Teacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Anonymous.AnonymousActivity;
import com.example.alnik.examquiz.CreateAccountActivity;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TeacherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private TeacherPagerAdapter mTeacherPagerAdapter;
    private TabLayout mTabLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRefUser;
    private DatabaseReference mCourses;

    private TextView nameView;
    private TextView emailView;
    private String fullName;

    private EditText CourseNameInput;
    private String CourseName;

    private RecyclerView CoursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.teacherViewPager);
        mTeacherPagerAdapter = new TeacherPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTeacherPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.teacherTab);
        mTabLayout.setupWithViewPager(mViewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mCourses = FirebaseDatabase.getInstance().getReference("Courses").child(currentUser.getUid());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.edit_profile) {
            // Handle the camera action
        } else if (id == R.id.delete_profile) {

            deleteAccount();

        } else if (id == R.id.about_course) {

        } else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                Global.currentUser = null;
                Global.test = null;
                Global.student = null;
                Global.course = null;
                Global.timeSubscripted = 0;
                Intent accountIntent = new Intent(TeacherActivity.this, LoginActivity.class );
                accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(accountIntent);
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

        new AlertDialog.Builder(TeacherActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Your Account?")
                .setMessage("This action is permative!\n" +
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
                                            Toast.makeText(TeacherActivity.this, "Account successfully deleted.", Toast.LENGTH_LONG).show();
                                            Intent out = new Intent(TeacherActivity.this, LoginActivity.class);
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

