package com.example.alnik.examquiz.Teacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.alnik.examquiz.Course.ResultsStatisticsFragment;
import com.example.alnik.examquiz.Course.ResultsStudentsFragment;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;

public class ResultsActivity extends AppCompatActivity {

    private ResultsStatisticsFragment mResultsStatisticsFragment;
    private ResultsStudentsFragment mResultsStudentsFragment;
    private FrameLayout mFrameLayout;

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        TextView title = findViewById(R.id.title);
        title.setText(Global.test.getTitle());


        mFrameLayout = findViewById(R.id.main_frame);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);


        mResultsStatisticsFragment = new ResultsStatisticsFragment();
        mResultsStudentsFragment = new ResultsStudentsFragment();
        setFragment(mResultsStatisticsFragment);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_statistics:
                        setFragment(mResultsStatisticsFragment);
                        return true;
                    case R.id.navigation_students:
                        setFragment(mResultsStudentsFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }


}
