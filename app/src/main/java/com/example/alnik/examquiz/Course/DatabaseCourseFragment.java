package com.example.alnik.examquiz.Course;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.alnik.examquiz.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
        multipleRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child("MultipleChoice");
        trueFalseRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child("TrueFalse");
        shortAnswerRef = FirebaseDatabase.getInstance().getReference("Questions").child(currentUser.getUid()).child("ShortAnswer");


//        MultipleChoiceList = mDatabaseView.findViewById(R.id.teacher_lesson_recycleView);
//        MultipleChoiceList.setHasFixedSize(true);
//        MultipleChoiceList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
//
//        TrueFalseList = mDatabaseView.findViewById(R.id.teacher_lesson_recycleView);
//        TrueFalseList.setHasFixedSize(true);
//        TrueFalseList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
//
//        ShortAnswerList = mDatabaseView.findViewById(R.id.teacher_lesson_recycleView);
//        ShortAnswerList.setHasFixedSize(true);
//        ShortAnswerList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));

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

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createMultipleChoice)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {



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

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createTrueFalseQuestion)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {



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

            }
        });

        //----------------------------Inflater for short answer question-------------------------
        final View createShortAnswerQuestion = factory.inflate(R.layout.create_short_answer, null);
        final EditText questionShortAnswerEnter = createShortAnswerQuestion.findViewById(R.id.questionShortAnswerEnter);

        mFabShortAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabMenu.close(true);

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTitle("Create New Question")
                        .setView(createShortAnswerQuestion)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {



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

            }
        });

        return  mDatabaseView;

    }

}
