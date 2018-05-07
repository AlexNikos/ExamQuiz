package com.example.alnik.examquiz.Anonymous;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.MyDemicalFormater;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.TrueFalse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsQuestionerStatisticsFragment extends Fragment {

    View mView;
    LinearLayout insert;

    DatabaseReference answersTestsParticipation;
    DatabaseReference usersTestsParticipation;
    ArrayList<Object> questions;


    public ResultsQuestionerStatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_results_statistics, container, false);

        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Anonymous").child("Answers").child("Questioners").child(Global.test.getId());
        usersTestsParticipation = FirebaseDatabase.getInstance().getReference("Anonymous").child("QuestionerParticipations").child("Questioners").child(Global.test.getId());

        try{
            answersTestsParticipation.keepSynced(true);
            usersTestsParticipation.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        insert = mView.findViewById(R.id.insert);

        loadQuestions();

        for(Object w : questions){
            if(w.getClass() == MultipleChoice.class){

                MultipleChoice question = (MultipleChoice)w;

                int index = questions.indexOf(w);

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                FirebaseDatabase.getInstance().getReference("Anonymous").child("Answers").child("Questioners").child(Global.test.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long opA = 0, opB = 0, opC= 0, opD = 0, opNone = 0;
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ArrayList<String> ans = ds.getValue(t);

                            if(ans.get(index).equals(question.getOptionA())){
                                opA++;
                            }else if(ans.get(index).equals(question.getOptionB())){
                                opB++;
                            }else if(ans.get(index).equals(question.getOptionB())){
                                opB++;
                            }else if(ans.get(index).equals(question.getOptionC())){
                                opC++;
                            }else if(ans.get(index).equals(question.getOptionD())){
                                opD++;
                            } else{
                                opNone++;
                            }

                        }

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 5, 0, 10);

                        insert.addView(drawBarMultiple(opA, opB, opC, opD, opNone,
                                question.getQuestion(), question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD()), layoutParams);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } else if(w.getClass() == TrueFalse.class){

                TrueFalse question = (TrueFalse) w;

                int index = questions.indexOf(w);

                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                FirebaseDatabase.getInstance().getReference("Anonymous").child("Answers").child("Questioners").child(Global.test.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long opTRUE = 0, opFALSE = 0, opNone = 0;
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ArrayList<String> ans = ds.getValue(t);

                            if(ans.get(index).equals("true")){
                                opTRUE++;
                            }else if(ans.get(index).equals("false")){
                                opFALSE++;
                            } else{
                                opNone++;
                            }

                        }

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 5, 0, 10);

                        insert.addView(drawBarTruefalse(opTRUE, opFALSE, opNone, question.getQuestion()), layoutParams);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }

        return mView;
    }

    public void loadQuestions(){

        questions = new ArrayList<>();

        for(Object w: Global.test.getQuestions()){

            String y = (String)((HashMap)w).get("type");

            if(y.equals("MultipleChoice")){

                MultipleChoice question = new MultipleChoice((String)((HashMap)w).get("question"), (String)((HashMap)w).get("optionA"), (String)((HashMap)w).get("optionB"),
                        (String)((HashMap)w).get("optionC"), (String)((HashMap)w).get("optionD"), (String)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));

                questions.add(question);

            }else if(y.equals("TrueFalse")){

                TrueFalse question = new TrueFalse((String)((HashMap)w).get("question"), (boolean)((HashMap)w).get("answer"), (String)((HashMap)w).get("id"));

                questions.add(question);


            }else if(y.equals("ShortAnswer")){

                ShortAnswer question = new ShortAnswer((String)((HashMap)w).get("question"), (String)((HashMap)w).get("id"));

                questions.add(question);

            }
        }

    }



    private View drawBarMultiple(long opA, long opB, long opC, long opD, long opNone,
                                String question ,String optionA, String optionB, String optionC, String optionD){

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View chartLayout = factory.inflate(R.layout.anonymous_bar_chart, null);
        final TextView questionView = chartLayout.findViewById(R.id.question);
        final BarChart barChart = chartLayout.findViewById(R.id.barChart);

        questionView.setText(question);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, opA));
        barEntries.add(new BarEntry(1, opB));
        barEntries.add(new BarEntry(2, opC));
        barEntries.add(new BarEntry(3, opD));
        barEntries.add(new BarEntry(4, opNone));


        ArrayList<String> names = new ArrayList<>();
        names.add(optionA);
        names.add(optionB);
        names.add(optionC);
        names.add(optionD);
        names.add("No Answer");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter((names.toArray(new String[names.size()]))));
        xAxis.setTextSize(10);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        int[] colors = new int[] {Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.LTGRAY};

        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        barDataSet.setValueTextSize(14);
        barDataSet.setBarBorderWidth(1);
        barDataSet.setColors(colors);
        barChart.getAxisLeft().setValueFormatter(new MyDemicalFormater());
        barChart.getAxisRight().setValueFormatter(new MyDemicalFormater());
        barDataSet.setValueFormatter(new MyDemicalFormater());

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);

        Description de = new Description();
        String total = String.valueOf((opA + opB + opC + opD + opNone));
        de.setText("Total Participants " +total);
        de.setTextSize(12);
        barChart.setDescription(de);
        barChart.setFitBars(true);
        barChart.invalidate();


        return chartLayout;

    }

    private View drawBarTruefalse(long opTrue, long opFalse,  long opNone, String question ){

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View chartLayout = factory.inflate(R.layout.anonymous_bar_chart, null);
        final TextView questionView = chartLayout.findViewById(R.id.question);
        final BarChart barChart = chartLayout.findViewById(R.id.barChart);

        questionView.setText(question);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, opTrue));
        barEntries.add(new BarEntry(1, opFalse));
        barEntries.add(new BarEntry(2, opNone));


        ArrayList<String> names = new ArrayList<>();
        names.add("TRUE");
        names.add("FALSE");
        names.add("No Answer");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter((names.toArray(new String[names.size()]))));
        xAxis.setTextSize(10);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        int[] colors = new int[] {Color.GREEN, Color.BLUE, Color.LTGRAY};

        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        barDataSet.setValueTextSize(14);
        barDataSet.setBarBorderWidth(1);
        barDataSet.setColors(colors);

        barChart.getAxisLeft().setValueFormatter(new MyDemicalFormater());
        barChart.getAxisRight().setValueFormatter(new MyDemicalFormater());

        barDataSet.setValueFormatter(new MyDemicalFormater());
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);

        Description de = new Description();
        String total = String.valueOf((opFalse + opTrue + opNone));
        de.setText("Total Participants " +total);
        de.setTextSize(12);
        barChart.setDescription(de);
        barChart.setFitBars(true);
        barChart.invalidate();


        return chartLayout;

    }


}



