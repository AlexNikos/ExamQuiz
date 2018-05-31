package com.example.alnik.examquiz.Anonymous;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.CustomDateTimePicker;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.Test;
import com.example.alnik.examquiz.models.TrueFalse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewQuestionerActivity extends AppCompatActivity {

    private CustomDateTimePicker custom;
    private long startDate;
    private long endDate;

    private FirebaseDatabase database;
    private DatabaseReference testRef;
    private DatabaseReference multipleRef;
    private DatabaseReference trueFalseRef;
    private DatabaseReference shortAnswerRef;
    private FirebaseUser currentUser;

    private EditText titleBox;
    private Button startTimeButton;
    private Button endTimeButon;
    private Button done;
    private Button cancelTest;
    private ExpandableRelativeLayout expandable_layout_MultiChoise;
    private ExpandableRelativeLayout expandable_layout_TrueFalse;
    private ExpandableRelativeLayout expandable_layout_ShortAnswer;

    private LinearLayout insertPoint;

    private RecyclerView pickMultipleChoiceRecycleView;
    private RecyclerView pickTrueFalseRecycleView;
    private RecyclerView pickShortAnswerRecycleView;

    ArrayList<Object> mQuestionArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        testRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questioners").child(Global.room.getId());
        multipleRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("MultipleChoice");
        trueFalseRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("TrueFalse");
        shortAnswerRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("ShortAnswer");

        try{
            testRef.keepSynced(true);
            multipleRef.keepSynced(true);
            trueFalseRef.keepSynced(true);
            shortAnswerRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        startTimeButton = findViewById(R.id.startButton);
        endTimeButon = findViewById(R.id.endButton);
        insertPoint = findViewById(R.id.insertPoint);
        done = findViewById(R.id.done);
        cancelTest = findViewById(R.id.cancelTest);
        titleBox = findViewById(R.id.titleTest);

        cancelTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = titleBox.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input a Title!", Toast.LENGTH_LONG).show();
                } else {


                    if (Long.valueOf(startDate) == 0 || Long.valueOf(endDate) == 0) {

                        Toast.makeText(getApplicationContext(), "Start Date and End Date should not be empty!", Toast.LENGTH_LONG).show();

                    } else if(mQuestionArray.isEmpty()){

                        Toast.makeText(NewQuestionerActivity.this, "Please add at least one question!", Toast.LENGTH_LONG).show();

                    } else {

                        String id = testRef.push().getKey();
                        Test newTest = new Test(id, title, Global.room.getId(), startDate, endDate, mQuestionArray/*, maxMarks*/);
                        testRef.child(id).setValue(newTest, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {

                                    Toast.makeText(NewQuestionerActivity.this, "Error,could not be saved.", Toast.LENGTH_LONG).show();

                                } else {

                                    Toast.makeText(NewQuestionerActivity.this, "Successfully created.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            }
        });

        pickMultipleChoiceRecycleView = findViewById(R.id.pickMultiChoiceRecycleView);
        pickMultipleChoiceRecycleView.setHasFixedSize(true);
        pickMultipleChoiceRecycleView.setLayoutManager(new LinearLayoutManager(this));

        pickTrueFalseRecycleView = findViewById(R.id.pickTrueFalseRecycleView);
        pickTrueFalseRecycleView.setHasFixedSize(true);
        pickTrueFalseRecycleView.setLayoutManager(new LinearLayoutManager(this));

        pickShortAnswerRecycleView = findViewById(R.id.pickShortAnswerRecycleView);
        pickShortAnswerRecycleView.setHasFixedSize(true);
        pickShortAnswerRecycleView.setLayoutManager(new LinearLayoutManager(this));

        expandable_layout_MultiChoise = findViewById(R.id.expandableLayoutMultiChoice);
        expandable_layout_TrueFalse = findViewById(R.id.expandableLayoutTrueFalse);
        expandable_layout_ShortAnswer = findViewById(R.id.expandableLayoutShortAnswer);

        Button expandMultiplebutton = findViewById(R.id.expandMultiplebutton);
        Button expandTrueFalseButton = findViewById(R.id.expandTrueFalseButton);
        Button expandShortAnswerButton = findViewById(R.id.expandShortAnswerButton);

        expandMultiplebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(expandable_layout_TrueFalse.isExpanded()){
                    expandable_layout_TrueFalse.collapse();
                    expandTrueFalseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if(expandable_layout_ShortAnswer.isExpanded()){
                    expandable_layout_ShortAnswer.collapse();
                    expandShortAnswerButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if (expandable_layout_MultiChoise.isExpanded()) {
                    expandable_layout_MultiChoise.collapse();
                    expandMultiplebutton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);


                } else if (!expandable_layout_MultiChoise.isExpanded()) {
                    expandable_layout_MultiChoise.expand();
                    expandMultiplebutton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp) , null);


                }
            }
        });

        expandTrueFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(expandable_layout_MultiChoise.isExpanded()){
                    expandable_layout_MultiChoise.collapse();
                    expandMultiplebutton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if(expandable_layout_ShortAnswer.isExpanded()){
                    expandable_layout_ShortAnswer.collapse();
                    expandShortAnswerButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if (expandable_layout_TrueFalse.isExpanded()) {
                    expandable_layout_TrueFalse.collapse();
                    expandTrueFalseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                } else if (!expandable_layout_TrueFalse.isExpanded()) {
                    expandable_layout_TrueFalse.expand();
                    expandTrueFalseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp) , null);

                }
            }
        });

        expandShortAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(expandable_layout_MultiChoise.isExpanded()){
                    expandable_layout_MultiChoise.collapse();
                    expandMultiplebutton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if(expandable_layout_TrueFalse.isExpanded()){
                    expandable_layout_TrueFalse.collapse();
                    expandTrueFalseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);

                }

                if (expandable_layout_ShortAnswer.isExpanded()) {
                    expandable_layout_ShortAnswer.collapse();
                    expandShortAnswerButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp) , null);


                } else if (!expandable_layout_ShortAnswer.isExpanded()) {
                    expandable_layout_ShortAnswer.expand();
                    expandShortAnswerButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp) , null);


                }
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custom = new CustomDateTimePicker(NewQuestionerActivity.this,
                        new CustomDateTimePicker.ICustomDateTimeListener() {

                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                              Date dateSelected, int year, String monthFullName,
                                              String monthShortName, int monthNumber, int date,
                                              String weekDayFullName, String weekDayShortName,
                                              int hour24, int hour12, int min, int sec,
                                              String AM_PM) {

                                startDate = dateSelected.getTime();
                                startTimeButton.setText(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(startDate));
                                Log.d("test", String.valueOf(startDate));

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                custom.set24HourFormat(true);
                custom.setDate(Calendar.getInstance());
                custom.showDialog();

            }
        });

        endTimeButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custom = new CustomDateTimePicker(NewQuestionerActivity.this,
                        new CustomDateTimePicker.ICustomDateTimeListener() {

                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                              Date dateSelected, int year, String monthFullName,
                                              String monthShortName, int monthNumber, int date,
                                              String weekDayFullName, String weekDayShortName,
                                              int hour24, int hour12, int min, int sec,
                                              String AM_PM) {

                                endDate = dateSelected.getTime();
                                endTimeButon.setText(new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(endDate));

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                custom.set24HourFormat(true);
                custom.setDate(Calendar.getInstance());
                custom.showDialog();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<MultipleChoice, CheckViewHolder> multipleChoicefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MultipleChoice, CheckViewHolder>(
                MultipleChoice.class,
                R.layout.single_multi_with_checkbox,
                CheckViewHolder.class,
                multipleRef
        ) {

            @Override
            protected void populateViewHolder(final CheckViewHolder viewHolder, MultipleChoice model, int position) {
                viewHolder.setQuestion(model.getQuestion());
                viewHolder.setOptionA(model.getOptionA());
                viewHolder.setOptionB(model.getOptionB());
                viewHolder.setOptionC(model.getOptionC());
                viewHolder.setOptionD(model.getOptionD());
                final DatabaseReference questionRef = getRef(position);

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), "first button clicked", Toast.LENGTH_LONG).show();
                    }
                });
