package com.example.alnik.examquiz.Course;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Time;
import com.example.alnik.examquiz.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class RequestsActivity extends AppCompatActivity {

    RecyclerView requestsRecycleView;

    FirebaseUser mCurrentuser;
    DatabaseReference courseRequestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRequests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Requests");

        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        courseRequestsRef = FirebaseDatabase.getInstance().getReference("Requests").child("Courses").child(Global.course.getId());

        requestsRecycleView = findViewById(R.id.requestsRecycleView);
        requestsRecycleView.hasFixedSize();
        requestsRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Time, requestsViewHolder> requestsRecyclerAdapter = new FirebaseRecyclerAdapter<Time, requestsViewHolder>(
                Time.class,
                R.layout.single_request,
                requestsViewHolder.class,
                courseRequestsRef
        ) {

            @Override
            protected void populateViewHolder(final requestsViewHolder viewHolder, Time model, int position) {
                // viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(new SimpleDateFormat("yyyy/MM/dd").format(model.getTime()));

                String id = getRef(position).getKey();
                FirebaseDatabase.getInstance().getReference().child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        viewHolder.setTitle(user.getFullname());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),"Button pressed", Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getApplicationContext(), "Accepted\n" +id, Toast.LENGTH_LONG).show();
                        Time timeSub = new Time(System.currentTimeMillis());
                        FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses").child(Global.course.getId()).child(id).setValue(timeSub);
                        FirebaseDatabase.getInstance().getReference("Subscriptions").child("Users").child(id).child(Global.course.getId()).setValue(timeSub);
                        FirebaseDatabase.getInstance().getReference("Requests").child("Courses").child(Global.course.getId()).child(id).removeValue();
                        FirebaseDatabase.getInstance().getReference("Requests").child("Users").child(id).child(Global.course.getId()).removeValue();
                        Global.course.setSubscribers(Global.course.getSubscribers() + 1);
                        FirebaseDatabase.getInstance().getReference("Courses").child(Global.course.getId()).setValue(Global.course);

                    }
                });

                viewHolder.rejectRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getApplicationContext(), "Rejected\n" +id, Toast.LENGTH_LONG).show();

                    }
                });

            }
        };

        requestsRecycleView.setAdapter(requestsRecyclerAdapter);
    }

    public static class requestsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView requestName;
        TextView requestDate;
        Button acceptRequest;
        Button rejectRequest;
        View view;

        public requestsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            requestName = itemView.findViewById(R.id.requestName);
            requestDate = itemView.findViewById(R.id.requestDate);
            view = itemView.findViewById(R.id.single_request);
            acceptRequest = itemView.findViewById(R.id.acceptRequest);
            rejectRequest = itemView.findViewById(R.id.rejectRequest);

        }

        public void setTitle(String title) {

            requestName.setText(title);
        }

        public void setTime(String time){

            requestDate.setText(time);
        }


    }
}
