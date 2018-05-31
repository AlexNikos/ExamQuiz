package com.example.alnik.examquiz.Anonymous;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Student.RunningTestActivity;
import com.example.alnik.examquiz.Student.StudentScoreActivity;
import com.example.alnik.examquiz.models.Test;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class AnonymousQuestionerActivity extends AppCompatActivity {

    private RecyclerView questionersRecycleView;

    private FirebaseUser mCurrentUser;
    private DatabaseReference questionersRef;
    private DatabaseReference questionerUsersParticipation;
    private DatabaseReference questionerQuestionersParticipation;
    private ProgressDialog mRegDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_questioner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Global.room.getName());

        questionersRecycleView = findViewById(R.id.questionersRecycleView);
        questionersRecycleView.hasFixedSize();
        questionersRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        questionersRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questioners").child(Global.room.getId());
        questionerUsersParticipation = FirebaseDatabase.getInstance().getReference("Anonymous").child("QuestionerParticipations").child("Users").child(mCurrentUser.getUid());
        questionerQuestionersParticipation = FirebaseDatabase.getInstance().getReference("Anonymous").child("QuestionerParticipations").child("Questioners");

        mRegDialog = new ProgressDialog(this);
        mRegDialog.setMessage("Please wait.");
        mRegDialog.setCanceledOnTouchOutside(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.sign_out) {
            mRegDialog.show();


            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("test", "User account deleted.");
                                mRegDialog.dismiss();
                                Intent out = new Intent(AnonymousQuestionerActivity.this, LoginActivity.class);
                                out.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(out);
                                finish();
                            }
                        }
                    });

            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Test, TestStudentViewHolder> testRecyclerAdapter = new FirebaseRecyclerAdapter<Test, TestStudentViewHolder>(
                Test.class,
                R.layout.single_test,
                TestStudentViewHolder.class,
                questionersRef        ) {

            @Override
            protected void populateViewHolder(final TestStudentViewHolder viewHolder, Test model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setStartTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getStartDate()));
                viewHolder.setEndTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getEndDate()));

                String key= getRef(position).getKey();
//---------------------------------action on click a Questioner----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        questionerUsersParticipation.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    Toast.makeText(getApplicationContext(), "You have already participated!" , Toast.LENGTH_LONG).show();

                                } else {

                                    questionersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Global.test = dataSnapshot.getValue(Test.class);
                                            Log.d("test", "testID is  " +Global.test.getId());

                                            long currentTime = System.currentTimeMillis();
                                            if(Global.test.getStartDate() > currentTime){

                                                Toast.makeText(getApplicationContext(), "Not Available yet!", Toast.LENGTH_LONG).show();

                                            } else if(Global.test.getEndDate() < currentTime){

                                                Toast.makeText(getApplicationContext(), "Questioner has ended!", Toast.LENGTH_LONG).show();


                                            } else{


                                                startActivity(new Intent(getApplicationContext(), RunningQuestionerActivity.class));

                                            }
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
                });

                long currentDate = System.currentTimeMillis();
                if(model.getEndDate() > currentDate){

                   // viewHolder.activeButton.setText("Active");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.GREEN);
                } else {

                    //viewHolder.activeButton.setText("Inactive");
                    viewHolder.activeButton.setClickable(false);
                    viewHolder.activeButton.setBackgroundColor(Color.RED);

                }

            }
        };

        questionersRecycleView.setAdapter(testRecyclerAdapter);

    }




    public static class TestStudentViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView testTitle;
        Button activeButton;
        TextView startTimeView;
        TextView endTimeView;
        CheckBox participation;
        ImageButton optionsButton;
        View view;

        public TestStudentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            testTitle = itemView.findViewById(R.id.singleTestTitle);
            activeButton = itemView.findViewById(R.id.activeTestButton);
            startTimeView = itemView.findViewById(R.id.startTime);
            endTimeView = itemView.findViewById(R.id.endTime);
            participation = itemView.findViewById(R.id.participation);
            participation.setVisibility(View.GONE);
            optionsButton = itemView.findViewById(R.id.optionsButton);
            optionsButton.setVisibility(View.GONE);
            view = itemView.findViewById(R.id.singleTestView);


        }

        public void setTitle(String title) {

            testTitle.setText(title);
        }

        public void setStartTime(String time){

            startTimeView.setText(time);
        }

        public void setEndTime(String time){

            endTimeView.setText(time);
        }


    }
}