//----------------------------------------------- Check Listener----------------------------------------------------
                viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            Toast.makeText(getApplicationContext(), "cheched", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final MultipleChoice question = dataSnapshot.getValue(MultipleChoice.class);
                                    Log.d("test", question.getId());
                                    mQuestionArray.add(question);

                                    View multipleView =  insertMultipleChoice(question);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(5, 10, 5, 10);
                                    insertPoint.addView(multipleView,layoutParams);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else if (b == false) {
                            Toast.makeText(getApplicationContext(), "NOT checked", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String id = (String) dataSnapshot.child("id").getValue();
                                    final String question = (String) dataSnapshot.child("question").getValue();

                                    for(Object w : mQuestionArray){
                                        if(w.getClass() == MultipleChoice.class){
                                            if (((MultipleChoice)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }

                                        } else if(w.getClass() == TrueFalse.class){
                                            if (((TrueFalse)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        } else if(w.getClass() == ShortAnswer.class){
                                            if (((ShortAnswer)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        }
                                    }

                                    for(int index=0; index<insertPoint.getChildCount(); ++index) {
                                        View nextChild = insertPoint.getChildAt(index);
                                        if(nextChild.getTag().toString().equals(id)){
                                            ((LinearLayout)nextChild.getParent()).removeView(nextChild);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }

        };

        pickMultipleChoiceRecycleView.setAdapter(multipleChoicefirebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<TrueFalse, TrueFalseCheckViewHolder> trueFalsefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TrueFalse, TrueFalseCheckViewHolder>(
                TrueFalse.class,
                R.layout.single_true_false_checkbox,
                TrueFalseCheckViewHolder.class,
                trueFalseRef
        ) {

            @Override
            protected void populateViewHolder(final TrueFalseCheckViewHolder viewHolder, TrueFalse model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                final DatabaseReference questionRef = getRef(position);


//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), "first button clicked", Toast.LENGTH_LONG).show();


                    }
                });
//----------------------------------------------- Check Listener----------------------------------------------------
                viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            Toast.makeText(getApplicationContext(), "cheched", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final TrueFalse question = dataSnapshot.getValue(TrueFalse.class);
                                    Log.d("test", question.getId());
                                    mQuestionArray.add(question);
                                    Log.d("test", "Multi ArrayList is:");
                                    for (/*MultipleChoice w : multipleChoiceArray*/Object w : mQuestionArray) {

                                        if(w.getClass() == MultipleChoice.class){
                                            Log.d("test", ((MultipleChoice)w).getQuestion());
                                            Log.d("test", ((MultipleChoice)w).getId());
                                            Log.d("test", ((MultipleChoice)w).getType());


                                        } else if(w.getClass() == TrueFalse.class){
                                            Log.d("test", ((TrueFalse)w).getQuestion());
                                            Log.d("test", ((TrueFalse)w).getId());
                                            Log.d("test", ((TrueFalse)w).getType());

                                        }else if(w.getClass() == ShortAnswer.class){
                                            Log.d("test", ((ShortAnswer)w).getQuestion());
                                            Log.d("test", ((ShortAnswer)w).getId());
                                            Log.d("test", ((ShortAnswer)w).getType());

                                        }
                                    }

                                    View trueFalseView =  insertTrueFalse(question);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(0, 10, 0, 20);
                                    insertPoint.addView(trueFalseView,layoutParams);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else if (b == false) {
                            Toast.makeText(getApplicationContext(), "NOT checked", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String id = (String) dataSnapshot.child("id").getValue();
                                    final String question = (String) dataSnapshot.child("question").getValue();

                                    for(Object w : mQuestionArray){
                                        if(w.getClass() == MultipleChoice.class){
                                            if (((MultipleChoice)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }

                                        } else if(w.getClass() == TrueFalse.class){
                                            if (((TrueFalse)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        } else if(w.getClass() == ShortAnswer.class){
                                            if (((ShortAnswer)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        }
                                    }

                                    for(int index=0; index < insertPoint.getChildCount(); ++index) {
                                        View nextChild = insertPoint.getChildAt(index);
                                        if(nextChild.getTag().toString().equals(id)){
                                            ((LinearLayout)nextChild.getParent()).removeView(nextChild);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }

        };

        pickTrueFalseRecycleView.setAdapter(trueFalsefirebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<ShortAnswer, ShortAnswerCheckViewHolder> shortAnswerfirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ShortAnswer, ShortAnswerCheckViewHolder>(
                ShortAnswer.class,
                R.layout.single_short_answer_checkbox,
                ShortAnswerCheckViewHolder.class,
                shortAnswerRef
        ) {

            @Override
            protected void populateViewHolder(final ShortAnswerCheckViewHolder viewHolder, ShortAnswer model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                final DatabaseReference questionRef = getRef(position);


//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), "first button clicked", Toast.LENGTH_LONG).show();


                    }
                });
//----------------------------------------------- Check Listener----------------------------------------------------
                viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            Toast.makeText(getApplicationContext(), "cheched", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final ShortAnswer question = dataSnapshot.getValue(ShortAnswer.class);
                                    Log.d("test", question.getId());

                                    mQuestionArray.add(question);
                                    Log.d("test", "Multi ArrayList is:");
                                    for (/*MultipleChoice w : multipleChoiceArray*/Object w : mQuestionArray) {

                                        if(w.getClass() == MultipleChoice.class){
                                            Log.d("test", ((MultipleChoice)w).getQuestion());
                                            Log.d("test", ((MultipleChoice)w).getId());
                                            Log.d("test", ((MultipleChoice)w).getType());


                                        } else if(w.getClass() == TrueFalse.class){
                                            Log.d("test", ((TrueFalse)w).getQuestion());
                                            Log.d("test", ((TrueFalse)w).getId());
                                            Log.d("test", ((TrueFalse)w).getType());

                                        } else if(w.getClass() == ShortAnswer.class){
                                            Log.d("test", ((ShortAnswer)w).getQuestion());
                                            Log.d("test", ((ShortAnswer)w).getId());
                                            Log.d("test", ((ShortAnswer)w).getType());

                                        }
                                    }

                                    View shortAnswerView =  insertShortAnswer(question);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(0, 10, 0, 20);
                                    insertPoint.addView(shortAnswerView,layoutParams);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else if (b == false) {
                            Toast.makeText(getApplicationContext(), "NOT checked", Toast.LENGTH_LONG).show();

                            questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String id = (String) dataSnapshot.child("id").getValue();
                                    final String question = (String) dataSnapshot.child("question").getValue();

                                    for(Object w : mQuestionArray){
                                        if(w.getClass() == MultipleChoice.class){
                                            if (((MultipleChoice)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }

                                        } else if(w.getClass() == TrueFalse.class){
                                            if (((TrueFalse)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        } else if(w.getClass() == ShortAnswer.class){
                                            if (((ShortAnswer)w).getId().equals(id)){
                                                mQuestionArray.remove(w);
                                                break;
                                            }
                                        }
                                    }

                                    for(int index=0; index<insertPoint.getChildCount(); ++index) {
                                        View nextChild = insertPoint.getChildAt(index);
                                        if(nextChild.getTag().toString().equals(id)){
                                            ((LinearLayout)nextChild.getParent()).removeView(nextChild);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
            }

        };

        pickShortAnswerRecycleView.setAdapter(shortAnswerfirebaseRecyclerAdapter);

    }

    public static class CheckViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;
        CheckBox check;

        View view;

        public CheckViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.question);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);
            check = itemView.findViewById(R.id.checkBox);

            view = itemView.findViewById(R.id.singleCheck);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }

        public void setOptionA(String option) {

            optionA.setText(option);
        }

        public void setOptionB(String option) {

            optionB.setText(option);
        }

        public void setOptionC(String option) {

            optionC.setText(option);
        }

        public void setOptionD(String option) {

            optionD.setText(option);
        }
    }

    public static class TrueFalseCheckViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        CheckBox check;

        View view;

        public TrueFalseCheckViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.singleTrueFalseQuestion);
            check = itemView.findViewById(R.id.checkBox1);

            view = itemView.findViewById(R.id.singleTrueFalseCheck);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }
    }

    public static class ShortAnswerCheckViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        CheckBox check;

        View view;

        public ShortAnswerCheckViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.singleShortAnswerQuestion);
            check = itemView.findViewById(R.id.singleShortAnswercheckBox);

            view = itemView.findViewById(R.id.singleShortAnswerCheck);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }
    }

    public View insertMultipleChoice( MultipleChoice question ){
        Log.d("test", question.getQuestion());

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addView = layoutInflater.inflate(R.layout.single_inserted_multi_test, null);
        addView.setTag(question.getId());
        TextView questionText = (TextView)addView.findViewById(R.id.question);
        TextView optionA = (TextView)addView.findViewById(R.id.optionA);
        TextView optionB = (TextView)addView.findViewById(R.id.optionB);
        TextView optionC = (TextView)addView.findViewById(R.id.optionC);
        TextView optionD = (TextView)addView.findViewById(R.id.optionD);
        LinearLayout scoreLiner = addView.findViewById(R.id.ScoreLiner);
        scoreLiner.setVisibility(View.GONE);
        questionText.setText(question.getQuestion());
        optionA.setText(question.getOptionA());
        optionB.setText(question.getOptionB());
        optionC.setText(question.getOptionC());
        optionD.setText(question.getOptionD());

        return addView;

    }

    public View insertTrueFalse(TrueFalse question){

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addView = layoutInflater.inflate(R.layout.single_inserted_truefalse_test, null);
        addView.setTag(question.getId());
        TextView questionText = (TextView)addView.findViewById(R.id.singleTrueFalseQuestion);
        questionText.setText(question.getQuestion());
        LinearLayout scoreLiner = addView.findViewById(R.id.ScoreLiner);
        scoreLiner.setVisibility(View.GONE);

        return addView;

    }

    public View insertShortAnswer(ShortAnswer question){

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addView = layoutInflater.inflate(R.layout.single_inserted_shortanswer_test, null);
        addView.setTag(question.getId());
        TextView questionText = (TextView)addView.findViewById(R.id.singleShortAnswerQuestion);
        questionText.setText(question.getQuestion());
        LinearLayout scoreLiner = addView.findViewById(R.id.ScoreLiner);
        scoreLiner.setVisibility(View.GONE);

        return addView;

    }

}


