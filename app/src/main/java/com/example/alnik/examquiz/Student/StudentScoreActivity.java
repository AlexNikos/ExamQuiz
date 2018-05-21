package com.example.alnik.examquiz.Student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentScoreActivity extends AppCompatActivity {


    Button NextBtn;
    RelativeLayout questionsInsert;

    private DatabaseReference marksTestsParticipation;

    long maxGrade = 0;
    ArrayList<Object> questions;
    ArrayList<Long> marks;
    long timePartitipation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_score);
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

        NextBtn = findViewById(R.id.NextBtn);
        questionsInsert = findViewById(R.id.questionsInsert);

        marksTestsParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId()).child(Global.currentUser.getId());

        try{
            marksTestsParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests").child(Global.test.getId()).child(Global.currentUser.getId()).child("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                timePartitipation = (long)dataSnapshot.getValue();

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

                        View mView = overalScore();
                        questionsInsert.addView(mView);

                        NextBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                finish();
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


    public View overalScore(){

        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        final View score = factory.inflate(R.layout.checking_score, null);
        final TextView name = score.findViewById(R.id.nameS);
        final TextView email = score.findViewById(R.id.emailS);
        final TextView time = score.findViewById(R.id.timeS);
        final TextView mark = score.findViewById(R.id.mark);
        final TextView maxMark = score.findViewById(R.id.maxMark);
        final LinearLayout insertPoint = score.findViewById(R.id.insertPoint);

        name.setText(Global.currentUser.getFullname());
        email.setText(Global.currentUser.getEmail());
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

            LayoutInflater factory2 = LayoutInflater.from(getBaseContext());
            final View score2 = factory.inflate(R.layout.single_overal_check, null);
            final TextView question = score2.findViewById(R.id.question);
            final TextView markTextView = score2.findViewById(R.id.mark);


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
