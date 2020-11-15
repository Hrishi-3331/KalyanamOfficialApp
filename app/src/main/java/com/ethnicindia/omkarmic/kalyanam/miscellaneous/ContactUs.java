package com.ethnicindia.omkarmic.kalyanam.miscellaneous;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ethnicindia.omkarmic.kalyanam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactUs extends AppCompatActivity {

    private EditText messageBody;
    private EditText messageSubject;
    private CoordinatorLayout frame;
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        messageBody = (EditText)findViewById(R.id.contact_message);
        messageSubject = (EditText)findViewById(R.id.contact_subject);
        frame = (CoordinatorLayout)findViewById(R.id.contact_frame);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.customer_care));

        mDialog = new ProgressDialog(ContactUs.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setTitle(getString(R.string.sending_message));
    }

    public void sendMessage(View view){
        final String subject = messageSubject.getText().toString();
        final String body = messageBody.getText().toString();

        if (subject.isEmpty()){
            Snackbar.make(frame, R.string.request_subject, Snackbar.LENGTH_SHORT).show();
        }
        else if (body.isEmpty()){
            Snackbar.make(frame, R.string.request_message, Snackbar.LENGTH_SHORT).show();
        }
        else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactUs.this);
            LayoutInflater inflater = getLayoutInflater();
            View DialogLayout = inflater.inflate(R.layout.confirm_dialog_layout, null);
            builder.setView(DialogLayout);

            Button ok = (Button) DialogLayout.findViewById(R.id.button_positive);
            Button cancel = (Button) DialogLayout.findViewById(R.id.button_negative);
            final android.app.AlertDialog confirm_dialog = builder.create();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmSend(body, subject);
                    confirm_dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm_dialog.dismiss();
                }
            });
            confirm_dialog.show();
        }
    }

    private void confirmSend(String message, String subject) {
        mDialog.show();
        mRef = mRef.push();
        mRef.child(getString(R.string.user)).setValue(mUser.getUid());
        mRef.child(getString(R.string.message)).setValue(message);
        mRef.child(getString(R.string.subject)).setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mDialog.dismiss();
                    showConfirmation();
                }else {
                    mDialog.dismiss();
                    Snackbar.make(frame, R.string.customer_care_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goBack(View view){
        finish();
    }

    private void showConfirmation(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactUs.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.confirm_dialog_layout, null);
        builder.setView(DialogLayout);

        Button ok = (Button) DialogLayout.findViewById(R.id.button_positive);
        Button cancel = (Button) DialogLayout.findViewById(R.id.button_negative);
        ok.setText(R.string.ok);
        cancel.setVisibility(View.GONE);
        TextView titleView = DialogLayout.findViewById(R.id.dialog_title);
        TextView messageView = DialogLayout.findViewById(R.id.dialog_message);
        titleView.setText(R.string.success);
        messageView.setText(R.string.message_senr);
        final android.app.AlertDialog success_dialog = builder.create();
        success_dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                success_dialog.dismiss();
                finish();
            }
        });
    }
}