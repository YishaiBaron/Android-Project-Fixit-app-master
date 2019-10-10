package com.example.fixxapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.fixxapp.fragments.FormFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProffDemo
        extends FragmentActivity
        implements InfoWindowManager.WindowShowListener,
        GoogleMap.OnMarkerClickListener {
    private Button button,mBack;
    private InfoWindow formWindow;
    private InfoWindowManager infoWindowManager;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);
         button = (Button)findViewById(R.id.btn1) ;
        mBack = (Button) findViewById(R.id.back);


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(ProffDemo.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProffDemo.this, ChooseScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProffDemo.this, ProffDemo.class);  //your class
                startActivity(i);
                finish();
            }
        });

        final MapInfoWindowFragment mapInfoWindowFragment =
                (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.infoWindowMap);

        infoWindowManager = mapInfoWindowFragment.infoWindowManager();
        infoWindowManager.setHideOnFling(true);


        mapInfoWindowFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ProffDemo.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        checkLocationPermission();
                    }
                }



                GpsTracker gps = new GpsTracker(ProffDemo.this);
                    double lati = gps.getLatitude(); // returns latitude
                    double longi = gps.getLongitude();
                LatLng latLng = new LatLng(lati, longi);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(9));
                googleMap.setMyLocationEnabled(true);

            //    Marker Myloc= googleMap.addMarker(new MarkerOptions().position(latLng).title("המיקום שלי").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)).visible(false));
             //   Marker markerMyLoc =  googleMap.addMarker(new MarkerOptions().position(latLng).title("המיקום שלי").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)));

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child("customerPublish");
                myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {


                            Double longi = Double.parseDouble(item_snapshot.child("longi").getValue().toString());
                            Double lati = Double.parseDouble(item_snapshot.child("lati").getValue().toString());
                            String Problam = item_snapshot.child("Problam").getValue().toString();
                            String phone = item_snapshot.child("phone").getValue().toString();
                            String fname = item_snapshot.child("firstname").getValue().toString();
                            String lname = item_snapshot.child("lastname").getValue().toString();
                            String date =  item_snapshot.child("date").getValue().toString();

                            LatLng pos = new LatLng(lati, longi);
                            googleMap.addMarker(new MarkerOptions().position(pos).title("\n"+"טלפון: "+phone).snippet("שם: "+fname+" "+lname+"\n"+date+"\n"+"תיאור הבעיה: "+Problam).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag)));

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),databaseError.getDetails(),Toast.LENGTH_SHORT).show();
                    }

                });

               googleMap.setOnMarkerClickListener(ProffDemo.this);



            }
        });

        infoWindowManager.setWindowShowListener(ProffDemo.this);


    }


    @Override
    public void onBackPressed() {
                        Intent intent = new Intent(ProffDemo.this, ChooseScreenActivity.class);
                        startActivity(intent);
                        finish();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
        final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
        InfoWindow.MarkerSpecification markerSpec =
                new InfoWindow.MarkerSpecification(offsetX, offsetY);
        String title = marker.getTitle();
     FormFragment.setMarker(marker);
        formWindow = new InfoWindow(marker, markerSpec, new FormFragment());

        InfoWindow infoWindow = null;

                infoWindow = formWindow;


        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }

        return true;
    }


    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ProffDemo.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(ProffDemo.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShowStarted: " + infoWindow);
    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShown: " + infoWindow);
    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHideStarted: " + infoWindow);
    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHidden: " + infoWindow);
    }
}