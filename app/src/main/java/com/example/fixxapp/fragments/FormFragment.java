package com.example.fixxapp.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fixxapp.R;
import com.google.android.gms.maps.model.Marker;

public class FormFragment extends Fragment  {
    private TextView textView;
    private static Marker a;
    private static String phone;


    public static void setMarker(Marker marker) {
        a=marker;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.info_window_form_fragment, container, false);
        TextView tv = view.findViewById(R.id.editTextFormInfoWindow); //id defined in camera.xml

        tv.setText(a.getSnippet()+"\n");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = ("tel:" + a.getTitle());
                Intent mIntent = new Intent(Intent.ACTION_DIAL);
                mIntent.setData(Uri.parse(number));
                startActivity(mIntent);
            }
        };

        final View.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lati = a.getPosition().latitude;
                double longi = a.getPosition().longitude;
                try {
    String url = "waze://?ll=" + lati + "," + longi + "&navigate=yes\n";
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
                }

                catch ( ActivityNotFoundException ex  )
                {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }
            }
        };
        final View.OnClickListener onClickListener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String wa = a.getTitle().substring(1);

                    String url = "https://wa.me/" + "972" + wa;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);


            }
        };


        view.findViewById(R.id.button2).setOnClickListener(onClickListener1);
        view.findViewById(R.id.button1).setOnClickListener(onClickListener2);
        view.findViewById(R.id.button3).setOnClickListener(onClickListener3);


   }
}