package com.example.alnik.examquiz.Student;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchCourseActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView searchRecycleView;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mCourses;
    //private DatabaseReference userRequest;
    private DatabaseReference requestRef;
    private DatabaseReference subscriptionsRef;
    FirebaseRecyclerAdapter<Course, searchViewHolder> searchRecycleViewAdapter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCourses = FirebaseDatabase.getInstance().getReference("Courses");
        requestRef = FirebaseDatabase.getInstance().getReference("Requests");
        subscriptionsRef = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Users").child(Global.currentUser.getId());
        searchView = findViewById(R.id.searchView);
        searchRecycleView = findViewById(R.id.searchRecycleView);
        searchRecycleView.hasFixedSize();
        searchRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        try{
            mCourses.keepSynced(true);
            requestRef.keepSynced(true);
            subscriptionsRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                searchRecycleView.setAdapter(searchRecycleViewAdapter1);

            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchRecycleView.setAdapter(searchRecycleViewAdapter1);

                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Query Q = mCourses.orderByChild("name").equalTo(newText);

                FirebaseRecyclerAdapter<Course, searchViewHolder> searchRecycleViewAdapter = new FirebaseRecyclerAdapter<Course, searchViewHolder>(
                        Course.class, R.layout.single_search_course, searchViewHolder.class, Q) {
                    @Override
                    protected void populateViewHolder(final searchViewHolder viewHolder, final Course model, int position) {

                        viewHolder.setName(model.getName());
                        viewHolder.setOwnerName(model.getOwnersName());

                        String courseID = getRef(position).getKey();

                        requestRef.child("Users").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("test", courseID);

                                Log.d("test", dataSnapshot.toString());
                                if(dataSnapshot.hasChild(courseID)){

                                    viewHolder.request.setText("Cancel Request");

                                } else {

                                    subscriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild(courseID)){
                                                viewHolder.request.setText("Subscribed");
                                                viewHolder.request.setEnabled(false);

                                            } else{

                                                viewHolder.request.setText("Request");

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

                        viewHolder.request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String courseID = getRef(position).getKey();

                                if(viewHolder.request.getText().toString().equals("Request")){

                                    requestRef.child("Users").child(mCurrentUser.getUid()).child(courseID).setValue(new Time(System.currentTimeMillis()));
                                    requestRef.child("Courses").child(courseID).child(mCurrentUser.getUid()).setValue(new Time(System.currentTimeMillis()));
                                    viewHolder.request.setText("Cancel Request");


                                } else if(viewHolder.request.getText().toString().equals("Cancel Request")){

                                    requestRef.child("Users").child(mCurrentUser.getUid()).child(courseID).removeValue();
                                    requestRef.child("Courses").child(courseID).child(mCurrentUser.getUid()).removeValue();
                                    viewHolder.request.setText("Request");


                                }

                            }
                        });

                    }
                };
                searchRecycleView.setAdapter(searchRecycleViewAdapter);
                return false;
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        searchRecycleViewAdapter1 = new FirebaseRecyclerAdapter<Course, searchViewHolder>(
                Course.class, R.layout.single_search_course, searchViewHolder.class, mCourses.orderByChild("name")) {
            @Override
            protected void populateViewHolder(final searchViewHolder viewHolder, final Course model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setOwnerName(model.getOwnersName());

                String courseID = getRef(position).getKey();

                requestRef.child("Users").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("test", courseID);

                        Log.d("test", dataSnapshot.toString());
                        if(dataSnapshot.hasChild(courseID)){

                            viewHolder.request.setText("Cancel Request");

                        } else {

                            subscriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(courseID)){
                                        viewHolder.request.setText("Subscribed");
                                        viewHolder.request.setEnabled(false);

                                    } else{

                                        viewHolder.request.setText("Request");

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

                viewHolder.request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       String courseID = getRef(position).getKey();

                       if(viewHolder.request.getText().toString().equals("Request")){

                           requestRef.child("Users").child(mCurrentUser.getUid()).child(courseID).setValue(new Time(System.currentTimeMillis()));
                           requestRef.child("Courses").child(courseID).child(mCurrentUser.getUid()).setValue(new Time(System.currentTimeMillis()));
                           viewHolder.request.setText("Cancel Request");


                       } else if(viewHolder.request.getText().toString().equals("Cancel Request")){

                           requestRef.child("Users").child(mCurrentUser.getUid()).child(courseID).removeValue();
                           requestRef.child("Courses").child(courseID).child(mCurrentUser.getUid()).removeValue();
                           viewHolder.request.setText("Request");


                       }

                    }
                });
            }
        };
        searchRecycleView.setAdapter(searchRecycleViewAdapter1);

    }

    public static class searchViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView searchName;
        TextView ownerName;
        Button request;
        View view;

        public searchViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            searchName = itemView.findViewById(R.id.searchName);
            ownerName = itemView.findViewById(R.id.ownerName);
            request = itemView.findViewById(R.id.request);

            view = itemView.findViewById(R.id.single_searchCourse);

        }

        public void setName(String title) {

            searchName.setText(title);
        }

        public void setOwnerName(String name){

            ownerName.setText(name);
        }


    }

}
