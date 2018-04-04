package com.example.alnik.examquiz.Course;

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

public class CheckTestActivity extends AppCompatActivity {

    Button PreviousBtn ,NextBtn;
    TextView currentQuestionNumber;
    TextView totalQuestionsNumber;
    TextView middle;
    RelativeLayout questionsInsert;
    LinearLayout indexLinear;

    private FirebaseUser currentUser;
    private DatabaseReference answersTestsParticipation;
    private DatabaseReference marksTestsParticipation;



    int i = 0, j = 1;
    long maxGrade = 0;
    ArrayList<Object> questions;
    ArrayList<String> ans;
    ArrayList<Long> marks;
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

        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Answers").child("Tests").child(Global.test.getId()).child(Global.student.getId());
        marksTestsParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(Global.student.getId());


        try{
            answersTestsParticipation.keepSynced(true);
            marksTestsParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }
        FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests").child(Global.test.getId()).child(Global.student.getId()).child("time").addListenerForSingleValueEvent(new ValueEventListener() {
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

                                if(NextBtn.getText().equals("Next")){

                                    i = ++i;
                                    j = ++j;
                                    currentQuestionNumber.setText(String.valueOf(j));

                                    if(i < questions.size()){

                                        Log.d("test", "question " +i +" is " +questions.get(i));
                                        typeQuestion(i);

                                    }

                                    if(j == questions.size()){

                                        NextBtn.setText("Overal Score");
                                        //NextBtn.setEnabled(false);
                                        //NextBtn.setVisibility(View.INVISIBLE);
                                    }

                                }
                                else if(NextBtn.getText().equals("Overal Score")){

                                    questionsInsert.removeAllViews();
                                    View mView = overalScore();
                                    questionsInsert.addView(mView);
                                    NextBtn.setEnabled(false);
                                    NextBtn.setVisibility(View.INVISIBLE);
                                    indexLinear.setVisibility(View.INVISIBLE);

                                }

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


        if(choice.equals("")){

            if (question.getAnswer().equals(question.getOptionA())) {

                optionA.setBackgroundColor(Color.BLUE);

            } else if (question.getAnswer().equals(question.getOptionB())) {

                optionB.setBackgroundColor(Color.BLUE);

            } else if (question.getAnswer().equals(question.getOptionC())) {

                optionC.setBackgroundColor(Color.BLUE);

            } else if (question.getAnswer().equals(question.getOptionD())) {

                optionD.setBackgroundColor(Color.BLUE);
            }

        }else {
            if (choice.equals(question.getOptionA())) {

                optionA.setBackgroundColor(Color.RED);

            } else if (choice.equals(question.getOptionB())) {

                optionB.setBackgroundColor(Color.RED);

            } else if (choice.equals(question.getOptionC())) {

                optionC.setBackgroundColor(Color.RED);

            } else if (choice.equals(question.getOptionD())) {

                optionD.setBackgroundColor(Color.RED);
            }


            if (question.getAnswer().equals(question.getOptionA())) {

                optionA.setBackgroundColor(Color.GREEN);

            } else if (question.getAnswer().equals(question.getOptionB())) {

                optionB.setBackgroundColor(Color.GREEN);

            } else if (question.getAnswer().equals(question.getOptionC())) {

                optionC.setBackgroundColor(Color.GREEN);

            } else if (question.getAnswer().equals(question.getOptionD())) {

                optionD.setBackgroundColor(Color.GREEN);
            }
        }

