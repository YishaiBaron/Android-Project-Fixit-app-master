package com.example.fixxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChooseScreenActivity extends AppCompatActivity {
    private Button cust, prof,mLogout,mSetting;
    private TextView mHello;
    private TextView mDet,mKet,mRet;
    DatabaseReference myRefr;
    private ProgressBar spinner;

    private int backButtonCount;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        if(!CheckNetwork.isInternetAvailable(ChooseScreenActivity.this)) //returns true if internet available
        {
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(ChooseScreenActivity.this);
            builder1.setMessage("האפליקציה דורשת חיבור לאינטרנט");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "יציאה",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                            startActivity(intent);
                            finish();
                            System.exit(0);

                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.setCanceledOnTouchOutside(false);
            alert11.setCancelable(false);

            alert11.show();
        }


        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(ChooseScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        //Toast.makeText(this, greeting, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_choose_screen);
        cust = (Button) findViewById(R.id.customer);
        prof = (Button) findViewById(R.id.proff);
        mSetting = (Button) findViewById(R.id.setting);
        mLogout = (Button) findViewById(R.id.logout);
        mHello = (TextView) findViewById(R.id.hello);
        mDet = (TextView) findViewById(R.id.details);
        mKet = (TextView) findViewById(R.id.ket);
        mRet = (TextView) findViewById(R.id.ret);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRefr = database.getReference();
        spinner.setVisibility(View.VISIBLE);
        mHello.setText("טוען נתונים....");

        Locale lHebrew = new Locale("he");

        final Geocoder mGeocoder = new Geocoder(ChooseScreenActivity.this, lHebrew);
        myRefr.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fname = dataSnapshot.child("newUsers").child(userId).child("firstname").getValue().toString();
                String servicecall;
                String mdate = "";
                String prob = "";
                String city = "";
                String address="";
                Double longi,lati;

                if (dataSnapshot.child("customerPublish").child(userId).hasChildren()) {
                    prob = dataSnapshot.child("customerPublish").child(userId).child("Problam").getValue().toString();

                    longi = Double.parseDouble(dataSnapshot.child("customerPublish").child(userId).child("longi").getValue().toString());
                    lati = Double.parseDouble(dataSnapshot.child("customerPublish").child(userId).child("lati").getValue().toString());



                    List<Address> addresses = null;
                    try {
                        addresses = mGeocoder.getFromLocation(lati, longi, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(addresses != null && addresses.size() > 0 ){
                        Address maddress = addresses.get(0);
                             if (maddress.getLocality() !=null){
                                   city = "מיקום: "+maddress.getLocality();
                                      if (maddress.getThoroughfare() !=null)
                                              address = " רחוב:"+maddress.getThoroughfare();
                    }}






                    mdate = "\n" + dataSnapshot.child("customerPublish").child(userId).child("date").getValue().toString();
                    servicecall = "יש לך קריאת שירות פעילה:";
                } else
                    servicecall = "אין לך קריאת שירות פעילה";

                Calendar c = Calendar.getInstance();
                int hours = c.get(Calendar.HOUR_OF_DAY);
                String greeting = null;
                if (hours >= 0 && hours <= 12) {
                    greeting = "בוקר טוב ";
                } else if (hours >= 12 && hours <= 16) {
                    greeting = "צהריים טובים ";
                } else if (hours >= 16 && hours <= 21) {
                    greeting = "ערב טוב ";
                } else if (hours >= 21 && hours <= 24) {
                    greeting = "לילה טוב ";
                }
                mHello.setText(greeting + fname + ",");
                mDet.setText(servicecall);
                mKet.setText(prob);
                mRet.setText(city+address+mdate);
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });




mSetting.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ChooseScreenActivity.this, CustomerSettingActivity.class);
        startActivity(intent);
        finish();
        return;
    }
});

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ChooseScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseScreenActivity.this, ClientActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseScreenActivity.this, ProffDemo.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
    /*
    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ChooseScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }*/
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "לחץ שוב על לחצן החזרה כדי לסגור את האפליקציה.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
       // FirebaseDatabase database = FirebaseDatabase.getInstance();
       // myRefr = database.getReference();
      //  myRefr.addValueEventListener(valueEventListener);

    }




}