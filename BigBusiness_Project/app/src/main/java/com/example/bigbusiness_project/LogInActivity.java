package com.example.bigbusiness_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class LogInActivity extends AppCompatActivity
{
  EditText t1;
  Button generateOtp,finalver;
  EditText verifyotp;
  TextView newuser;
    boolean flag;
    private String verificationId;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    boolean res;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        flag = false;
        t1 = findViewById(R.id.t1);
        generateOtp = (Button) findViewById(R.id.b1);
        verifyotp = (EditText)findViewById(R.id.entotp);
        finalver = findViewById(R.id.finalverify);
        mAuth = FirebaseAuth.getInstance();
        newuser = (TextView)findViewById(R.id.newus);
         rootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());FirebaseApp.initializeApp(this);
         firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        generateOtp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (t1.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), " Please Enter Your  Phone Number", Toast.LENGTH_LONG).show();
                    return;
                }
                //if (t1.getText().toString().trim().length() != 10) {
                  //  Toast.makeText(getApplicationContext(), " Invalid Phone Number", Toast.LENGTH_LONG).show();
                    //return;
                //}
                else
                    {
                        String phone = "+91"+ t1.getText().toString();
                    sendVerificationCode(phone);
                }
            }
        });
        finalver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(verifyotp.getText().toString()))
                {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    Toast.makeText(LogInActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
                else
                    verifyCode(verifyotp.getText().toString());
            }
        });
        newuser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             startActivity(new Intent(LogInActivity.this,RegisterActivity.class));
             finish();
            }
        });
    }

    private boolean checkforusers(String phone)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {

                        for(DataSnapshot data: snapshot.getChildren())
                        {
                          if(data.child("phone").getValue().toString().equalsIgnoreCase(phone))
                          {
                              Toast.makeText(getApplicationContext()," User Exists in DataBase",Toast.LENGTH_LONG).show();
                         startActivity(new Intent(LogInActivity.this, MainActivity.class));
                          finish();
                          flag = true;
                          break;
                          }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
        return flag;
    }

    private void signInWithCredential(PhoneAuthCredential credential)
    {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Intent i = new Intent(LogInActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        } else
                            {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

        private void sendVerificationCode(String number)
        {
        // this method is used for getting
        // OTP on user phone number.
            PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
        {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null)
            {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                verifyotp.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code)
    {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }
}