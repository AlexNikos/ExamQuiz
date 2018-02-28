package com.example.alnik.examquiz.Course;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.Teacher.TeacherCourseFragment;
import com.example.alnik.examquiz.models.Course;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.TrueFalse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatabaseCourseFragment extends Fragment {

    private FloatingActionMenu fabMenu;
    private FloatingActionButton mFabMultiple;
    private FloatingActionButton mFabTrueFalse;
    private FloatingActionButton mFabShortAnswer;

    private RecyclerView MultipleChoiceList;
    private RecyclerView TrueFalseList;
    private RecyclerView ShortAnswerList;

    private DatabaseReference multipleRef;
    private DatabaseReference trueFalseRef;
    private DatabaseReference shortAnswerRef;
    private FirebaseUser currentUser;


    private View mDatabaseView;


    public DatabaseCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabaseView = inflater.inflate(R.layout.fragment_database_course, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        multipleRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child(CourseActivity.courseName).child("MultipleChoice");
        trueFalseRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child(CourseActivity.courseName).child("TrueFalse");
        shortAnswerRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child(CourseActivity.courseName).child("ShortAnswer");


        MultipleChoiceList = mDatabaseView.findViewById(R.id.multiple_recycleView);
        MultipleChoiceList.setHasFixedSize(true);
        MultipleChoiceList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        MultipleChoiceList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fabMenu.close(true);
            }
        });


        TrueFalseList = mDatabaseView.findViewById(R.id.trueFalse_recycleView);
        TrueFalseList.setHasFixedSize(true);
        TrueFalseList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        TrueFalseList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fabMenu.close(true);
            }
        });

        ShortAnswerList = mDatabaseView.findViewById(R.id.shortAnswer_recyclerView);
        ShortAnswerList.setHasFixedSize(true);
        ShortAnswerList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ShortAnswerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fabMenu.close(true);
            }
        });

        //----------------------Floating Action Menu And Buttons------------------------------------
        fabMenu = mDatabaseView.findViewById(R.id.fabMenu);
        mFabMultiple = (FloatingActionButton) mDatabaseView.findViewById(R.id.multiple);
        mFabTrueFalse = (FloatingActionButton) mDatabaseView.findViewById(R.id.trueFalse);
        mFabShortAnswer = (FloatingActionButton) mDatabaseView.findViewById(R.id.shortAns);

        //----------------------------Inflater for multiple choice question-------------------------
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View createMultipleChoice = factory.inflate(R.layout.create_multiple_choice, null);
        final EditText questionMultipleEnter = createMultipleChoice.findViewById(R.id.questionMultipleEnter);
        final EditText optionA = createMultipleChoice.findViewById(R.id.optionA);
        final EditText optionB = createMultipleChoice.findViewById(R.id.optionB);
        final EditText optionC = createMultipleChoice.findViewById(R.id.optionC);
        final EditText optionD = createMultipleChoice.findViewById(R.id.optionD);
        final RadioButton RadioOptionA = createMultipleChoice.findViewById(R.id.radioOptionA);
        final RadioButton RadioOptionB = createMultipleChoice.findViewById(R.id.radioOptionB);
        final RadioButton RadioOptionC = createMultipleChoice.findViewById(R.id.radioOptionC);
        final RadioButton RadioOptionD = createMultipleChoice.findViewById(R.id.radioOptionD);


        mFabMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);

                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createMultipleChoice)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String questionId = multipleRef.push().getKey().toString();
                                        String questionInput = questionMultipleEnter.getText().toString();
                                        String optionAinpute = optionA.getText().toString();
                                        String optionBinpute = optionB.getText().toString();
                                        String optionCinpute = optionC.getText().toString();
                                        String optionDinpute = optionD.getText().toString();
                                        String answer = "";
                                        if(RadioOptionA.isChecked()){
                                            answer = optionAinpute;
                                        }else if(RadioOptionB.isChecked()){
                                            answer = optionBinpute;
                                        }else if(RadioOptionC.isChecked()){
                                            answer = optionCinpute;
                                        }else if(RadioOptionD.isChecked()){
                                            answer = optionDinpute;
                                        }

                                        if(questionInput.isEmpty() || optionAinpute.isEmpty() || optionBinpute.isEmpty() || optionCinpute.isEmpty() || optionDinpute.isEmpty() || answer.isEmpty()
                                                || (!RadioOptionA.isChecked() && !RadioOptionB.isChecked() && !RadioOptionC.isChecked() && !RadioOptionD.isChecked())){
                                            Toast.makeText(getContext(), "No Empty Fields Allowed!", Toast.LENGTH_LONG).show();

                                        } else{
                                            MultipleChoice newQuestion = new MultipleChoice(questionInput, optionAinpute, optionBinpute, optionCinpute, optionDinpute, answer, questionId );

                                            multipleRef.child(questionId).setValue(newQuestion, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Toast.makeText(getContext(), "Question Added to Your Database.", Toast.LENGTH_LONG).show();
                                                    questionMultipleEnter.setText("");
                                                    optionA.setText("");
                                                    optionB.setText("");
                                                    optionC.setText("");
                                                    optionD.setText("");
                                                    RadioOptionA.setChecked(false);
                                                    RadioOptionB.setChecked(false);
                                                    RadioOptionC.setChecked(false);
                                                    RadioOptionD.setChecked(false);

                                                }
                                            });
                                        }

                                        ((ViewGroup) createMultipleChoice.getParent()).removeView(createMultipleChoice);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                questionMultipleEnter.setText("");
                                optionA.setText("");
                                optionB.setText("");
                                optionC.setText("");
                                optionD.setText("");
                                RadioOptionA.setChecked(false);
                                RadioOptionB.setChecked(false);
                                RadioOptionC.setChecked(false);
                                RadioOptionD.setChecked(false);

                                ((ViewGroup) createMultipleChoice.getParent()).removeView(createMultipleChoice);

                            }
                        })
                        .show();
                alert.setCanceledOnTouchOutside(false);

            }
        });

        //----------------------------Inflater for true false question-------------------------
        final View createTrueFalseQuestion = factory.inflate(R.layout.create_true_false, null);
        final EditText questionTrueFalseEnter = createTrueFalseQuestion.findViewById(R.id.questionTrueFalseEnter);
        final RadioButton RadioOptionTrue = createTrueFalseQuestion.findViewById(R.id.radioOptionTrue);
        final RadioButton RadioOptionFalse = createTrueFalseQuestion.findViewById(R.id.radioOptionFalse);

        mFabTrueFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabMenu.close(true);

                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createTrueFalseQuestion)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String questionId = trueFalseRef.push().getKey().toString();
                                        String questionInput = questionTrueFalseEnter.getText().toString();
                                        boolean answer = false;
                                        if(RadioOptionTrue.isChecked()){
                                            answer = true;
                                        }else if(RadioOptionFalse.isChecked()){
                                            answer = false;
                                        }

                                        if(questionInput.isEmpty() || (!RadioOptionTrue.isChecked() && !RadioOptionFalse.isChecked())){
                                            Toast.makeText(getContext(), "No Empty Fields Allowed!", Toast.LENGTH_LONG).show();

                                        } else{
                                            TrueFalse newQuestion = new TrueFalse(questionInput, answer, questionId);

                                            trueFalseRef.child(questionId).setValue(newQuestion, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Toast.makeText(getContext(), "Question Added to Your Database.", Toast.LENGTH_LONG).show();
                                                    questionTrueFalseEnter.setText("");
                                                    RadioOptionTrue.setChecked(false);
                                                    RadioOptionFalse.setChecked(false);

                                                }
                                            });
                                        }



                                        ((ViewGroup) createTrueFalseQuestion.getParent()).removeView(createTrueFalseQuestion);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                questionTrueFalseEnter.setText("");
                                RadioOptionTrue.setChecked(false);
                                RadioOptionFalse.setChecked(false);
                                ((ViewGroup) createTrueFalseQuestion.getParent()).removeView(createTrueFalseQuestion);

                            }
                        })
                        .show();
                alert.setCanceledOnTouchOutside(false);

            }
        });

        //----------------------------Inflater for short answer question-------------------------
        final View createShortAnswerQuestion = factory.inflate(R.layout.create_short_answer, null);
        final EditText questionShortAnswerEnter = createShortAnswerQuestion.findViewById(R.id.questionShortAnswerEnter);

        mFabShortAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabMenu.close(true);

               AlertDialog alert =  new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createShortAnswerQuestion)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String questionId = shortAnswerRef.push().getKey().toString();
                                        String questionInput = questionShortAnswerEnter.getText().toString();

                                        if(questionInput.isEmpty()){
                                            Toast.makeText(getContext(), "No Empty Fields Allowed!", Toast.LENGTH_LONG).show();

                                        } else{
                                            ShortAnswer newQuestion = new ShortAnswer(questionInput, questionId);

                                            shortAnswerRef.child(questionId).setValue(newQuestion, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Toast.makeText(getContext(), "Question Added to Your Database.", Toast.LENGTH_LONG).show();
                                                    questionShortAnswerEnter.setText("");

                                                }
                                            });
                                        }

                                        ((ViewGroup) createShortAnswerQuestion.getParent()).removeView(createShortAnswerQuestion);
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                questionShortAnswerEnter.setText("");
                                ((ViewGroup) createShortAnswerQuestion.getParent()).removeView(createShortAnswerQuestion);

                            }
                        })
                        .show();
                alert.setCanceledOnTouchOutside(false);

            }
        });

        return  mDatabaseView;

    }

    @Override
    public void onStart() {
        super.onStart();

        //-------------------------------Multiple Choice Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<MultipleChoice, multipleChoiceViewHolder> multipleChoicefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MultipleChoice, multipleChoiceViewHolder>(
                MultipleChoice.class,
                R.layout.course_single_multiple_choice,
                multipleChoiceViewHolder.class,
                multipleRef
        ) {

            @Override
            protected void populateViewHolder(final multipleChoiceViewHolder viewHolder, MultipleChoice model, int position) {
                viewHolder.setQuestion(model.getQuestion());
                viewHolder.setOptionA(model.getOptionA());
                viewHolder.setOptionB(model.getOptionB());
                viewHolder.setOptionC(model.getOptionC());
                viewHolder.setOptionD(model.getOptionD());


                //final DatabaseReference courseRef= getRef(position);
                //final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMenu.close(true);

                        Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();
//                        Intent startCourseActivity = new Intent(getActivity(), CourseActivity.class);
//                        startCourseActivity.putExtra("courseName", postKey);
//                        startActivity(startCourseActivity);


                    }
                });

            }

        };

        MultipleChoiceList.setAdapter(multipleChoicefirebaseRecyclerAdapter);

        //-------------------------------True False Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<TrueFalse, trueFalseViewHolder> trueFalsefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TrueFalse, trueFalseViewHolder>(
                TrueFalse.class,
                R.layout.course_single_truefalse,
                trueFalseViewHolder.class,
                trueFalseRef
        ) {

            @Override
            protected void populateViewHolder(final trueFalseViewHolder viewHolder, TrueFalse model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                //final DatabaseReference courseRef= getRef(position);
                //final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMenu.close(true);
                        Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();
//                        Intent startCourseActivity = new Intent(getActivity(), CourseActivity.class);
//                        startCourseActivity.putExtra("courseName", postKey);
//                        startActivity(startCourseActivity);


                    }
                });

            }

        };

        TrueFalseList.setAdapter(trueFalsefirebaseRecyclerAdapter);

        //-------------------------------Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<ShortAnswer, shortAnswerViewHolder> shortAnswerfirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ShortAnswer, shortAnswerViewHolder>(
                ShortAnswer.class,
                R.layout.course_single_shortanswer,
                shortAnswerViewHolder.class,
                shortAnswerRef
        ) {

            @Override
            protected void populateViewHolder(final shortAnswerViewHolder viewHolder, ShortAnswer model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                //final DatabaseReference courseRef= getRef(position);
                //final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMenu.close(true);
                        Toast.makeText(getContext(), "first button clicked", Toast.LENGTH_LONG).show();
//                        Intent startCourseActivity = new Intent(getActivity(), CourseActivity.class);
//                        startCourseActivity.putExtra("courseName", postKey);
//                        startActivity(startCourseActivity);

                    }
                });

            }

        };

        ShortAnswerList.setAdapter(shortAnswerfirebaseRecyclerAdapter);

    }

    public static class multipleChoiceViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;

        View view;

        public multipleChoiceViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.single_multiple_question);
            optionA = itemView.findViewById(R.id.single_optionA);
            optionB = itemView.findViewById(R.id.single_optionB);
            optionC = itemView.findViewById(R.id.single_optionC);
            optionD = itemView.findViewById(R.id.single_optionD);

            view = itemView.findViewById(R.id.single_multiple);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }

        public void setOptionA(String option){

            optionA.setText(option);
        }

        public void setOptionB(String option){

            optionB.setText(option);
        }

        public void setOptionC(String option){

            optionC.setText(option);
        }

        public void setOptionD(String option){

            optionD.setText(option);
        }


    }

    public static class trueFalseViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        TextView optionTrue;
        TextView optionFalse;


        View view;

        public trueFalseViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.single_trueFalse_question);
            optionTrue = itemView.findViewById(R.id.single_optionTrue);
            optionFalse = itemView.findViewById(R.id.single_optionFalse);


            view = itemView.findViewById(R.id.single_trueFalse);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }

    }

    public static class shortAnswerViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;


        View view;

        public shortAnswerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            question = itemView.findViewById(R.id.single_shortanswer_question);

            view = itemView.findViewById(R.id.single_shortAnswer);

        }

        public void setQuestion(String mQuestion) {

            question.setText(mQuestion);
        }

    }



}
