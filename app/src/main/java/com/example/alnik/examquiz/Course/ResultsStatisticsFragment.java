package com.example.alnik.examquiz.Course;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.alnik.examquiz.MyDemicalFormater;
import com.example.alnik.examquiz.Global;
import com.example.alnik.examquiz.R;
import com.example.alnik.examquiz.models.MultipleChoice;
import com.example.alnik.examquiz.models.ShortAnswer;
import com.example.alnik.examquiz.models.TrueFalse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsStatisticsFragment extends Fragment {

    View mView;
    LinearLayout insert;


    DatabaseReference answersTestsParticipation;
    DatabaseReference marksTestsParticipation;
    DatabaseReference usersTestsParticipation;
    DatabaseReference courseSubscribers;


    ArrayList<Object> questions;
    ArrayList<String> studentsIDparticipated;
    ArrayList<String> passStudents;
    long maxGrade = 0;

    long subscount;
    long studentsParticipated;

    public ResultsStatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_results_statistics, container, false);

        answersTestsParticipation = FirebaseDatabase.getInstance().getReference("Answers").child("Tests").child(Global.test.getId());
        marksTestsParticipation = FirebaseDatabase.getInstance().getReference("Marks").child("Tests").child(Global.test.getId());
        usersTestsParticipation = FirebaseDatabase.getInstance().getReference("TestParticipations").child("Tests").child(Global.test.getId());
        courseSubscribers = FirebaseDatabase.getInstance().getReference("Subscriptions").child("Courses").child(Global.course.getId());

        try{
            answersTestsParticipation.keepSynced(true);
            marksTestsParticipation.keepSynced(true);
            usersTestsParticipation.keepSynced(true);
            courseSubscribers.keepSynced(true);

        }catch (Exception e){
            Log.d("test", "error: "+ e.toString());
        }

        insert = mView.findViewById(R.id.insert);

        loadQuestions();
        calculateParticipation();

        return mView;
    }

    public void loadQuestions(){

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

    }

    private void calculateParticipation() {

        courseSubscribers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                subscount = dataSnapshot.getChildrenCount();

                usersTestsParticipation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        studentsParticipated = dataSnapshot.getChildrenCount();

                        insert.addView(drawChart("Student Partitipation", studentsParticipated, subscount - studentsParticipated, "Participated", "Not Participated"), 0);
                        Log.d("test", "participated " + studentsParticipated +" from " +subscount);
                        calculatePass();
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

    private void calculatePass() {

        passStudents = new ArrayList<>();
        studentsIDparticipated = new ArrayList<>();
        marksTestsParticipation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("test", "onDataChange: " + dataSnapshot.toString());
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    studentsIDparticipated.add((String) ds.getKey());
                    Log.d("test", "onCreateView: " +studentsIDparticipated.toString());
                    pass((String) ds.getKey());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void pass(String student){

        GenericTypeIndicator<ArrayList<Long>> s = new GenericTypeIndicator<ArrayList<Long>>() {};
        marksTestsParticipation.child(student).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Long> marks = new ArrayList<>();
                marks = dataSnapshot.getValue(s);
                long sum = 0;
                for(int i = 0; i < marks.size()-1; i++){
                    sum = sum + marks.get(i);
                }

                if( sum >= (marks.get(marks.size()-1))/2 ) {
                    passStudents.add(student);
                    Log.d("test", "passStudents: " + passStudents.toString());
                }

                if(insert.getChildCount() > 1){
                    insert.removeViewAt(1);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 10, 0, 20);
                    insert.addView(drawBar(passStudents.size(), studentsParticipated - passStudents.size(), subscount-studentsParticipated), 1, layoutParams);
                    //insert.addView(drawChart("Student Success", passStudents.size(), studentsParticipated - passStudents.size(), "Passed", "Failed"), 1);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 10, 0, 20);
                    insert.addView(drawBar(passStudents.size(), studentsParticipated - passStudents.size(), subscount-studentsParticipated), 1, layoutParams);
                    //insert.addView(drawChart("Student Success", passStudents.size(), studentsParticipated - passStudents.size(), "Passed", "Failed"), 1);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private View drawChart(String description, float firstValue, float secondValue, String label1, String label2) {


        LayoutInflater factory = LayoutInflater.from(getContext());
        final View chartLayout = factory.inflate(R.layout.pie_chart, null);
        final PieChart mChart = chartLayout.findViewById(R.id.pieChart);
        mChart.setTag(description);

        mChart.setHoleRadius(25f);
        mChart.setTransparentCircleAlpha(0);
        mChart.setRotationEnabled(false);
        Description de = new Description();
        de.setText(description);
        de.setTextSize(12);
        mChart.setDescription(de);
        mChart.setUsePercentValues(true);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(firstValue, label1));
        entries.add(new PieEntry(secondValue, label2));

        PieDataSet pieDataSet = new PieDataSet(entries, null);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        pieDataSet.setValueFormatter(new PercentFormatter());


        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        pieDataSet.setColors(colors);

        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        PieData pieData = new PieData(pieDataSet);
        mChart.setData(pieData);
        mChart.invalidate();

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                if(mChart.getTag().equals("Student Partitipation")){

                    Toast.makeText(getContext(),   String.format("%.0f of %d students", e.getY(), subscount), Toast.LENGTH_LONG).show();
                } else if(mChart.getTag().equals("Student Success")){

                    Toast.makeText(getContext(),   String.format("%.0f of %d students", e.getY(), studentsParticipated), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onNothingSelected() {

            }
        });

        return chartLayout;

    }

    private View drawBar(long pass, long fail, long notParticipated){

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View chartLayout = factory.inflate(R.layout.bar_chart, null);
        final BarChart barChart = chartLayout.findViewById(R.id.barChart);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0,pass));
        barEntries.add(new BarEntry(1,fail));
        barEntries.add(new BarEntry(2,notParticipated));

        ArrayList<String> names = new ArrayList<>();
        names.add("Passed");
        names.add("Failed");
        names.add("Not Participated");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter((names.toArray(new String[names.size()]))));
        xAxis.setTextSize(10);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        int[] colors = new int[] {Color.GREEN, Color.RED, Color.LTGRAY};

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
        de.setText("Students Success");
        de.setTextSize(12);
        barChart.setDescription(de);
        barChart.setFitBars(true);
        barChart.invalidate();


        return chartLayout;

    }
}
