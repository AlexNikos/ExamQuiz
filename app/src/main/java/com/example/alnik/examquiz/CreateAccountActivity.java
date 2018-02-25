package com.example.alnik.examquiz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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


public class CreateAccountActivity extends AppCompatActivity {


    private TextInputEditText nameInput;
    private TextInputEditText surnameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private RadioGroup typeInput;
    private RadioButton radioTeacher;
    private RadioButton radioStudent;
    private Button createAccount;
    private Button cancel;

    private String name = "";
    private String surname = "";
    private String email = "";
    private String password = "";
    private String type;
    private String uid;

    private ProgressDialog mRegDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    FirebaseDatabase database;
    DatabaseReference myRef;


    public CreateAccountActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        typeInput = findViewById(R.id.type);
        radioTeacher = findViewById(R.id.teacher);
        radioStudent  = findViewById(R.id.student);
        nameInput = findViewById(R.id.nameInput);
        surnameInput = findViewById(R.id.surnameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        createAccount = findViewById(R.id.createButton);
        cancel = findViewById(R.id.cancelButton);

        mRegDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        typeInput.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.teacher:
                        type = "Teacher";

                        break;
                    case R.id.student:
                        type = "Student";
                        break;
                }
                Toast.makeText(CreateAccountActivity.this, type, Toast.LENGTH_SHORT).show();
            }
        });

        nameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameInput.getText().toString();
                surnameInput.requestFocus();
            }
        });

        surnameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surname = surnameInput.getText().toString();
                emailInput.requestFocus();
            }
        });


        emailInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = emailInput.getText().toString();
                if (temp.contains("@") && temp.contains(".") && !temp.isEmpty()){
                    email = emailInput.getText().toString();
                    passwordInput.requestFocus();
                    Toast.makeText(CreateAccountActivity.this, email, Toast.LENGTH_SHORT).show();

                } else {
                    emailInput.requestFocus();
                    Toast.makeText(CreateAccountActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();

                }
            }
        });

        passwordInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = passwordInput.getText().toString();

                if (temp.isEmpty() || temp.length() < 6){
                    Toast.makeText(CreateAccountActivity.this, "Password must have at least 6 chars", Toast.LENGTH_SHORT).show();

                } else {
                    password = passwordInput.getText().toString();
                }
                Log.d("test",type +" " +name +" " +surname +" " +email +" " +password);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                finish();
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type == null){
                    new AlertDialog.Builder(CreateAccountActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Please select your account type!")
                            .setPositiveButton("Student", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            type = "Student";
                                            radioStudent.setChecked(true);
                                        }
                                    }
                            )
                            .setNegativeButton("Teacher", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    type = "Teacher";
                                    radioTeacher.setChecked(true);

                                }
                            })
                            .show();
                    return;

                }
                name = nameInput.getText().toString();
                surname = surnameInput.getText().toString();
                email = emailInput.getText().toString();
                if (!email.contains("@") || !email.contains(".") || email.isEmpty() || email.equals("")){
                    Toast.makeText(CreateAccountActivity.this, "Enter a valide email", Toast.LENGTH_LONG).show();
                    emailInput.requestFocus();
                    return;
                }
                password = passwordInput.getText().toString();
                if(password.isEmpty() || password.length() < 6){
                    Toast.makeText(CreateAccountActivity.this, "Password must have at least 6 chars", Toast.LENGTH_SHORT).show();
                    passwordInput.requestFocus();
                    return;
                }
                mRegDialog.setTitle("Registering");
                mRegDialog.setMessage("Please wait while we create your account!");
                mRegDialog.setCanceledOnTouchOutside(false);
                mRegDialog.show();
                register(email, password);
                signIn(email, password);
                Log.d("test", "ok " +uid);

            }
        });


    }

    private void register(final String email, final String password){
        Log.d("test", email +" "+ password);

        if(!email.contains("@") || !email.contains(".") || email.isEmpty() ||password.isEmpty() || password.length() < 6){

            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            mRegDialog.dismiss();
                            mUser = mAuth.getCurrentUser();
                            uid = mUser.getUid();
                            Log.d("test",uid +" " +name);
                            User newUser = new User(uid, name, surname, email, password, type);
                            Log.d("test", newUser.getId() +" " +newUser.getName());
                            myRef = database.getReference();
                            myRef.child("Users").child(newUser.getId()).setValue(newUser);

                            Log.d("test", "createUserWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the User.
                            mRegDialog.hide();
                            Log.w("test", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            Toast.makeText(CreateAccountActivity.this, "Welcome " +name, Toast.LENGTH_LONG).show();

                            mUser = mAuth.getCurrentUser();
                            myRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("type");
                            // Log.d("test","uid is " + mUser.getUid());
                            Log.d("test", myRef.toString());
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("test", dataSnapshot.toString());

                                    String type = dataSnapshot.getValue().toString();
                                    Log.d("test", "type on server is  " + type);
                                    if(type.equals("Student")){
                                        Intent accountIntent = new Intent(CreateAccountActivity.this, StudentActivity.class );
                                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(accountIntent);
                                        finish();

                                    } else if(type.equals("Teacher")){
                                        Intent accountIntent = new Intent(CreateAccountActivity.this, TeacherActivity.class );
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
                            Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                        }

                    }
                });
    }

}
