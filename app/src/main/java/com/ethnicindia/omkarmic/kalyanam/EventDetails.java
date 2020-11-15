package com.ethnicindia.omkarmic.kalyanam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventDetails extends AppCompatActivity {

    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventVenue;
    private TextView eventMode;
    private TextView eventLongDescription;
    private TextView eventShortDescription;
    private TextView devotees;
    private TextView countries;
    private TextView organizations;
    private DatabaseReference mRef;
    private Button registerButton;
    private Button premiumButton;
    private ProgressDialog mDialog;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventImage = (ImageView)findViewById(R.id.event_detailed_image);
        eventTitle = (TextView)findViewById(R.id.event_detailed_title);
        eventDate = (TextView)findViewById(R.id.event_detailed_date);
        eventTime = (TextView)findViewById(R.id.event_detailed_time);
        eventVenue = (TextView)findViewById(R.id.event_detailed_venue);
        eventMode = (TextView)findViewById(R.id.event_detailed_mode);
        eventLongDescription = (TextView)findViewById(R.id.event_detailed_description_long);
        eventShortDescription = (TextView)findViewById(R.id.event_detailed_description_short);
        devotees = (TextView)findViewById(R.id.event_detailed_devotees);
        countries = (TextView)findViewById(R.id.event_detailed_countries);
        organizations = (TextView)findViewById(R.id.event_detailed_organizations);

        registerButton = (Button)findViewById(R.id.button_register);
        premiumButton = (Button)findViewById(R.id.premium_button);

        mDialog = new ProgressDialog(EventDetails.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setTitle("Loading data..");
        mDialog.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        mRef = FirebaseDatabase.getInstance().getReference().child("events").child(id);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    eventTitle.setText(snapshot.child("title").getValue().toString());
                    eventDate.setText(snapshot.child("date").getValue().toString());
                    eventTime.setText(snapshot.child("time").getValue().toString());
                    eventVenue.setText(snapshot.child("venue").getValue().toString());
                    eventMode.setText(snapshot.child("mode").getValue().toString());
                    eventLongDescription.setText(snapshot.child("description_long").getValue().toString());
                    eventShortDescription.setText(snapshot.child("description_short").getValue().toString());
                    devotees.setText(snapshot.child("devotees").getValue().toString());
                    countries.setText(snapshot.child("countries").getValue().toString());
                    organizations.setText(snapshot.child("organizations").getValue().toString());
                    try {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(eventImage);
                    }
                    catch (Exception e){
                        eventImage.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("registrations").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    registerButton.setVisibility(View.GONE);
                    premiumButton.setVisibility(View.VISIBLE);
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void register(View view){
        Intent intent = new Intent(EventDetails.this, EventRegistration.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void buyPremium(View view){
        Intent intent = new Intent(EventDetails.this, PostRegistration.class);
        intent.putExtra("code", 0);
        intent.putExtra("id", id);
        startActivity(intent);
    }

}