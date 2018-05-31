package com.example.alnik.examquiz.Anonymous;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.TrueFalse;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckQuestionerActivity extends AppCompatActivity {

    private Button PreviousBtn ,NextBtn;
    private TextView currentQuestionNumber;
    private TextView totalQuestionsNumber;
    private TextView middle;
    private RelativeLayout questionsInsert;
    private LinearLayout indexLinear;

    private FirebaseUser currentUser;
    private DatabaseReference answersTestsParticipation;

    int i = 0, j = 1;
    //long maxGrade = 0;
    ArrayList<Object> questions;
    ArrayList<String> ans;
    //ArrayList<Long> marks;
    long timePartitipation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_test);
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

        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Anonymous").child("Answers").child("Questioners").child(Global.test.getId()).child(Global.participantID);

        try{
            answersTestsParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        FirebaseDatabase.getInstance().getReference("Anonymous").child("QuestionerParticipations").child("Questioners").child(Global.test.getId()).child(Global.participantID).child("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timePartitipation = (long)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
        answersTestsParticipation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ans = dataSnapshot.getValue(t);

                questions = new ArrayList<>();

                for(Object w: Global.test.getQuestions()){

                    String y = (String)((HashMap)w).get("type");

                    if(y.equals("MultipleChoice")){

                        MultipleChoice question = new MultipleChoice((String)((HashMap)w).get("question"), (String)((HashMap)w).get("optionA"), (String)((HashMap)w).get("optionB"),
                                (String)((HashMap)w).get("optionC"), (String)((HashMap)w).get("optionD"), (String)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));
                        question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                        questions.add(question);

                    }else if(y.equals("TrueFalse")){

                        TrueFalse question = new TrueFalse((String)((HashMap)w).get("question"), (boolean)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));
                        question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                        questions.add(question);


                    }else if(y.equals("ShortAnswer")){

                        ShortAnswer question = new ShortAnswer((String)((HashMap)w).get("question"), (String)((HashMap)w).get("id"));
                        question.setMaxGrade(   (long) ((HashMap)w).get("maxGrade")  );
                        questions.add(question);

                    }
                }

                indexLinear = findViewById(R.id.indexLinear);
                PreviousBtn = findViewById(R.id.PreviousBtn);
                NextBtn = findViewById(R.id.NextBtn);
                questionsInsert = findViewById(R.id.questionsInsert);
                currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
                currentQuestionNumber.setText(String.valueOf(j));
                totalQuestionsNumber = findViewById(R.id.totalQuestionsNumber);
                totalQuestionsNumber.setText(String.valueOf(questions.size()));
                middle = findViewById(R.id.middle);

                PreviousBtn.setEnabled(false);
                PreviousBtn.setVisibility(View.INVISIBLE);

                if(questions.size() == 1){

                    //NextBtn.setText("Submit");
                    NextBtn.setEnabled(false);
                    NextBtn.setVisibility(View.INVISIBLE);
                }

                Log.d("test", "questions size is " +questions.size());
                Log.d("test", "ans size is " +ans.size());
                Log.d("test", "question " +i +" is " +questions.get(i));

                typeQuestion(i);

                PreviousBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("test","i before is " +String.valueOf(i));

                        if(j == questions.size() && questions.size() != 1){
                            NextBtn.setEnabled(true);
                            NextBtn.setVisibility(View.VISIBLE);

                        }

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

                        if(indexLinear.getVisibility() == View.INVISIBLE){
                            NextBtn.setEnabled(true);
                            NextBtn.setVisibility(View.VISIBLE);
                            indexLinear.setVisibility(View.VISIBLE);
                            i = questions.size() - 1;
                            j = i+1;
                            typeQuestion(i);
                            NextBtn.setText("Overal Score");
                            currentQuestionNumber.setText(String.valueOf(j));
                            totalQuestionsNumber.setText(String.valueOf(questions.size()));
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

                        if(NextBtn.isEnabled()){

                            i = ++i;
                            j = ++j;
                            currentQuestionNumber.setText(String.valueOf(j));

                            if(i < questions.size()){

                                Log.d("test", "question " +i +" is " +questions.get(i));
                                typeQuestion(i);

                            }

                            if(j == questions.size()){

                                NextBtn.setEnabled(false);
                                NextBtn.setVisibility(View.INVISIBLE);
                            }

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public View multiple(MultipleChoice question, String choice){

        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View multipleChoice = factory.inflate(R.layout.checking_multiplechoice, null);
        final TextView questionMultipleEnter = multipleChoice.findViewById(R.id.questionRunning);
        final TextView optionA = multipleChoice.findViewById(R.id.optionA);
        final TextView optionB = multipleChoice.findViewById(R.id.optionB);
        final TextView optionC = multipleChoice.findViewById(R.id.optionC);
        final TextView optionD = multipleChoice.findViewById(R.id.optionD);
        final LinearLayout scoreLinear = multipleChoice.findViewById(R.id.scoreLinear);
        scoreLinear.setVisibility(View.GONE);
        final LinearLayout linearA = multipleChoice.findViewById(R.id.A);
        final LinearLayout linearB = multipleChoice.findViewById(R.id.B);
        final LinearLayout linearC = multipleChoice.findViewById(R.id.C);
        final LinearLayout linearD = multipleChoice.findViewById(R.id.D);


        questionMultipleEnter.setText(question.getQuestion());
        optionA.setText(question.getOptionA());
        optionB.setText(question.getOptionB());
        optionC.setText(question.getOptionC());
        optionD.setText(question.getOptionD());


            if (choice.equals(question.getOptionA())) {

                linearA.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));

            } else if (choice.equals(question.getOptionB())) {

                linearB.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));

            } else if (choice.equals(question.getOptionC())) {

                linearC.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));

            } else if (choice.equals(question.getOptionD())) {

                linearD.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));
            }

        return multipleChoice;
    }

    public View trueFalse(TrueFalse question, String choice) {

        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View trueFalseQuestion = factory.inflate(R.layout.checking_truefalse, null);
        final TextView questionTrueFalseEnter = trueFalseQuestion.findViewById(R.id.questionRunning);
        final TextView trueView = trueFalseQuestion.findViewById(R.id.trueView);
        final TextView falseView = trueFalseQuestion.findViewById(R.id.falseView);
        final LinearLayout scoreLinear = trueFalseQuestion.findViewById(R.id.scoreLinear);
        scoreLinear.setVisibility(View.GONE);
        final LinearLayout linearA = trueFalseQuestion.findViewById(R.id.A);
        final LinearLayout linearB = trueFalseQuestion.findViewById(R.id.B);

        questionTrueFalseEnter.setText(question.getQuestion());

            if (choice.equals("true")) {

                linearA.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));
            } else if (choice.equals("false")) {

                linearB.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.choice_background));

            }

        return  trueFalseQuestion;

    }

    public  View shortAnswer(ShortAnswer question, String answer){

        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View shortAnswerQuestion = factory.inflate(R.layout.checking_shortanswer, null);
        final TextView questionShortAnswerEnter = shortAnswerQuestion.findViewById(R.id.questionRunning);
        final TextView answerShortAnswer = shortAnswerQuestion.findViewById(R.id.answerRunning);
        final LinearLayout scoreLinear = shortAnswerQuestion.findViewById(R.id.scoreLinear);
        scoreLinear.setVisibility(View.GONE);

        questionShortAnswerEnter.setText(question.getQuestion());


        if(ans.get(i) != null){
            answerShortAnswer.setText(ans.get(i));
        }


        return  shortAnswerQuestion;
    }

    public void typeQuestion(int i){

        if(questions.get(i).getClass() == MultipleChoice.class){

            MultipleChoice question = (MultipleChoice)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = multiple(question, ans.get(i));
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == TrueFalse.class){

            TrueFalse question = (TrueFalse)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = trueFalse(question, ans.get(i));
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == ShortAnswer.class){

            ShortAnswer question = (ShortAnswer)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = shortAnswer(question, ans.get(i));
            questionsInsert.addView(mView);

        }

    }
}
