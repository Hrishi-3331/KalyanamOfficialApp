package com.ethnicindia.omkarmic.kalyanam.miscellaneous;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ethnicindia.omkarmic.kalyanam.R;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    public void goBack(View view){
        finish();
    }
}