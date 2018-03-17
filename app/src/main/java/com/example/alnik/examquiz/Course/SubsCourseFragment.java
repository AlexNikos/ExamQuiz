package com.example.alnik.examquiz.Course;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Time;
import com.example.alnik.examquiz.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubsCourseFragment extends Fragment {

    View mView;
    RecyclerView subsRecycleView;
    DatabaseReference courseSubsRef;
    DatabaseReference usersSubsRef;



    public SubsCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_subs_course, container, false);

        Log.d("test", "courseId: " + Global.course.getId());
        courseSubsRef = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses").child(Global.course.getId());

        subsRecycleView = mView.findViewById(R.id.subsRecycleView);
        subsRecycleView.setHasFixedSize(true);
        subsRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Time, subsViewHolder> subsRecyclerAdapter = new FirebaseRecyclerAdapter<Time, subsViewHolder>(
                Time.class,
                R.layout.single_subscriber,
                subsViewHolder.class,
                courseSubsRef
        ) {

            @Override
            protected void populateViewHolder(final subsViewHolder viewHolder, Time model, int position) {
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
                        Toast.makeText(getContext(),"Button pressed", Toast.LENGTH_LONG).show();
                    }
                });

            }
        };

        subsRecycleView.setAdapter(subsRecyclerAdapter);
    }

    public static class subsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView subName;
        TextView timeView;
        View view;

        public subsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            subName = itemView.findViewById(R.id.sesarchName);
            timeView = itemView.findViewById(R.id.subDate);
            view = itemView.findViewById(R.id.single_sub);

        }

        public void setTitle(String title) {

            subName.setText(title);
        }

        public void setTime(String time){

            timeView.setText(time);
        }


    }
}