        return multipleChoice;
    }

    public View trueFalse(TrueFalse question, String choice, long grade) {

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

        if (choice.equals("")) {

            if (String.valueOf(question.getAnswer()).equals("true")) {

                trueView.setBackgroundColor(Color.BLUE);

            } else {

                falseView.setBackgroundColor(Color.BLUE);

            }

        } else {

            if (choice.equals("true")) {

                trueView.setBackgroundColor(Color.RED);
            } else if (choice.equals("false")) {

                falseView.setBackgroundColor(Color.RED);

            }

            if (String.valueOf(question.getAnswer()).equals("true")) {

                trueView.setBackgroundColor(Color.GREEN);

            } else {

                falseView.setBackgroundColor(Color.GREEN);

            }
        }

        return  trueFalseQuestion;

    }

    public  View shortAnswer(ShortAnswer question, String answer, long grade){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View shortAnswerQuestion = factory.inflate(R.layout.checking_shortanswer, null);
        final TextView questionShortAnswerEnter = shortAnswerQuestion.findViewById(R.id.questionRunning);
        final TextView answerShortAnswer = shortAnswerQuestion.findViewById(R.id.answerRunning);
        final TextView maxMark = shortAnswerQuestion.findViewById(R.id.maxMark);
        final EditText mark = shortAnswerQuestion.findViewById(R.id.mark);
        questionShortAnswerEnter.setText(question.getQuestion());
        mark.setText(String.valueOf(grade));
        maxMark.setText(String.valueOf(question.getMaxGrade()));

        if(ans.get(i) != null){
            answerShortAnswer.setText(ans.get(i));
        }

        mark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                mark.setSelection(mark.getText().length());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(mark.getText().toString().equals("")){
                    mark.setText("0");
                    mark.setSelection(mark.getText().length());


                }

                if( Long.parseLong( mark.getText().toString()) > question.getMaxGrade()){

                    Toast.makeText(getApplicationContext(), "Max score is " +question.getMaxGrade(), Toast.LENGTH_LONG).show();
                    mark.setText("0");
                    mark.setSelection(mark.getText().length());

                }
                marks.set(i, Long.parseLong( mark.getText().toString()));

                for(long g: marks){
                    Log.d("test", "shortAnswer: " +g);

                }

            }
        });

//        mark.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if(mark.getText().toString().equals("")){
//                    mark.setText("0");
//
//                }
//
//                if( Long.parseLong( mark.getText().toString()) > question.getMaxGrade()){
//
//                    Toast.makeText(getApplicationContext(), "Max mark is " +question.getMaxGrade(), Toast.LENGTH_LONG).show();
//                    mark.setText("0");
//                }
//                marks.set(i, Long.parseLong( mark.getText().toString()));
//
//                for(long g: marks){
//                    Log.d("test", "shortAnswer: " +g);
//
//                }
//                return false;
//            }
//        });




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



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(Global.student.getId()).setValue(marks, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){

                    FirebaseDatabase.getInstance().getReference("Marks").child("Users").child(Global.student.getId()).child(Global.test.getId()).setValue(marks, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }

    public View overalScore(){

        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View score = factory.inflate(R.layout.checking_score, null);
        final TextView name = score.findViewById(R.id.nameS);
        final TextView email = score.findViewById(R.id.emailS);
        final TextView time = score.findViewById(R.id.timeS);
        final TextView mark = score.findViewById(R.id.mark);
        final TextView maxMark = score.findViewById(R.id.maxMark);
        final LinearLayout insertPoint = score.findViewById(R.id.insertPoint);

        name.setText(Global.student.getFullname());
        email.setText(Global.student.getEmail());
        time.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(timePartitipation));

        long markt = 0;
        long maxMarkt = marks.get(marks.size()-1);
        for(long q: marks){

            markt = markt + q;
        }
        markt = markt - maxMarkt;

        mark.setText(String.valueOf(markt));
        maxMark.setText(String.valueOf(maxMarkt));

        int k = 1;
        for(int j = 0; j < marks.size()-1; j++){

            LayoutInflater factory2 = LayoutInflater.from(getApplicationContext());
            final View score2 = factory.inflate(R.layout.single_overal_check, null);
            final TextView question = score2.findViewById(R.id.question);
            final TextView markTextView = score2.findViewById(R.id.mark);

//            question.setText("Question " +k +" :");
//            markTextView.setText(marks.get(j) +"/" +maxMarkt);

            if(questions.get(j).getClass() == MultipleChoice.class){

                question.setText("Question " +k +" :");
                markTextView.setText(marks.get(j) +"/" +((MultipleChoice)questions.get(j)).getMaxGrade());
                k++;

            } else if(questions.get(j).getClass() == TrueFalse.class){

                question.setText("Question " +k +" :");
                markTextView.setText(marks.get(j) +"/" +((TrueFalse)questions.get(j)).getMaxGrade());
                k++;

            } else if(questions.get(j).getClass() == ShortAnswer.class){

                question.setText("Question " +k +" :" );
                markTextView.setText(marks.get(j) +"/" +((ShortAnswer)questions.get(j)).getMaxGrade());
                k++;

            }

            insertPoint.addView(score2);

        }

        return score;
    }


}
