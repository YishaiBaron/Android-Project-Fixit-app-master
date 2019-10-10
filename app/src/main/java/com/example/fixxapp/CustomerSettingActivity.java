package com.example.fixxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettingActivity extends AppCompatActivity {

    private EditText mPhoneField,mFirstName,mLastName,mServiceCall;

    private Button mBack, mConfirm,mCancel;
    private TextView mServiceText;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase,mCustomerDatabaseP;

    private String userID;
    private String mName,mLast,mPhone,mService;

    private String mProfileImageUrl;

    private Uri resultUri;
    boolean isPublish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_customer_setting);

        mFirstName = (EditText) findViewById(R.id.firstname);
        mLastName = (EditText) findViewById(R.id.lastname);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mServiceCall = (EditText) findViewById(R.id.servicecall);
        mServiceText = (TextView) findViewById(R.id.servicetext);


        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        mCancel= (Button) findViewById(R.id.cancel);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("newUsers").child(userID);



        mCustomerDatabaseP = FirebaseDatabase.getInstance().getReference().child("customerPublish").child(userID);


        getUserInfo();
/*
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
*/
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhoneField.length() != 10  ) {
                    Toast.makeText(getApplicationContext(), "הכנס מספר טלפון  נייד תקני בעל 10 ספרות", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUserInformation();
                Toast.makeText(getApplicationContext(), "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomerSettingActivity.this, ChooseScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerSettingActivity.this, ChooseScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("customerPublish").child(userID);
                Intent i = new Intent(CustomerSettingActivity.this, CustomerSettingActivity.class);  //your class
                startActivity(i);
                data.removeValue();
                Toast.makeText(getApplicationContext(), "קריאת שירות הוסרה בהצלחה", Toast.LENGTH_SHORT).show();

                finish();
            }
        });



    }
    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("firstname")!=null){
                        mName = map.get("firstname").toString();
                        mFirstName.setText(mName);
                    }
                    if(map.get("lastname")!=null){
                        mLast = map.get("lastname").toString();
                        mLastName.setText(mLast);
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mCustomerDatabaseP.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                isPublish = dataSnapshot.hasChildren();
                if(isPublish)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    mServiceCall.setVisibility(View.VISIBLE);
                    mServiceText.setVisibility(View.VISIBLE);
                    mCancel.setVisibility(View.VISIBLE);
                    if(map.get("Problam")!=null) {
                        mService = map.get("Problam").toString();
                        mServiceCall.setText(mService);
                    }
                }

            }
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void saveUserInformation(){
        mName = mFirstName.getText().toString();
        mLast = mLastName.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mService=mServiceCall.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("firstname",mName);
        userInfo.put("lastname",mLast);
        userInfo.put("phone",mPhone);
        mCustomerDatabase.updateChildren(userInfo);
        if(isPublish)
        {
            userInfo.put("Problam",mService);
            mCustomerDatabaseP.updateChildren(userInfo);

        }

    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent intent = new Intent(CustomerSettingActivity.this, ChooseScreenActivity.class);
        startActivity(intent);
        finish();
    }
}