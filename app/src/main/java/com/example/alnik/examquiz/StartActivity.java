package com.example.alnik.examquiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.alnik.examquiz.Student.StudentActivity;
import com.example.alnik.examquiz.Teacher.TeacherActivity;
import com.example.alnik.examquiz.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private ProgressDialog mRegDialog;
    FirebaseUser currentUser;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);


        mRegDialog = new ProgressDialog(this);
        mRegDialog.setMessage("Loading...");
        mRegDialog.setCanceledOnTouchOutside(false);
        mRegDialog.show();

        mSharedPreferences = this.getSharedPreferences("com.example.alnik.examquiz", Context.MODE_PRIVATE);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            if (currentUser.isAnonymous()) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mRegDialog.dismiss();
                                    Log.d("test", "User account deleted.");
                                    Intent accountIntent = new Intent(StartActivity.this, LoginActivity.class );
                                    accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(accountIntent);
                                    finish();
                                }
                            }
                        });



            } else {
                Log.d("test", "onCheckedChanged2: "+ mSharedPreferences.getBoolean("remember", false));


                if(mSharedPreferences.getBoolean("remember", false) == true){

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mRegDialog.dismiss();
                            Global.currentUser = dataSnapshot.getValue(User.class);
                            Toast.makeText(StartActivity.this, "Hello " + Global.currentUser.getName(), Toast.LENGTH_LONG).show();

                            if (Global.currentUser.getType().equals("Student")) {
                                Intent accountIntent = new Intent(StartActivity.this, StudentActivity.class);
                                accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(accountIntent);
                                finish();

                            } else if (Global.currentUser.getType().equals("Teacher")) {
                                Intent accountIntent = new Intent(StartActivity.this, TeacherActivity.class);
                                accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(accountIntent);
                                finish();

                            } else {
                                Intent accountIntent = new Intent(StartActivity.this, LoginActivity.class);
                                accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(accountIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mRegDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                        }
                    });
                } else{

                    Intent accountIntent = new Intent(StartActivity.this, LoginActivity.class);
                    accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(accountIntent);
                    finish();

                }



            }
        } else{

            mRegDialog.dismiss();
            Intent accountIntent = new Intent(StartActivity.this, LoginActivity.class);
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(accountIntent);
            finish();


        }
    }
}
