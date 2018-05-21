package com.example.alnik.examquiz.Anonymous;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class RoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mViewPager;
    private RoomPagerAdapter mRoomPagerAdapter;
    private TabLayout mTabLayout;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase questions;
    private DatabaseReference myRefUser;
    private DatabaseReference mRooms;

    private TextView nameView;
    private TextView emailView;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Global.room.getName());

        mViewPager = (ViewPager) findViewById(R.id.roomViewPager);
        mRoomPagerAdapter = new RoomPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mRoomPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.roomTab);
        mTabLayout.setupWithViewPager(mViewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mRooms = FirebaseDatabase.getInstance().getReference("Anonymous").child("Rooms");


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

        } else if (id == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            if (mAuth == null){
                Global.currentUser = null;
                Global.test = null;
                Global.student = null;
                Global.course = null;
                Global.timeSubscripted = 0;
                startActivity(new Intent(RoomActivity.this, LoginActivity.class));
                finish();
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
