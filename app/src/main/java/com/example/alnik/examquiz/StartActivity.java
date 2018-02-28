package com.example.alnik.examquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.alnik.examquiz.Teacher.TeacherActivity;
import com.example.alnik.examquiz.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("Users").child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    Toast.makeText(StartActivity.this, "Hello " +user.getName(), Toast.LENGTH_LONG).show();

                    if(user.getType().equals("Student")){
                        Intent accountIntent = new Intent(StartActivity.this, StudentActivity.class );
                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(accountIntent);
                        finish();

                    } else if(user.getType().equals("Teacher")){
                        Intent accountIntent = new Intent(StartActivity.this, TeacherActivity.class );
                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(accountIntent);
                        finish();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseAuth.getInstance().signOut();
                }
            });

        } else{
            Intent accountIntent = new Intent(StartActivity.this, LoginActivity.class );
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(accountIntent);
            finish();

        }
    }
}
