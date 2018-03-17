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

        //courseName = getActivity().getIntent().getExtras().getString("courseName");
        //courseId = getActivity().getIntent().getExtras().getString("course_id");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        announcementsRef = FirebaseDatabase.getInstance().getReference("Announcements").child(Global.course.getId());

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

        announcementsRecyclerAdapter = new FirebaseRecyclerAdapter<Announcement, announcementsViewHolder>(
                Announcement.class,
                R.layout.single_notification,
                announcementsViewHolder.class,
                announcementsRef.orderByChild("time")
        ) {

            @Override
            protected void populateViewHolder(final announcementsViewHolder viewHolder, Announcement model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(model.getTime()));

                announceId = getRef(position).getKey();
                Log.d("test", "announces Id" +announceId);

                announcesReadRef = FirebaseDatabase.getInstance().getReference("AnnouncementsRead").child(announceId).child(currentUser.getUid());
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
//-----------------------------------popup menu for 3 dots------------------------------------------------------------
//                viewHolder.options.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        PopupMenu popup = new PopupMenu(view.getContext(), view);
//                        popup.inflate(R.menu.popup_menu);
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.delete:
//
//                                        new android.support.v7.app.AlertDialog.Builder(getContext())
//                                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                                .setTitle("Are you sure?")
//                                                .setMessage("Do you want to delete this Announcement?")
//                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                notificationRef.child(key).removeValue();
//                                                            }
//                                                        }
//                                                )
//                                                .setNegativeButton("No", null)
//                                                .show();
//
//                                        break;
//                                    case R.id.edit:
//
//                                        LayoutInflater factory = LayoutInflater.from(getContext());
//                                        final View notification = factory.inflate(R.layout.notification_create, null);
//                                        final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
//                                        final EditText notificationBody = notification.findViewById(R.id.notificationBody);
//
//                                        notificationRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                final Announcement mNotification = dataSnapshot.getValue(Announcement.class);
//
//                                                notificationTitle.setText(mNotification.getTitle());
//                                                notificationBody.setText(mNotification.getBody());
//
//
//                                                new android.support.v7.app.AlertDialog.Builder(getContext())
//                                                        .setTitle("Edit Announcement")
//                                                        .setView(notification)
//                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                        String title = notificationTitle.getText().toString();
//                                                                        String body = notificationBody.getText().toString();
//                                                                        mNotification.setTitle(title);
//                                                                        mNotification.setBody(body);
//
//                                                                        notificationRef.child(key).setValue(mNotification, new DatabaseReference.CompletionListener() {
//                                                                            @Override
//                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                                                                                if(databaseError == null){
//                                                                                    Toast.makeText(getContext(), "Announcement Edited", Toast.LENGTH_LONG).show();
//
//                                                                                } else {
//                                                                                    Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
//
//                                                                                }
//
//                                                                            }
//                                                                        });
//
//                                                                        ((ViewGroup) notification.getParent()).removeView(notification);
//                                                                    }
//                                                                }
//                                                        )
//                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                                ((ViewGroup) notification.getParent()).removeView(notification);
//
//                                                            }
//                                                        })
//                                                        .show();
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                        break;
//                                }
//                                return false;
//                            }
//                        });
//                        popup.show();
//                    }
//                });
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
