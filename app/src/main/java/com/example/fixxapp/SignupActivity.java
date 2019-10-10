package com.example.fixxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail,inputPassword, mFirstName,mLastname,mPhone ;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        mFirstName =(EditText) findViewById(R.id.firstname);
        mLastname =(EditText) findViewById(R.id.lastname);

        mPhone  =(EditText) findViewById(R.id.fphone);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                final String fisrtname = mFirstName.getText().toString();
                final String lastname = mLastname.getText().toString();

                final String phone = mPhone.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "הזן כתובת דוא''ל!", Toast.LENGTH_SHORT).show();
                    return;
                }
                     if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(getApplicationContext(), "הזן כתובת דוא''ל תקנית", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "הזן את הסיסמה!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "הכנס מספר טלפון!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(fisrtname)) {
                    Toast.makeText(getApplicationContext(), "הזן שם פרטי!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(getApplicationContext(), "הזן שם משפחה!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "הסיסמה קצרה מדי, הזן מינימום 6 תווים!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.length() != 10  ) {
                    Toast.makeText(getApplicationContext(), "הכנס מספר טלפון  נייד תקני בעל 10 ספרות", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                             Toast.makeText(SignupActivity.this, "נרשמת בהצלחה!" , Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "אימות נכשל. " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String user_id = auth.getCurrentUser().getUid();

                                    HashMap hashMap = new HashMap();
                                    hashMap.put("firstname",fisrtname);
                                    hashMap.put("lastname",lastname);
                                    hashMap.put("phone",phone);

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("newUsers").child(user_id);
                                    databaseReference.updateChildren(hashMap);
                                    startActivity(new Intent(SignupActivity.this, ChooseScreenActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}