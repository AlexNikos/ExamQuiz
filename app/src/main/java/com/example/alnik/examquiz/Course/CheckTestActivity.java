package com.example.alnik.examquiz.Course;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;

public class CheckTestActivity extends AppCompatActivity {

    Button PreviousBtn ,NextBtn;
    TextView currentQuestionNumber;
    TextView totalQuestionsNumber;
    RelativeLayout questionsInsert;

    private FirebaseUser currentUser;
    private DatabaseReference answersTestsParticipation;
    private DatabaseReference marksTestsParticipation;



    int i = 0, j = 1;
    long grade = 0, maxGrade = 0;
    ArrayList<Object> questions;
    ArrayList<String> ans;
    ArrayList<Long> marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Global.test.getTitle());

        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Answers").child("Tests").child(Global.test.getId()).child(Global.studentID);
        marksTestsParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(Global.studentID);


        GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
        answersTestsParticipation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                ans = dataSnapshot.getValue(t);
                Log.d("test", "onDataChange: " +ans.toString());

                GenericTypeIndicator<ArrayList<Long>> s = new GenericTypeIndicator<ArrayList<Long>>() {};

                marksTestsParticipation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        marks = dataSnapshot.getValue(s);

                        for(long l: marks){
                            Log.d("test", "marks: " +l);

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





                        PreviousBtn = findViewById(R.id.PreviousBtn);
                        NextBtn = findViewById(R.id.NextBtn);
                        questionsInsert = findViewById(R.id.questionsInsert);
                        currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
                        currentQuestionNumber.setText(String.valueOf(j));
                        totalQuestionsNumber = findViewById(R.id.totalQuestionsNumber);
                        totalQuestionsNumber.setText(String.valueOf(questions.size()));

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
                                        // NextBtn.setText("Submit");
                                        NextBtn.setEnabled(false);
                                        NextBtn.setVisibility(View.INVISIBLE);
                                    }

                                }
//                        else if(NextBtn.getText().equals("Submit")){
//
//                            Toast.makeText(getApplicationContext(), "End", Toast.LENGTH_LONG).show();
//
//                        }

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public View multiple(MultipleChoice question, String choice, long grade){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View multipleChoice = factory.inflate(R.layout.checking_multiplechoice, null);
        final TextView questionMultipleEnter = multipleChoice.findViewById(R.id.questionRunning);
        final TextView optionA = multipleChoice.findViewById(R.id.optionA);
        final TextView optionB = multipleChoice.findViewById(R.id.optionB);
        final TextView optionC = multipleChoice.findViewById(R.id.optionC);
        final TextView optionD = multipleChoice.findViewById(R.id.optionD);
        final TextView maxMark = multipleChoice.findViewById(R.id.maxMark);
        final TextView mark = multipleChoice.findViewById(R.id.mark);

        questionMultipleEnter.setText(question.getQuestion());
        optionA.setText(question.getOptionA());
        optionB.setText(question.getOptionB());
        optionC.setText(question.getOptionC());
        optionD.setText(question.getOptionD());
        mark.setText(String.valueOf(grade));
        maxMark.setText(String.valueOf(question.getMaxGrade()));


        if (choice.equals(question.getOptionA())){

            optionA.setBackgroundColor(Color.RED);

        } else if (choice.equals(question.getOptionB())){

            optionB.setBackgroundColor(Color.RED);

        } else if(choice.equals(question.getOptionC())){

            optionC.setBackgroundColor(Color.RED);

        } else if(choice.equals(question.getOptionD())){

            optionD.setBackgroundColor(Color.RED);
        }

        if (question.getAnswer().equals(question.getOptionA())){

            optionA.setBackgroundColor(Color.GREEN);

        } else if (question.getAnswer().equals(question.getOptionB())){

            optionB.setBackgroundColor(Color.GREEN);

        } else if(question.getAnswer().equals(question.getOptionC())){

            optionC.setBackgroundColor(Color.GREEN);

        } else if(question.getAnswer().equals(question.getOptionD())){

            optionD.setBackgroundColor(Color.GREEN);
        }


        return multipleChoice;
    }

    public View trueFalse(TrueFalse question, String choice, long grade){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View trueFalseQuestion = factory.inflate(R.layout.checking_truefalse, null);
        final TextView questionTrueFalseEnter = trueFalseQuestion.findViewById(R.id.questionRunning);
        final TextView maxMark = trueFalseQuestion.findViewById(R.id.maxMark);
        final TextView trueView = trueFalseQuestion.findViewById(R.id.trueView);
        final TextView falseView = trueFalseQuestion.findViewById(R.id.falseView);
        final TextView mark = trueFalseQuestion.findViewById(R.id.mark);
        questionTrueFalseEnter.setText(question.getQuestion());
        mark.setText(String.valueOf(grade));
        maxMark.setText(String.valueOf(question.getMaxGrade()));

        if(choice.equals("true")){

            trueView.setBackgroundColor(Color.RED);
        } else if(choice.equals("false")){

            falseView.setBackgroundColor(Color.RED);

        }

        if(String.valueOf(question.getAnswer()).equals("true")){

            trueView.setBackgroundColor(Color.GREEN);

        } else{

            falseView.setBackgroundColor(Color.GREEN);

        }

        return  trueFalseQuestion;

    }

    public  View shortAnswer(ShortAnswer question, String answer, long grade){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View shortAnswerQuestion = factory.inflate(R.layout.checking_shortanswer, null);
        final TextView questionShortAnswerEnter = shortAnswerQuestion.findViewById(R.id.questionRunning);
        final EditText answerShortAnswer = shortAnswerQuestion.findViewById(R.id.answerRunning);
        final TextView maxMark = shortAnswerQuestion.findViewById(R.id.maxMark);
        final EditText mark = shortAnswerQuestion.findViewById(R.id.mark);
        questionShortAnswerEnter.setText(question.getQuestion());
        mark.setText(String.valueOf(grade));
        maxMark.setText(String.valueOf(maxGrade));

        if(ans.get(i) != null){
            answerShortAnswer.setText(ans.get(i));
        }



        return  shortAnswerQuestion;
    }

    public void typeQuestion(int i){

        if(questions.get(i).getClass() == MultipleChoice.class){

            MultipleChoice question = (MultipleChoice)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = multiple(question, ans.get(i), marks.get(i));
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == TrueFalse.class){

            TrueFalse question = (TrueFalse)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = trueFalse(question, ans.get(i), marks.get(i));
            questionsInsert.addView(mView);

        } else if(questions.get(i).getClass() == ShortAnswer.class){

            ShortAnswer question = (ShortAnswer)questions.get(i);
            questionsInsert.removeAllViews();
            View mView = shortAnswer(question, ans.get(i), marks.get(i));
            questionsInsert.addView(mView);

        }

    }

    public long gradeCalculate(ArrayList<Object> questions, String[] ans){
        long grade = 0;

        for(Object w: questions){

            if(w.getClass() == MultipleChoice.class) {

                if( ans[questions.indexOf(w)].equals( ((MultipleChoice)w).getAnswer()) ){

                    grade = grade + ((MultipleChoice)w).getMaxGrade();
                }


            } else if(w.getClass() == TrueFalse.class){

                if( ans[questions.indexOf(w)] == String.valueOf( ((TrueFalse)w).getAnswer()) ){

                    grade = grade + ((TrueFalse)w).getMaxGrade();
                }

            }

        }

        return grade;
    }
}
