package com.example.alnik.examquiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference nameRef;
    private DatabaseReference typeRef;


    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button creatAccButton;
    private Button anonymousButton;

    private ProgressDialog mRegDialog;

    private String email;
    private String password;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        creatAccButton = findViewById(R.id.createAccButton);
        anonymousButton = findViewById(R.id.anonymousButton);

        mRegDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null){
                    Toast.makeText(LoginActivity.this, "Signed in as: " + mUser.getEmail(), Toast.LENGTH_LONG).show();
                }

            }
        };


        emailField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                email = emailField.getText().toString();
                Log.d("test", email);

                if (i == EditorInfo.IME_ACTION_NEXT) {
                    if (email.contains("@") && email.contains(".") && !email.isEmpty()){

                        passwordField.requestFocus();
                        return true;

                    } else {

                        Toast.makeText(LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                        emailField.setText("");
                        return true;
                    }
                }
                return false;
            }
        });



        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                password = passwordField.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(password.isEmpty() || password.length() < 4){
                        Toast.makeText(LoginActivity.this, "Password must have at least 6 chars", Toast.LENGTH_SHORT).show();
                        passwordField.setText("");
                    } else {
                        login(email,password);

                    }
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if (inputFormat(email, password)){
                    login(email,password);
                }
            }
        });

        creatAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInAnonymously()
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in User's information
                                    Log.d("test", "signInAnonymously:success");
                                    //FirebaseUser User = mAuth.getCurrentUser();
                                    startActivity(new Intent(LoginActivity.this, AnonymousActivity.class));
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the User.
                                    Log.w("test", "signInAnonymously:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });
    }

    public void login(String email, String password){

        mRegDialog.setTitle("Loging in...");
        mRegDialog.setMessage("Please wait while you login your account!");
        mRegDialog.setCanceledOnTouchOutside(false);
        mRegDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //We're in!
                            mRegDialog.dismiss();

                             mUser = mAuth.getCurrentUser();

                             myRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
                             nameRef = myRef.child("name");
                             typeRef = myRef.child("type");

                             nameRef.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     Toast.makeText(LoginActivity.this, "Hello " +dataSnapshot.getValue().toString(), Toast.LENGTH_LONG)
                                             .show();
                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });

                            Log.d("test", myRef.toString());
                            typeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("test", dataSnapshot.toString());

                                    String type = dataSnapshot.getValue().toString();
                                    Log.d("test", "type on server is  " + type);
                                    if(type.equals("Student")){
                                        Intent accountIntent = new Intent(LoginActivity.this, StudentActivity.class );
                                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(accountIntent);
                                        finish();

                                    } else if(type.equals("Teacher")){
                                        Intent accountIntent = new Intent(LoginActivity.this, TeacherActivity.class );
                                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(accountIntent);
                                        finish();

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("test","onCancelled");

                                }
                            });

                        }else {
                            mRegDialog.hide();
                            Toast.makeText(LoginActivity.this, "Wrong email or password!", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUser = mAuth.getCurrentUser();

        if(mUser!=null){
            FirebaseAuth.getInstance().signOut();

        }
//        if(mUser != null){
//            myRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
//            nameRef = myRef.child("name");
//            typeRef = myRef.child("type");
//
//            nameRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Toast.makeText(LoginActivity.this, "Hello " +dataSnapshot.getValue().toString(), Toast.LENGTH_LONG)
//                            .show();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            Log.d("test", myRef.toString());
//            typeRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("test", dataSnapshot.toString());
//
//                    String type = dataSnapshot.getValue().toString();
//                    Log.d("test", "type on server is  " + type);
//                    if(type.equals("Student")){
//                        Intent accountIntent = new Intent(LoginActivity.this, StudentActivity.class );
//                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(accountIntent);
//                        finish();
//
//                    } else if(type.equals("Teacher")){
//                        Intent accountIntent = new Intent(LoginActivity.this, TeacherActivity.class );
//                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(accountIntent);
//                        finish();
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("test","onCancelled");
//
//                }
//            });
//
//
//        }

    }


    private boolean inputFormat(String email, String password){

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this,"Please input email and password", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!email.contains("@") || !email.contains(".") || password.length() < 6){
            Toast.makeText(LoginActivity.this,"Please input a valid email and password", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


}
