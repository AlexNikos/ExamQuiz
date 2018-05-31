package com.example.alnik.examquiz.Anonymous;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.LoginActivity;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Student.CourseStudentActivity;
import com.example.alnik.examquiz.Student.StudentActivity;
import com.example.alnik.examquiz.models.Course;
import com.example.alnik.examquiz.models.Room;
import com.example.alnik.examquiz.models.Time;
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

public class AnonymousActivity extends AppCompatActivity {

    private String room;
    private EditText txt;
    private RecyclerView roomsRecycleView;
    private ProgressDialog mRegDialog;
    private FirebaseUser mCurrentUser;
    private DatabaseReference rooms;
    private DatabaseReference roomRoomParticiptions;
    private DatabaseReference userRoomParticipations;
    private DatabaseReference allRooms;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        roomsRecycleView = findViewById(R.id.roomsRecycleView);
        roomsRecycleView.setHasFixedSize(true);
        roomsRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRoomParticipations = FirebaseDatabase.getInstance().getReference("Anonymous").child("RoomParticipations").child("Users").child(mCurrentUser.getUid());
        allRooms = FirebaseDatabase.getInstance().getReference("Anonymous").child("Rooms");
        rooms = FirebaseDatabase.getInstance().getReference("Anonymous").child("RoomOwnership").child("Room");
        roomRoomParticiptions = FirebaseDatabase.getInstance().getReference("Anonymous").child("RoomParticipations").child("Rooms");

        mRegDialog = new ProgressDialog(this);
        mRegDialog.setMessage("Please wait.");
        mRegDialog.setCanceledOnTouchOutside(false);


        txt = new EditText(AnonymousActivity.this);
        txt.setSingleLine(true);


        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AnonymousActivity.this)
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Add New Room")
                        .setMessage("Enter Room name:")
                        .setView(txt)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        room = txt.getText().toString();

                                        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if(!dataSnapshot.hasChild(room)){

                                                    Toast.makeText(AnonymousActivity.this, "Room does not exist!", Toast.LENGTH_LONG).show();

                                                } else{
                                                    allRooms.orderByChild("name").equalTo(room).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Room toAdd;
                                                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                toAdd = ds.getValue(Room.class);
                                                                Log.d("test", "toAdd name: " +toAdd.getName());
                                                                Log.d("test", "toAdd name: " +toAdd.getId());

                                                                long time = System.currentTimeMillis();
                                                                userRoomParticipations.child(toAdd.getId()).setValue(new Time(time));
                                                                roomRoomParticiptions.child(toAdd.getId()).child(mCurrentUser.getUid()).setValue(new Time(time));
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

                                        txt.setText("");
                                        Log.d("test", room);
                                        ((ViewGroup) txt.getParent()).removeView(txt);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ViewGroup) txt.getParent()).removeView(txt);

                            }
                        })
                        .show();

            }
        });
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
                                mRegDialog.dismiss();
                                Log.d("test", "User account deleted.");
                                Intent out = new Intent(AnonymousActivity.this, LoginActivity.class);
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

        FirebaseRecyclerAdapter<Time, RoomViewHolder> roomRecycleViewAdapter = new FirebaseRecyclerAdapter<Time, RoomViewHolder>(

                Time.class,
                R.layout.single_student_course,
                RoomViewHolder.class,
                userRoomParticipations

        ) {
            @Override
            protected void populateViewHolder(final RoomViewHolder roomViewHolder, Time subs, int i) {

                String room_id = getRef(i).getKey();
                Log.d("test", "Room id to populate " +room_id);

                allRooms.child(room_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("test", dataSnapshot.toString());
                        Room room = dataSnapshot.getValue(Room.class);
                        //Global.room = room;

                        roomViewHolder.setName(room.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                roomViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String room_id = getRef(i).getKey();

                        allRooms.child(room_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Room room = dataSnapshot.getValue(Room.class);
                                Global.room = room;
                                Log.d("test", "on click room name " +Global.room.getName());
                                startActivity(new Intent(AnonymousActivity.this, AnonymousQuestionerActivity.class));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };

        roomsRecycleView.setAdapter(roomRecycleViewAdapter);

    }


    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView singleSudentSubscriptionTime;
        TextView singleSudentCourseName;
        TextView textView5;
        ImageButton singleStudentCourseOptions;

        public RoomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            singleSudentSubscriptionTime = mView.findViewById(R.id.singleSudentSubscriptionTime);
            singleSudentSubscriptionTime.setVisibility(View.GONE);
            singleSudentCourseName = mView.findViewById(R.id.singleSudentCourseName);
            singleStudentCourseOptions = mView.findViewById(R.id.singleStudentCourseOptions);
            singleStudentCourseOptions.setVisibility(View.GONE);
            textView5 = mView.findViewById(R.id.textView5);
            textView5.setVisibility(View.GONE);

        }

        public void setName(String name){

            singleSudentCourseName.setText(name);

        }

    }

}
