package com.example.alnik.examquiz;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alnik on 03-Apr-18.
 */

public class ExamQuiz extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
