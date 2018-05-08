package com.example.alnik.examquiz.Anonymous;

        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.widget.GridLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.alnik.examquiz.Global;
        import com.example.alnik.examquiz.R;
        import com.example.alnik.examquiz.models.MultipleChoice;
        import com.example.alnik.examquiz.models.ShortAnswer;
        import com.example.alnik.examquiz.models.TrueFalse;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
        import com.github.clans.fab.FloatingActionButton;
        import com.github.clans.fab.FloatingActionMenu;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsFragment extends Fragment {

    private FloatingActionMenu fabMenu;
    private FloatingActionButton mFabMultiple;
    private FloatingActionButton mFabTrueFalse;
    private FloatingActionButton mFabShortAnswer;

    private Button mutipleChoiceButton;
    private Button trueFalseButton;
    private Button shortAnswerbutton;

    private ExpandableRelativeLayout expandable_layout_MultiChoise;
    private ExpandableRelativeLayout expandable_layout_TrueFalse;
    private ExpandableRelativeLayout expandable_layout_ShortAnswer;

    private RecyclerView MultipleChoiceList;
    private RecyclerView TrueFalseList;
    private RecyclerView ShortAnswerList;

    private DatabaseReference multipleRef;
    private DatabaseReference trueFalseRef;
    private DatabaseReference shortAnswerRef;
    private FirebaseUser currentUser;


    private View mDatabaseView;


    public QuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabaseView = inflater.inflate(R.layout.fragment_questions, container, false);


        mutipleChoiceButton = mDatabaseView.findViewById(R.id.expMultiButton);
        trueFalseButton = mDatabaseView.findViewById(R.id.expTrueFalseButton);
        shortAnswerbutton = mDatabaseView.findViewById(R.id.expShortAnswerButton);

        expandable_layout_MultiChoise = mDatabaseView.findViewById(R.id.multiExpand);
        expandable_layout_MultiChoise.collapse();
        expandable_layout_TrueFalse = mDatabaseView.findViewById(R.id.trueFalseExpand);
        expandable_layout_TrueFalse.collapse();
        expandable_layout_ShortAnswer = mDatabaseView.findViewById(R.id.shortAnswerExpand);
        expandable_layout_ShortAnswer.collapse();


        mutipleChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(expandable_layout_TrueFalse.isExpanded()){
//                    expandable_layout_TrueFalse.collapse();
//                }
//
//                if(expandable_layout_ShortAnswer.isExpanded()){
//                    expandable_layout_ShortAnswer.collapse();
//                }

                if (expandable_layout_MultiChoise.isExpanded()) {
                    expandable_layout_MultiChoise.collapse();
                    fabMenu.showMenu(true);


                } else if (!expandable_layout_MultiChoise.isExpanded()) {
                    fabMenu.hideMenu(true);
                    expandable_layout_MultiChoise.expand();

                }
            }
        });

        trueFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(expandable_layout_MultiChoise.isExpanded()){
//                    expandable_layout_MultiChoise.collapse();
//                }
//
//                if(expandable_layout_ShortAnswer.isExpanded()){
//                    expandable_layout_ShortAnswer.collapse();
//                }

                if (expandable_layout_TrueFalse.isExpanded()) {
                    expandable_layout_TrueFalse.collapse();
                    fabMenu.showMenu(true);


                } else if (!expandable_layout_TrueFalse.isExpanded()) {
                    fabMenu.hideMenu(true);
                    expandable_layout_TrueFalse.expand();

                }
            }
        });

        shortAnswerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(expandable_layout_MultiChoise.isExpanded()){
//                    expandable_layout_MultiChoise.collapse();
//                }
//
//                if(expandable_layout_TrueFalse.isExpanded()){
//                    expandable_layout_TrueFalse.collapse();
//                }

                if (expandable_layout_ShortAnswer.isExpanded()) {
                    expandable_layout_ShortAnswer.collapse();
                    fabMenu.showMenu(true);


                } else if (!expandable_layout_ShortAnswer.isExpanded()) {
                    fabMenu.hideMenu(true);
                    expandable_layout_ShortAnswer.expand();

                }
            }
        });


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        multipleRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("MultipleChoice");
        trueFalseRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("TrueFalse");
        shortAnswerRef = FirebaseDatabase.getInstance().getReference("Anonymous").child("Questions").child(Global.room.getId()).child("ShortAnswer");

        try{
            multipleRef.keepSynced(true);
            trueFalseRef.keepSynced(true);
            shortAnswerRef.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }


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
        final RadioGroup radioOption = createMultipleChoice.findViewById(R.id.radioOption);
        radioOption.setVisibility(View.GONE);
