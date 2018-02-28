package com.example.alnik.examquiz;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static java.util.Calendar.*;


public class NewTestActivity extends AppCompatActivity {

    Calendar start;
    Calendar end;
    CustomDateTimePicker custom;
    Date mDate;
    int mYear = -1, mMonth = -1, mDay= -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);

        final Button startButton = findViewById(R.id.startButton);
        final Button endButon = findViewById(R.id.endButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custom = new CustomDateTimePicker(NewTestActivity.this,
                        new CustomDateTimePicker.ICustomDateTimeListener() {

                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                              Date dateSelected, int year, String monthFullName,
                                              String monthShortName, int monthNumber, int date,
                                              String weekDayFullName, String weekDayShortName,
                                              int hour24, int hour12, int min, int sec,
                                              String AM_PM) {
                                mYear = year;
                                mMonth = monthNumber +1;
                                mDay = date;
                                startButton.setText(year +"/" +monthNumber +"/" +date +" -- " +hour24 +":" +min);

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

        endButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custom = new CustomDateTimePicker(NewTestActivity.this,
                        new CustomDateTimePicker.ICustomDateTimeListener() {

                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected,
                                              Date dateSelected, int year, String monthFullName,
                                              String monthShortName, int monthNumber, int date,
                                              String weekDayFullName, String weekDayShortName,
                                              int hour24, int hour12, int min, int sec,
                                              String AM_PM) {
                                mYear = year;
                                mMonth = monthNumber +1;
                                mDay = date;
                                endButon.setText(year +"/" +monthNumber +"/" +date +" -- " +hour24 +":" +min);

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

}


