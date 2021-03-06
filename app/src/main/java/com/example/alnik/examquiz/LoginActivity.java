package com.example.alnik.examquiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Anonymous.AnonymousActivity;
import com.example.alnik.examquiz.Student.StudentActivity;
import com.example.alnik.examquiz.Teacher.TeacherActivity;
import com.example.alnik.examquiz.models.User;
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
    private CheckBox remember;
    private SharedPreferences toRemember;

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
        remember = findViewById(R.id.remember);

        mRegDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

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

        toRemember = this.getSharedPreferences("com.example.alnik.examquiz", Context.MODE_PRIVATE);

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(remember.isChecked()){
                    //Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_LONG).show();
                    toRemember.edit().putBoolean("remember", true).apply();
                    Log.d("test", "onCheckedChanged: "+ toRemember.getBoolean("remember", false));

                } else if(!remember.isChecked()){

                    //Toast.makeText(LoginActivity.this, "NO Checked", Toast.LENGTH_LONG).show();
                    toRemember.edit().putBoolean("remember", false).apply();
                    Log.d("test", "onCheckedChanged: "+ toRemember.getBoolean("remember", false));


                }

            }
        });
    }

    public void login(String email, String password){

        mRegDialog.setTitle("Loging in...");
        mRegDialog.setMessage("Please wait!");
        mRegDialog.setCanceledOnTouchOutside(false);
        mRegDialog.show();

        if(remember.isChecked()){
            //Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_LONG).show();
            toRemember.edit().putBoolean("remember", true).apply();
            Log.d("test", "onCheckedChanged: "+ toRemember.getBoolean("remember", false));

        } else if(!remember.isChecked()){

            //Toast.makeText(LoginActivity.this, "NO Checked", Toast.LENGTH_LONG).show();
            toRemember.edit().putBoolean("remember", false).apply();
            Log.d("test", "onCheckedChanged: "+ toRemember.getBoolean("remember", false));


        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //We're in!
                            mRegDialog.dismiss();

                             mUser = mAuth.getCurrentUser();

                             myRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

                             myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {

                                     Global.currentUser = dataSnapshot.getValue(User.class);
                                     Toast.makeText(LoginActivity.this, "Hello " +Global.currentUser.getFullname(), Toast.LENGTH_LONG)
                                             .show();

                                     if(Global.currentUser.getType().equals("Student")){
                                         Intent accountIntent = new Intent(LoginActivity.this, StudentActivity.class );
                                         accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(accountIntent);
                                         finish();

                                     } else if(Global.currentUser.getType().equals("Teacher")){
                                         Intent accountIntent = new Intent(LoginActivity.this, TeacherActivity.class );
                                         accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(accountIntent);
                                         finish();

                                     }

                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

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