//        final RadioButton RadioOptionA = createMultipleChoice.findViewById(R.id.radioOptionA);
//        final RadioButton RadioOptionB = createMultipleChoice.findViewById(R.id.radioOptionB);
//        final RadioButton RadioOptionC = createMultipleChoice.findViewById(R.id.radioOptionC);
//        final RadioButton RadioOptionD = createMultipleChoice.findViewById(R.id.radioOptionD);


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
//                                        String answer = "";
//                                        if(RadioOptionA.isChecked()){
//                                            answer = optionAinpute;
//                                        }else if(RadioOptionB.isChecked()){
//                                            answer = optionBinpute;
//                                        }else if(RadioOptionC.isChecked()){
//                                            answer = optionCinpute;
//                                        }else if(RadioOptionD.isChecked()){
//                                            answer = optionDinpute;
//                                        }

                                        if(questionInput.isEmpty() || optionAinpute.isEmpty() || optionBinpute.isEmpty() || optionCinpute.isEmpty() || optionDinpute.isEmpty() /*|| answer.isEmpty()
                                                || (!RadioOptionA.isChecked() && !RadioOptionB.isChecked() && !RadioOptionC.isChecked() && !RadioOptionD.isChecked())*/){
                                            Toast.makeText(getContext(), "No Empty Fields Allowed!", Toast.LENGTH_LONG).show();

                                        } else{
                                            MultipleChoice newQuestion = new MultipleChoice(questionInput, optionAinpute, optionBinpute, optionCinpute, optionDinpute, questionId );

                                            multipleRef.child(questionId).setValue(newQuestion, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Toast.makeText(getContext(), "Question Added to Your Database.", Toast.LENGTH_LONG).show();
                                                    questionMultipleEnter.setText("");
                                                    optionA.setText("");
                                                    optionB.setText("");
                                                    optionC.setText("");
                                                    optionD.setText("");
//                                                    RadioOptionA.setChecked(false);
//                                                    RadioOptionB.setChecked(false);
//                                                    RadioOptionC.setChecked(false);
//                                                    RadioOptionD.setChecked(false);

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
//                                RadioOptionA.setChecked(false);
//                                RadioOptionB.setChecked(false);
//                                RadioOptionC.setChecked(false);
//                                RadioOptionD.setChecked(false);

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
        final RadioGroup radioOption2 = createTrueFalseQuestion.findViewById(R.id.radioOption);
        radioOption2.setVisibility(View.GONE);
//        final RadioButton RadioOptionTrue = createTrueFalseQuestion.findViewById(R.id.radioOptionTrue);
//        final RadioButton RadioOptionFalse = createTrueFalseQuestion.findViewById(R.id.radioOptionFalse);

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
//                                        boolean answer = false;
//                                        if(RadioOptionTrue.isChecked()){
//                                            answer = true;
//                                        }else if(RadioOptionFalse.isChecked()){
//                                            answer = false;
//                                        }

                                        if(questionInput.isEmpty() /*|| (!RadioOptionTrue.isChecked() && !RadioOptionFalse.isChecked())*/){
                                            Toast.makeText(getContext(), "No Empty Fields Allowed!", Toast.LENGTH_LONG).show();

                                        } else{
                                            TrueFalse newQuestion = new TrueFalse(questionInput, questionId);

                                            trueFalseRef.child(questionId).setValue(newQuestion, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    Toast.makeText(getContext(), "Question Added to Your Database.", Toast.LENGTH_LONG).show();
                                                    questionTrueFalseEnter.setText("");
//                                                    RadioOptionTrue.setChecked(false);
//                                                    RadioOptionFalse.setChecked(false);

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
//                                RadioOptionTrue.setChecked(false);
//                                RadioOptionFalse.setChecked(false);
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
        FirebaseRecyclerAdapter<MultipleChoice, MultipleChoiceViewHolder> multipleChoicefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MultipleChoice, MultipleChoiceViewHolder>(
                MultipleChoice.class,
                R.layout.course_single_multiple_choice,
                MultipleChoiceViewHolder.class,
                multipleRef
        ) {

            @Override
            protected void populateViewHolder(final MultipleChoiceViewHolder viewHolder, MultipleChoice model, int position) {
                viewHolder.setQuestion(model.getQuestion());
                viewHolder.setOptionA(model.getOptionA());
                viewHolder.setOptionB(model.getOptionB());
                viewHolder.setOptionC(model.getOptionC());
                viewHolder.setOptionD(model.getOptionD());


//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMenu.close(true);

                        LayoutInflater factory = LayoutInflater.from(getContext());
                        final View showMultipleChoice = factory.inflate(R.layout.create_multiple_choice, null);
                        final EditText questionMultipleEnter = showMultipleChoice.findViewById(R.id.questionMultipleEnter);
                        final EditText optionA = showMultipleChoice.findViewById(R.id.optionA);
                        final EditText optionB = showMultipleChoice.findViewById(R.id.optionB);
                        final EditText optionC = showMultipleChoice.findViewById(R.id.optionC);
                        final EditText optionD = showMultipleChoice.findViewById(R.id.optionD);
                        final RadioGroup radioOption = showMultipleChoice.findViewById(R.id.radioOption);
                        radioOption.setVisibility(View.GONE);
//                        final RadioButton RadioOptionA = showMultipleChoice.findViewById(R.id.radioOptionA);
//                        final RadioButton RadioOptionB = showMultipleChoice.findViewById(R.id.radioOptionB);
//                        final RadioButton RadioOptionC = showMultipleChoice.findViewById(R.id.radioOptionC);
//                        final RadioButton RadioOptionD = showMultipleChoice.findViewById(R.id.radioOptionD);

                        getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                MultipleChoice question = (MultipleChoice) dataSnapshot.getValue(MultipleChoice.class);
                                questionMultipleEnter.setText(question.getQuestion());
                                optionA.setText(question.getOptionA());
                                optionB.setText(question.getOptionB());
                                optionC.setText(question.getOptionC());
                                optionD.setText(question.getOptionD());

//                                if(question.getAnswer().equals(question.getOptionA())){
//                                    RadioOptionA.setChecked(true);
//
//                                } else if(question.getAnswer().equals(question.getOptionB())){
//                                    RadioOptionB.setChecked(true);
//
//                                } else if(question.getAnswer().equals(question.getOptionC())) {
//                                    RadioOptionC.setChecked(true);
//
//                                } else if(question.getAnswer().equals(question.getOptionD())) {
//                                    RadioOptionD.setChecked(true);
//
//                                }

                                questionMultipleEnter.setFocusableInTouchMode(false);
                                optionA.setFocusableInTouchMode(false);
                                optionB.setFocusableInTouchMode(false);
                                optionC.setFocusableInTouchMode(false);
                                optionD.setFocusableInTouchMode(false);
//                                RadioOptionA.setClickable(false);
//                                RadioOptionB.setClickable(false);
//                                RadioOptionC.setClickable(false);
//                                RadioOptionD.setClickable(false);


                                AlertDialog alert = new AlertDialog.Builder(getContext())
                                        .setView(showMultipleChoice)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                ((ViewGroup) showMultipleChoice.getParent()).removeView(showMultipleChoice);

                                            }
                                        })
                                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        ((ViewGroup) showMultipleChoice.getParent()).removeView(showMultipleChoice);
                                                        questionMultipleEnter.setFocusableInTouchMode(true);
                                                        optionA.setFocusableInTouchMode(true);
                                                        optionB.setFocusableInTouchMode(true);
                                                        optionC.setFocusableInTouchMode(true);
                                                        optionD.setFocusableInTouchMode(true);
//                                                        RadioOptionA.setClickable(true);
//                                                        RadioOptionB.setClickable(true);
//                                                        RadioOptionC.setClickable(true);
//                                                        RadioOptionD.setClickable(true);

                                                        AlertDialog alert = new AlertDialog.Builder(getContext())
                                                                .setView(showMultipleChoice)
                                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        ((ViewGroup) showMultipleChoice.getParent()).removeView(showMultipleChoice);

                                                                    }
                                                                })
                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                String q = questionMultipleEnter.getText().toString();
                                                                                String a = optionA.getText().toString();
                                                                                String b = optionB.getText().toString();
                                                                                String c = optionC.getText().toString();
                                                                                String d = optionD.getText().toString();
