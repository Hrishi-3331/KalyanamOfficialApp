package com.ethnicindia.omkarmic.kalyanam.miscellaneous;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ethnicindia.omkarmic.kalyanam.R;

public class MyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
    }

    public void goBack(View view){
        finish();
    }

}