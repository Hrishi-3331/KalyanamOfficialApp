package com.ethnicindia.omkarmic.kalyanam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventRegistration extends AppCompatActivity {

    private EditText nameInput;
    private EditText cityInput;
    private EditText countryInput;
    private EditText mobileInput;
    private String id;
    private TextView title;
    private TextView fees;
    private boolean enabled;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private SharedPreferences preferences;
    private CoordinatorLayout frame;
    private TextView statementView;
    private Button registrationButton;
    private Button premiumButton;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        nameInput = (EditText)findViewById(R.id.event_registration_name);
        cityInput = (EditText)findViewById(R.id.event_registration_city);
        countryInput = (EditText)findViewById(R.id.event_registration_country);
        mobileInput = (EditText)findViewById(R.id.event_registration_mobile);

        title = (TextView)findViewById(R.id.event_registration_title);
        fees = (TextView)findViewById(R.id.event_registration_fee);

        statementView = (TextView)findViewById(R.id.event_registration_statement);
        registrationButton = (Button)findViewById(R.id.event_registration_button);
        statementView.setText("Registrations not open");
        registrationButton.setEnabled(false);
        premiumButton = (Button)findViewById(R.id.event_registration_premium_button);
        frame = (CoordinatorLayout)findViewById(R.id.registration_coordinator);
        enabled = false;

        mDialog = new ProgressDialog(EventRegistration.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle("Loading...Please wait");
        mDialog.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        mRef = FirebaseDatabase.getInstance().getReference().child("events").child(id);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences(getString(R.string.db_name), MODE_PRIVATE);
        loadDataFromLocalDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("registrations").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    registrationButton.setVisibility(View.GONE);
                    premiumButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    title.setText(snapshot.child("title").getValue().toString());
                    fees.setText(snapshot.child("fees").getValue().toString());
                    DataSnapshot status =  snapshot.child("registration_status");
                    if (status.getValue() != null){
                        String state = status.getValue().toString();
                        if (state.equals("open")) {
                            enabled = true;
                            enableRegistrations();
                        }
                    }
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void enableRegistrations() {
        statementView.setText("Registrations open");
        registrationButton.setEnabled(true);
    }

    private void loadDataFromLocalDB() {
        String name = preferences.getString("name", "NA");
        String city = preferences.getString("city", "NA");
        String country = preferences.getString("country", "NA");
        String mobile = preferences.getString("mobile", "NA");

        if (!name.equals("NA")){
            nameInput.setText(name);
        }
        if (!city.equals("NA")){
            cityInput.setText(city);
        }
        if (!country.equals("NA")){
            countryInput.setText(country);
        }
        if (!mobile.equals("NA")){
            mobileInput.setText(mobile);
        }
    }

    public void registerUser(View view){
        if (!enabled){
            Snackbar.make(frame, "Registrations are currently disabled", Snackbar.LENGTH_SHORT).show();
        }
        else {
            String name = nameInput.getText().toString();
            String city = cityInput.getText().toString();
            String country = countryInput.getText().toString();
            String mobile = mobileInput.getText().toString();

            if (name.isEmpty()) {
                Snackbar.make(frame, "Please enter your name", Snackbar.LENGTH_SHORT).show();
            } else if (city.isEmpty()) {
                Snackbar.make(frame, "Please enter your city", Snackbar.LENGTH_SHORT).show();
            } else if (country.isEmpty()) {
                Snackbar.make(frame, "Please enter your country", Snackbar.LENGTH_SHORT).show();
            } else if (mobile.isEmpty()) {
                Snackbar.make(frame, "Please enter your mobile number", Snackbar.LENGTH_SHORT).show();
            } else {
                mDialog.show();
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", name);
                editor.putString("mobile", mobile);
                editor.putString("city", city);
                editor.putString("country", country);

                mRef = mRef.child("registrations").push();
                FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("registrations").child(id).setValue(mRef.getKey());
                mRef.child("name").setValue(name);
                mRef.child("city").setValue(city);
                mRef.child("country").setValue(country);
                mRef.child("mobile").setValue(mobile);
                mRef.child("id").setValue(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            editor.apply();
                            editor.commit();
                            Intent intent = new Intent(EventRegistration.this, PostRegistration.class);
                            intent.putExtra("id", id);
                            intent.putExtra("code", 1);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(frame, "Could not register. Please try again later.", Snackbar.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }
        }
    }

    public void buyPremium(View view){
        Intent intent = new Intent(EventRegistration.this, PostRegistration.class);
        intent.putExtra("code", 0);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}