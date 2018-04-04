package com.example.alnik.examquiz.Student;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.Time;
import com.example.alnik.examquiz.models.TrueFalse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RunningTestActivity extends AppCompatActivity {

    Button PreviousBtn ,NextBtn;
    TextView currentQuestionNumber;
    TextView totalQuestionsNumber;
    TextView timer;
    RelativeLayout questionsInsert;

    private FirebaseUser currentUser;
    private DatabaseReference testTestsParticipation;
    private DatabaseReference testUsersParticipation;
    private DatabaseReference answersTestsParticipation;
    private DatabaseReference answersUsersParticipation;
    private DatabaseReference marksTestsParticipation;
    private DatabaseReference marksUsersParticipation;

    int i = 0, j = 1;
    long grade = 0, maxGrade = 0;
    ArrayList<Object> questions;
    String[] ans;
    long[] marks;

    CountDownTimer myCountDownTimerObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Global.test.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();

            }
        });

        timer = findViewById(R.id.timer);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        testUsersParticipation = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Users").child(currentUser.getUid()).child(Global.test.getId());
        testTestsParticipation = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests").child(Global.test.getId()).child(currentUser.getUid());
        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Answers").child("Tests").child(Global.test.getId()).child(currentUser.getUid());
        answersUsersParticipation = FirebaseDatabase.getInstance().getReference("Answers").child("Users").child(currentUser.getUid()).child(Global.test.getId());
        marksTestsParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(currentUser.getUid());
        marksUsersParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Users").child(currentUser.getUid()).child(Global.test.getId());
        try{
            testTestsParticipation.keepSynced(true);
            testTestsParticipation.keepSynced(true);
            answersTestsParticipation.keepSynced(true);
            answersUsersParticipation.keepSynced(true);
            marksTestsParticipation.keepSynced(true);
            marksUsersParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error " +e.toString());
        }

        questions = new ArrayList<>();

        for(Object w: Global.test.getQuestions()){

            String y = (String)((HashMap)w).get("type");

            if(y.equals("MultipleChoice")){

                MultipleChoice question = new MultipleChoice((String)((HashMap)w).get("question"), (String)((HashMap)w).get("optionA"), (String)((HashMap)w).get("optionB"),
                        (String)((HashMap)w).get("optionC"), (String)((HashMap)w).get("optionD"), (String)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));
                question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                maxGrade = maxGrade + question.getMaxGrade();
                questions.add(question);

            }else if(y.equals("TrueFalse")){

                TrueFalse question = new TrueFalse((String)((HashMap)w).get("question"), (boolean)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));
                question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                maxGrade = maxGrade + question.getMaxGrade();

                questions.add(question);


            }else if(y.equals("ShortAnswer")){

                ShortAnswer question = new ShortAnswer((String)((HashMap)w).get("question"), (String)((HashMap)w).get("id"));
                question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                maxGrade = maxGrade + question.getMaxGrade();

                questions.add(question);

            }
        }
        Log.d("test", questions.toString());
        ans = new String[questions.size()];
        marks = new long[questions.size()+1];

        for(int index = 0; index < ans.length; index++){
            ans[index] = "";
            marks[index] = 0;
        }


        PreviousBtn = findViewById(R.id.PreviousBtn);
        NextBtn = findViewById(R.id.NextBtn);
        questionsInsert = findViewById(R.id.questionsInsert);
        currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
        currentQuestionNumber.setText(String.valueOf(j));
        totalQuestionsNumber = findViewById(R.id.totalQuestionsNumber);
        totalQuestionsNumber.setText(String.valueOf(questions.size()));

        PreviousBtn.setEnabled(false);
        PreviousBtn.setVisibility(View.INVISIBLE);

        long time = Global.test.getEndDate() - System.currentTimeMillis();
        myCountDownTimerObject = new CountDownTimer(time, 1000)
        {
            public void onTick(long millisUntilFinished) {

                formattedTimeLeft(millisUntilFinished);
            }

            public void onFinish() {

                submitTest();

            }
        }.start();

        if(questions.size() == 1){

            NextBtn.setText("Submit");
        }

        Log.d("test", "questions size is " +questions.size());
        Log.d("test", "ans size is " +ans.length);
        Log.d("test", "question " +i +" is " +questions.get(i));

        typeQuestion(i);


        PreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("test","i before is " +String.valueOf(i));


                if(PreviousBtn.isEnabled()){

                    i = --i;
                    j = --j;
                    currentQuestionNumber.setText(String.valueOf(j));
                    NextBtn.setText("Next");
                    Log.d("test", "question " +i +" is " +questions.get(i));

                    typeQuestion(i);

                }

                if(i == 0){

                    PreviousBtn.setEnabled(false);
                    PreviousBtn.setVisibility(View.INVISIBLE);

                }

                Log.d("test", "i after is " +String.valueOf(i));

            }
        });

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("test","i before is " +String.valueOf(i));

                if(i == 0 && questions.size() != 1){
                    PreviousBtn.setEnabled(true);
                    PreviousBtn.setVisibility(View.VISIBLE);

                }

                if(NextBtn.getText().equals("Next")){

                    i = ++i;
                    j = ++j;
                    currentQuestionNumber.setText(String.valueOf(j));

                    if(i < questions.size()){

                        Log.d("test", "question " +i +" is " +questions.get(i));
                        typeQuestion(i);

                    }

                    if(j == questions.size()){
                        NextBtn.setText("Submit");
                    }

                } else if(NextBtn.getText().equals("Submit")){

                    submitTest();

                }

                Log.d("test", "i after is " +String.valueOf(i));

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myCountDownTimerObject.cancel();

    }

    public View multiple(MultipleChoice question, String choice){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View multipleChoice = factory.inflate(R.layout.running_multichoice, null);
        final TextView questionMultipleEnter = multipleChoice.findViewById(R.id.questionRunning);
        final TextView optionA = multipleChoice.findViewById(R.id.optionA);
        final TextView optionB = multipleChoice.findViewById(R.id.optionB);
        final TextView optionC = multipleChoice.findViewById(R.id.optionC);
        final TextView optionD = multipleChoice.findViewById(R.id.optionD);
        final RadioButton RadioOptionA = multipleChoice.findViewById(R.id.radioButtonA);
        final RadioButton RadioOptionB = multipleChoice.findViewById(R.id.radioButtonB);
        final RadioButton RadioOptionC = multipleChoice.findViewById(R.id.radioButtonC);
        final RadioButton RadioOptionD = multipleChoice.findViewById(R.id.radioButtonD);
        questionMultipleEnter.setText(question.getQuestion());
        optionA.setText(question.getOptionA());
        optionB.setText(question.getOptionB());
        optionC.setText(question.getOptionC());
        optionD.setText(question.getOptionD());

        if(choice == question.getOptionA()){

            RadioOptionA.setChecked(true);

        } else if(choice == question.getOptionB()){

            RadioOptionB.setChecked(true);

        }else if(choice == question.getOptionC()){

            RadioOptionC.setChecked(true);

        }else if(choice == question.getOptionD()){

            RadioOptionD.setChecked(true);
        }

        RadioOptionA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), optionA.getText().toString(), Toast.LENGTH_LONG).show();
                    ans[i] = question.getOptionA();
                    RadioOptionB.setChecked(false);
                    RadioOptionC.setChecked(false);
                    RadioOptionD.setChecked(false);

                    if(question.getAnswer().equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }

                }
            }
        });

        RadioOptionB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), optionB.getText().toString(), Toast.LENGTH_LONG).show();
                    ans[i] = question.getOptionB();
                    RadioOptionA.setChecked(false);
                    RadioOptionC.setChecked(false);
                    RadioOptionD.setChecked(false);

                    if(question.getAnswer().equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }
                }
            }
        });

        RadioOptionC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), optionC.getText().toString(), Toast.LENGTH_LONG).show();
                    ans[i] = question.getOptionC();
                    RadioOptionB.setChecked(false);
                    RadioOptionA.setChecked(false);
                    RadioOptionD.setChecked(false);

                    if(question.getAnswer().equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }
                }
            }
        });

        RadioOptionD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), optionD.getText().toString(), Toast.LENGTH_LONG).show();
                    ans[i] = question.getOptionD();
                    RadioOptionB.setChecked(false);
                    RadioOptionC.setChecked(false);
                    RadioOptionA.setChecked(false);

                    if(question.getAnswer().equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }
                }
            }
        });

        return multipleChoice;
    }

    public View trueFalse(TrueFalse question, String choice){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View trueFalseQuestion = factory.inflate(R.layout.running_truefalse, null);
        final TextView questionTrueFalseEnter = trueFalseQuestion.findViewById(R.id.questionRunning);
        final RadioButton RadioOptionTrue = trueFalseQuestion.findViewById(R.id.radioButtonA);
        final RadioButton RadioOptionFalse = trueFalseQuestion.findViewById(R.id.radioButtonB);
        questionTrueFalseEnter.setText(question.getQuestion());

        if(choice == "true"){

            RadioOptionTrue.setChecked(true);

        } else if(choice == "false"){

            RadioOptionFalse.setChecked(true);

        }

        RadioOptionTrue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Toast.makeText(getApplicationContext(), "It is checked", Toast.LENGTH_LONG).show();
                    ans[i] = "true";
                    RadioOptionFalse.setChecked(false);

                    if( String.valueOf(question.getAnswer()).equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);
                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }
                }
            }
        });

        RadioOptionFalse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Toast.makeText(getApplicationContext(), "It is checked", Toast.LENGTH_LONG).show();
                    ans[i] = "false";
                    RadioOptionTrue.setChecked(false);

                    if( String.valueOf(question.getAnswer()).equals(ans[i])){

                        marks[i] = question.getMaxGrade();
                        Log.d("test", "onCheckedChanged: " +marks[i]);
                    } else {

                        marks[i] = 0;
                        Log.d("test", "onCheckedChanged: " +marks[i]);

                    }

                }
            }
        });

        return  trueFalseQuestion;

    }

    public  View shortAnswer(String question, String answer){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View shortAnswerQuestion = factory.inflate(R.layout.running_shortanswer, null);
        final TextView questionShortAnswerEnter = shortAnswerQuestion.findViewById(R.id.questionRunning);
        final EditText answerShortAnswer = shortAnswerQuestion.findViewById(R.id.answerRunning);
        questionShortAnswerEnter.setText(question);

        if(ans[i] != null){
            answerShortAnswer.setText(ans[i]);
        }


        answerShortAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ans[i] = answerShortAnswer.getText().toString();

            }
        });

        return  shortAnswerQuestion;
    }

    public void typeQuestion(int i){

        if(questions.get(i).getClass() == MultipleChoice.class){

            MultipleChoice question = (MultipleChoice)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = multiple(question, ans[i]);
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == TrueFalse.class){

            TrueFalse question = (TrueFalse)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = trueFalse(question, ans[i]);
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == ShortAnswer.class){

            ShortAnswer question = (ShortAnswer)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = shortAnswer(question.getQuestion(), ans[i]);
            questionsInsert.addView(mView);

        }

    }

    void formattedTimeLeft(long millis) {

        int days = (int) (MILLISECONDS.toDays(millis) % 24);
        int hrs = (int) (MILLISECONDS.toHours(millis) % 24);
        int min = (int) (MILLISECONDS.toMinutes(millis) % 60);
        int sec = (int) (MILLISECONDS.toSeconds(millis) % 60);
        //int mls = (int) (millis % 1000);
        if(days == 0){

            timer.setText( String.format("Time left: %02d:%02d:%02d", hrs, min, sec ) );

        } else{

            timer.setText( String.format("Time left: %d days and %02d:%02d:%02d", days, hrs, min, sec));

        }

    }



    public void submitTest(){

        marks[marks.length-1] = maxGrade;

        Time time = new Time(System.currentTimeMillis());
        testTestsParticipation.setValue(time, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError == null){

                    testUsersParticipation.setValue(time, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                List ansList = new ArrayList<String>(Arrays.asList(ans));
                                List marksList = new ArrayList<>();
                                for(long l : marks) marksList.add(l);
                                answersTestsParticipation.setValue(ansList);
                                answersUsersParticipation.setValue(ansList);
                                marksTestsParticipation.setValue(marksList);
                                marksUsersParticipation.setValue(marksList);
                                Toast.makeText(getApplicationContext(), "Test successfully submitted.", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        }
                    });
                }
            }
        });


    }
}