//                                                                                String answer;
//
//                                                                                if(RadioOptionA.isChecked()){
//                                                                                    answer = a;
//                                                                                    question.setAnswer(answer);
//
//                                                                                } else if(RadioOptionB.isChecked()){
//                                                                                    answer = b;
//                                                                                    question.setAnswer(answer);
//
//                                                                                } else if(RadioOptionC.isChecked()){
//                                                                                    answer = c;
//                                                                                    question.setAnswer(answer);
//
//                                                                                } else if(RadioOptionD.isChecked()){
//                                                                                    answer = d;
//                                                                                    question.setAnswer(answer);
//
//                                                                                }
                                                                                question.setQuestion(q);
                                                                                question.setOptionA(a);
                                                                                question.setOptionB(b);
                                                                                question.setOptionC(c);
                                                                                question.setOptionD(d);

                                                                                getRef(position).setValue(question, new DatabaseReference.CompletionListener() {
                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                        if(databaseError == null){
                                                                                            Toast.makeText(getContext(), "Question succesfully updated.", Toast.LENGTH_LONG).show();

                                                                                        } else {
                                                                                            Toast.makeText(getContext(), "Could not update question. Try again!", Toast.LENGTH_LONG).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }
                                                                        }
                                                                )
                                                                .show();
                                                        alert.setCanceledOnTouchOutside(false);
                                                    }
                                                }
                                        )
                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if(databaseError == null){
                                                            Toast.makeText(getContext(), "Question deleted", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showMultipleChoice.getParent()).removeView(showMultipleChoice);

                                                        } else {
                                                            Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showMultipleChoice.getParent()).removeView(showMultipleChoice);

                                                        }
                                                    }
                                                });

                                            }
                                        })
                                        .show();
                                alert.setCanceledOnTouchOutside(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }

        };

        MultipleChoiceList.setAdapter(multipleChoicefirebaseRecyclerAdapter);

        //-------------------------------True False Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<TrueFalse, TrueFalseViewHolder> trueFalsefirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TrueFalse, TrueFalseViewHolder>(
                TrueFalse.class,
                R.layout.course_single_truefalse,
                TrueFalseViewHolder.class,
                trueFalseRef
        ) {

            @Override
            protected void populateViewHolder(final TrueFalseViewHolder viewHolder, TrueFalse model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                //final DatabaseReference courseRef= getRef(position);
                //final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        fabMenu.close(true);

                        LayoutInflater factory = LayoutInflater.from(getContext());
                        final View showTrueFalseChoice = factory.inflate(R.layout.create_true_false, null);
                        final EditText questionTrueFalseEnter = showTrueFalseChoice.findViewById(R.id.questionTrueFalseEnter);
                        final RadioGroup radioOption = showTrueFalseChoice.findViewById(R.id.radioOption);
                        radioOption.setVisibility(View.GONE);
//                        final RadioButton RadioOptionTrue = showTrueFalseChoice.findViewById(R.id.radioOptionTrue);
//                        final RadioButton RadioOptionFalse = showTrueFalseChoice.findViewById(R.id.radioOptionFalse);

                        getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                TrueFalse question = (TrueFalse) dataSnapshot.getValue(TrueFalse.class);
                                questionTrueFalseEnter.setText(question.getQuestion());

//                                if(question.getAnswer().equals(true)){
//                                    RadioOptionTrue.setChecked(true);
//
//                                } else if(question.getAnswer().equals(false)){
//                                    RadioOptionFalse.setChecked(true);
//
//                                }

                                questionTrueFalseEnter.setFocusableInTouchMode(false);
//                                RadioOptionTrue.setClickable(false);
//                                RadioOptionFalse.setClickable(false);



                                AlertDialog alert = new AlertDialog.Builder(getContext())
                                        .setView(showTrueFalseChoice)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                ((ViewGroup) showTrueFalseChoice.getParent()).removeView(showTrueFalseChoice);

                                            }
                                        })
                                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        ((ViewGroup) showTrueFalseChoice.getParent()).removeView(showTrueFalseChoice);
                                                        questionTrueFalseEnter.setFocusableInTouchMode(true);
