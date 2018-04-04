package com.example.alnik.examquiz.Student;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Announcement;
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

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementsStudentFragment extends Fragment {

    private DatabaseReference announcesReadRef;
    private DatabaseReference announcementsRef;
    private FirebaseUser currentUser;

    String courseName, courseId;
    String announceId;// = "empty";
    Query byTime;

    RecyclerView announcementsRecyclerView;

    FirebaseRecyclerAdapter<Announcement, announcementsViewHolder> announcementsRecyclerAdapter;

    View mView;

    public AnnouncementsStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_announcements_student, container, false);

        announcementsRef = FirebaseDatabase.getInstance().getReference("Announcements").child(Global.course.getId());

        try{
            announcementsRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        announcementsRecyclerView = mView.findViewById(R.id.announcementsRecyclerView);
        announcementsRecyclerView.hasFixedSize();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        announcementsRecyclerView.setLayoutManager(mLayoutManager);


        return  mView;
    }


    @Override
    public void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses").child(Global.course.getId()).child(currentUser.getUid())
//                .child("time").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long time = (long)dataSnapshot.getValue();
//                Log.d("test", "onDataChange: " +time);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        announcementsRecyclerAdapter = new FirebaseRecyclerAdapter<Announcement, announcementsViewHolder>(
                Announcement.class,
                R.layout.single_notification,
                announcementsViewHolder.class,
                announcementsRef.orderByChild("time").startAt(Global.timeSubscripted)

        ) {

            @Override
            protected void populateViewHolder(final announcementsViewHolder viewHolder, Announcement model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(model.getTime()));

                announceId = getRef(position).getKey();
                Log.d("test", "announces Id" +announceId);

                announcesReadRef = FirebaseDatabase.getInstance().getReference("AnnouncementsRead").child(announceId).child(currentUser.getUid());

                try{
                    announcesReadRef.keepSynced(true);

                }catch (Exception e){
                    Log.d("test", "error: "+ e.toString());
                }
                announcesReadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){

                            viewHolder.newAnnouncement.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        announceId = getRef(position).getKey();
                        Log.d("test", announceId);
                        announcesReadRef = FirebaseDatabase.getInstance().getReference("AnnouncementsRead").child(announceId).child(currentUser.getUid());
                        announcesReadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren()){

                                    viewHolder.newAnnouncement.setVisibility(View.GONE);

                                } else {

                                    announcesReadRef.setValue(new Time(System.currentTimeMillis()));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        LayoutInflater factory = LayoutInflater.from(getContext());
                        final View notification = factory.inflate(R.layout.notification_create, null);
                        final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
                        final EditText notificationBody = notification.findViewById(R.id.notificationBody);

                        announcementsRef.child(announceId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Announcement mAnnouncement = dataSnapshot.getValue(Announcement.class);

                                notificationTitle.setText(mAnnouncement.getTitle());
                                notificationTitle.setFocusable(false);
                                notificationTitle.setClickable(false);
                                notificationBody.setText(mAnnouncement.getBody());
                                notificationBody.setFocusable(false);
                                notificationBody.setClickable(false);

                                AlertDialog alert = new AlertDialog.Builder(getContext(), android.R.style.ThemeOverlay_Material_Light)
                                        .setTitle(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(mAnnouncement.getTime()))
                                        .setView(notification)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                announcementsRecyclerView.removeAllViews();

                                                ((ViewGroup) notification.getParent()).removeView(notification);

                                            }
                                        }).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };

        announcementsRecyclerView.setAdapter(announcementsRecyclerAdapter);

    }



    public static class announcementsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView notificationTitle;
        TextView newAnnouncement;
        ImageButton options;
        TextView timeView;
        View view;

        public announcementsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            notificationTitle = itemView.findViewById(R.id.sinleNotificationTitle);
            options = itemView.findViewById(R.id.optionsNotification);
            options.setVisibility(View.INVISIBLE);
            options.setEnabled(false);
            newAnnouncement = itemView.findViewById(R.id.newAnnouncement);
            timeView = itemView.findViewById(R.id.timeNotification);
            view = itemView.findViewById(R.id.singelNotificationView);

        }

        public void setTitle(String title) {

            notificationTitle.setText(title);
        }

        public void setTime(String time){

            timeView.setText(time);
        }


    }

}
