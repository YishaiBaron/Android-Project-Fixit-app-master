package com.example.fixxapp;
import androidx.appcompat.app.AlertDialog;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ClientActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private Button mButton,mCancel,mBack;
    private FusedLocationProviderClient mFusedLocationClient;
    private SupportMapFragment mapFragment;

    Location mLastLocation;

    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        checkIfPublish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);





        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get the application context
        mButton = (Button) findViewById(R.id.btn1);
        mCancel = (Button) findViewById(R.id.btn2);
        mBack = (Button) findViewById(R.id.back);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientActivity.this, ChooseScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Map userInfo = new HashMap();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("newUsers").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.child("phone").getValue().toString();
                String fname = dataSnapshot.child("firstname").getValue().toString();
                String lname = dataSnapshot.child("lastname").getValue().toString();
                userInfo.put("phone", phone);
                userInfo.put("firstname", fname);
                userInfo.put("lastname", lname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ClientActivity.this);

                alert.setTitle("קריאת שירות");
                alert.setMessage("תיאור הבעיה:");

                final EditText input = new EditText(ClientActivity.this);
                alert.setView(input);
                alert.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String mInput = input.getText().toString();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                       // String date = new Date().toString();

                        String pattern = "תאריך:dd/MM/yyyy שעה: HH:mm";
                        DateFormat df = new SimpleDateFormat(pattern);
                        Date today = Calendar.getInstance().getTime();
                        String todayAsString = df.format(today);


                        DatabaseReference data = FirebaseDatabase.getInstance().getReference("customerPublish").child(userId);
                        LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        userInfo.put("lati", myLocation.latitude);
                        userInfo.put("longi", myLocation.longitude);
                        userInfo.put("Problam", mInput);
                        userInfo.put("date", todayAsString);

                        data.updateChildren(userInfo);
                        // Do something with value!
                        Toast.makeText(getApplication(), "בוצע בהצלחה!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ClientActivity.this, ChooseScreenActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
                alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();

            }

        });



        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("customerPublish").child(userId);
                data.removeValue();
                Toast.makeText(getApplication(), "קריאת שירות בוטלה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClientActivity.this, ChooseScreenActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }








    private void checkIfPublish() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference myRefi = database.getReference().child("customerPublish").child(userId);
        myRefi.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    mButton.setVisibility(View.GONE);
                    mCancel.setVisibility(View.VISIBLE);

                    String pb = dataSnapshot.child("Problam").getValue().toString();
                    String dt = dataSnapshot.child("date").getValue().toString();


                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(ClientActivity.this);
                    builder1.setTitle("קריאת שירות אחרונה:");

                    builder1.setMessage(pb +"\n" + dt);
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "אישור",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.setCanceledOnTouchOutside(false);
                    alert11.show();

                }
                else
                {
                    mButton.setVisibility(View.VISIBLE);
                    mCancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        Intent intent = new Intent(ClientActivity.this, ChooseScreenActivity.class);
        startActivity(intent);
        finish();
    }
    @Override

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                 mMap.addMarker(new MarkerOptions().position(latLng).title("המיקום שלי").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)));

                  //  if (!getDriversAroundStarted)
                    //    getDriversAround();
                }
            }
        }
    };



    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ClientActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(ClientActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}