//                                                        RadioOptionTrue.setClickable(true);
//                                                        RadioOptionFalse.setClickable(true);

                                                        AlertDialog alert = new AlertDialog.Builder(getContext())
                                                                .setView(showTrueFalseChoice)
                                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        ((ViewGroup) showTrueFalseChoice.getParent()).removeView(showTrueFalseChoice);

                                                                    }
                                                                })
                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                String q = questionTrueFalseEnter.getText().toString();

//                                                                                if(RadioOptionTrue.isChecked()){
//
//                                                                                    question.setAnswer(true);
//
//                                                                                } else if(RadioOptionFalse.isChecked()){
//
//                                                                                    question.setAnswer(false);
//
//                                                                                }
                                                                                question.setQuestion(q);


                                                                                getRef(position).setValue(question, new DatabaseReference.CompletionListener() {
                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                        if(databaseError == null){
                                                                                            Toast.makeText(getContext(), "Question succesfully updated.", Toast.LENGTH_LONG).show();

                                                                                        } else {
                                                                                            Toast.makeText(getContext(), "Could not update question. Try again!", Toast.LENGTH_LONG).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }
                                                                        }
                                                                )
                                                                .show();
                                                        alert.setCanceledOnTouchOutside(false);
                                                    }
                                                }
                                        )
                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if(databaseError == null){
                                                            Toast.makeText(getContext(), "Question deleted", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showTrueFalseChoice.getParent()).removeView(showTrueFalseChoice);

                                                        } else {
                                                            Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showTrueFalseChoice.getParent()).removeView(showTrueFalseChoice);

                                                        }
                                                    }
                                                });

                                            }
                                        })
                                        .show();
                                alert.setCanceledOnTouchOutside(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }

        };
        TrueFalseList.setAdapter(trueFalsefirebaseRecyclerAdapter);

        //-------------------------------Firebase Adapter-------------------------------------
        FirebaseRecyclerAdapter<ShortAnswer, ShortAnswerViewHolder> shortAnswerfirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ShortAnswer, ShortAnswerViewHolder>(
                ShortAnswer.class,
                R.layout.course_single_shortanswer,
                ShortAnswerViewHolder.class,
                shortAnswerRef
        ) {

            @Override
            protected void populateViewHolder(final ShortAnswerViewHolder viewHolder, ShortAnswer model, int position) {
                viewHolder.setQuestion(model.getQuestion());

                //final DatabaseReference courseRef= getRef(position);
                //final String postKey = courseRef.getKey();

//---------------------------------action on click a Course----------------------------------------------------------
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        fabMenu.close(true);

                        LayoutInflater factory = LayoutInflater.from(getContext());
                        final View showShortAnswer = factory.inflate(R.layout.create_short_answer, null);
                        final EditText questionShortAnswerEnter = showShortAnswer.findViewById(R.id.questionShortAnswerEnter);


                        getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ShortAnswer question = (ShortAnswer) dataSnapshot.getValue(ShortAnswer.class);
                                questionShortAnswerEnter.setText(question.getQuestion());

                                questionShortAnswerEnter.setFocusableInTouchMode(false);

                                AlertDialog alert = new AlertDialog.Builder(getContext())
                                        .setView(showShortAnswer)
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                ((ViewGroup) showShortAnswer.getParent()).removeView(showShortAnswer);

                                            }
                                        })
                                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        ((ViewGroup) showShortAnswer.getParent()).removeView(showShortAnswer);
                                                        questionShortAnswerEnter.setFocusableInTouchMode(true);

                                                        AlertDialog alert = new AlertDialog.Builder(getContext())
                                                                .setView(showShortAnswer)
                                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                        ((ViewGroup) showShortAnswer.getParent()).removeView(showShortAnswer);

                                                                    }
                                                                })
                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                String q = questionShortAnswerEnter.getText().toString();

                                                                                question.setQuestion(q);

                                                                                getRef(position).setValue(question, new DatabaseReference.CompletionListener() {
                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                        if(databaseError == null){
                                                                                            Toast.makeText(getContext(), "Question succesfully updated.", Toast.LENGTH_LONG).show();

                                                                                        } else {
                                                                                            Toast.makeText(getContext(), "Could not update question. Try again!", Toast.LENGTH_LONG).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }
                                                                        }
                                                                )
                                                                .show();
                                                        alert.setCanceledOnTouchOutside(false);
                                                    }
                                                }
                                        )
                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if(databaseError == null){
                                                            Toast.makeText(getContext(), "Question deleted", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showShortAnswer.getParent()).removeView(showShortAnswer);

                                                        } else {
                                                            Toast.makeText(getContext(), "An error occurred, try again!", Toast.LENGTH_LONG).show();
                                                            ((ViewGroup) showShortAnswer.getParent()).removeView(showShortAnswer);

                                                        }
                                                    }
                                                });

                                            }
                                        })
                                        .show();
                                alert.setCanceledOnTouchOutside(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }

        };

        ShortAnswerList.setAdapter(shortAnswerfirebaseRecyclerAdapter);

    }

    public static class MultipleChoiceViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;

        View view;

        public MultipleChoiceViewHolder(View itemView) {
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

    public static class TrueFalseViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;
        TextView optionTrue;
        TextView optionFalse;


        View view;

        public TrueFalseViewHolder(View itemView) {
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

    public static class ShortAnswerViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView question;


        View view;

        public ShortAnswerViewHolder(View itemView) {
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
