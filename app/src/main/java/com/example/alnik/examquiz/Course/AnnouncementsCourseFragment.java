package com.example.alnik.examquiz.Course;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.Announcement;
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
public class AnnouncementsCourseFragment extends Fragment {

    FirebaseRecyclerAdapter<Announcement, notificationViewHolder> notificationRecyclerAdapter;
    private View mNotificationView;
    private FloatingActionButton createNotification;
    private RecyclerView notificationsRecycleView;

    private DatabaseReference notificationRef;
    private DatabaseReference announcementReadRef;
    private FirebaseUser currentUser;

    public AnnouncementsCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mNotificationView = inflater.inflate(R.layout.fragment_notifications_course, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationRef = FirebaseDatabase.getInstance().getReference("Announcements").child(Global.course.getId());
        announcementReadRef = FirebaseDatabase.getInstance().getReference("AnnouncementsRead");

        try{
            notificationRef.keepSynced(true);
            announcementReadRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        createNotification = mNotificationView.findViewById(R.id.create_new_notification);
        createNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater factory = LayoutInflater.from(getContext());
                final View notification = factory.inflate(R.layout.notification_create, null);
                final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
                final EditText notificationBody = notification.findViewById(R.id.notificationBody);

                AlertDialog alertNotification = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Announcement")
                        .setView(notification)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String title = notificationTitle.getText().toString();
                                String body = notificationBody.getText().toString();
                                String id = notificationRef.push().getKey();

                                Announcement mAnnouncement = new Announcement(id, title, body);
                                notificationRef.child(id).setValue(mAnnouncement, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError == null){
                                            Toast.makeText(getContext(), "Announcement Created", Toast.LENGTH_LONG).show();
                                            notificationsRecycleView.smoothScrollToPosition(notificationRecyclerAdapter.getItemCount());
                                            ((ViewGroup) notification.getParent()).removeView(notification);

                                        } else {
                                            Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
                                            ((ViewGroup) notification.getParent()).removeView(notification);

                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                notificationTitle.setText("");
                                notificationBody.setText("");
                                ((ViewGroup) notification.getParent()).removeView(notification);
                            }
                        })
                        .show();

                alertNotification.setCanceledOnTouchOutside(false);
            }
        });

        notificationsRecycleView = mNotificationView.findViewById(R.id.notificatioRecycleView);
        notificationsRecycleView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        notificationsRecycleView.setLayoutManager(mLayoutManager);
        notificationsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    createNotification.hide();
                } else {
                    createNotification.show();
                }
            }
        });


        return mNotificationView;
    }

    @Override
    public void onStart() {
        super.onStart();

        notificationRecyclerAdapter = new FirebaseRecyclerAdapter<Announcement, notificationViewHolder>(
                Announcement.class,
                R.layout.single_notification,
                notificationViewHolder.class,
                notificationRef.orderByChild("time")
        ) {

            @Override
            protected void populateViewHolder(final notificationViewHolder viewHolder, Announcement model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(model.getTime()));

                String key= getRef(position).getKey();
//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater factory = LayoutInflater.from(getContext());
                        final View notification = factory.inflate(R.layout.notification_create, null);
                        final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
                        final EditText notificationBody = notification.findViewById(R.id.notificationBody);

                        notificationRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Announcement mAnnouncement = dataSnapshot.getValue(Announcement.class);

                                notificationTitle.setText(mAnnouncement.getTitle());
                                notificationTitle.setFocusable(false);
                                notificationTitle.setClickable(false);
                                notificationBody.setText(mAnnouncement.getBody());
                                notificationBody.setFocusable(false);
                                notificationBody.setClickable(false);

                                AlertDialog alert = new AlertDialog.Builder(getContext(), android.R.style.ThemeOverlay_Material_Dialog)
                                        .setTitle(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(mAnnouncement.getTime()))
                                        .setView(notification)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

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
                viewHolder.options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.inflate(R.menu.popup_menu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:

                                        new android.support.v7.app.AlertDialog.Builder(getContext())
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Are you sure?")
                                                .setMessage("Do you want to delete this Announcement?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                notificationRef.child(key).removeValue();
                                                                announcementReadRef.child(key).removeValue();

                                                            }
                                                        }
                                                )
                                                .setNegativeButton("No", null)
                                                .show();

                                        break;
                                    case R.id.edit:

                                        LayoutInflater factory = LayoutInflater.from(getContext());
                                        final View notification = factory.inflate(R.layout.notification_create, null);
                                        final EditText notificationTitle = notification.findViewById(R.id.notificationTitle);
                                        final EditText notificationBody = notification.findViewById(R.id.notificationBody);

                                        notificationRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                final Announcement mAnnouncement = dataSnapshot.getValue(Announcement.class);

                                                notificationTitle.setText(mAnnouncement.getTitle());
                                                notificationBody.setText(mAnnouncement.getBody());


                                                new android.support.v7.app.AlertDialog.Builder(getContext())
                                                        .setTitle("Edit Announcement")
                                                        .setView(notification)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        String title = notificationTitle.getText().toString();
                                                                        String body = notificationBody.getText().toString();
                                                                        mAnnouncement.setTitle(title);
                                                                        mAnnouncement.setBody(body);

                                                                        notificationRef.child(key).setValue(mAnnouncement, new DatabaseReference.CompletionListener() {
                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                                                if(databaseError == null){
                                                                                    Toast.makeText(getContext(), "Announcement Edited", Toast.LENGTH_LONG).show();

                                                                                } else {
                                                                                    Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();

                                                                                }

                                                                            }
                                                                        });

                                                                        ((ViewGroup) notification.getParent()).removeView(notification);
                                                                    }
                                                                }
                                                        )
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                ((ViewGroup) notification.getParent()).removeView(notification);

                                                            }
                                                        })
                                                        .show();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }
        };

        notificationsRecycleView.setAdapter(notificationRecyclerAdapter);

    }


    public static class notificationViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView notificationTitle;
        ImageButton options;
        TextView timeView;
        View view;

        public notificationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            notificationTitle = itemView.findViewById(R.id.sinleNotificationTitle);
            options = itemView.findViewById(R.id.optionsNotification);
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
