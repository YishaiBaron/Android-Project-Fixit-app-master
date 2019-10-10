package com.example.fixxapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;

import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity {
  //  private Button mDriver, mCustomer, mDemo, mGoogle,mFacebook,mlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
       ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE}, 1);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

        //    Toast.makeText(MainActivity.this, "Already Logged In",
                    //Toast.LENGTH_LONG).show();

           Intent intent = new Intent(MainActivity.this, ChooseScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

       // setContentView(R.layout.activity_main);

        // Add code to print out the key hash
       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),  //Or replace to your package name directly, instead getPackageName()  "com.your.app"
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {

        }*/



       // mDriver = (Button) findViewById(R.id.Professional);
     //   mCustomer = (Button) findViewById(R.id.Customer);
       // mDemo  = (Button) findViewById(R.id.Demo);
      //  mGoogle = (Button) findViewById(R.id.Google);
     //   mFacebook  = (Button) findViewById(R.id.Facebook);
      //  mlogin = (Button) findViewById(R.id.login);


/*        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
*/
      //  mCustomer.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
     //           Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
      //          startActivity(intent);
      //          finish();
     //           return;
     //       }
    //    });
/*
        mDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProffDemo.class);
                startActivity(intent);
                finish();
                return;
            }
        });*/

    /*    mGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
                startActivity(intent);
            finish();
          //      return;
            }
        });*/
      //  mlogin.setOnClickListener(new View.OnClickListener() {
      //      @Override
       //     public void onClick(View v) {
       //         Intent intent = new Intent(MainActivity.this, LoginActivity.class);
       //         startActivity(intent);
        //        finish();
                //      return;
       //     }
     //   });
/*
        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
                startActivity(intent);
                finish();
                //      return;
            }
        });*/
    }


}
