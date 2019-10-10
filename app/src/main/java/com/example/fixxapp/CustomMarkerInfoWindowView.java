package com.example.fixxapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public CustomMarkerInfoWindowView(Context context) {
        this.context = context.getApplicationContext();
    }


    @Override
    public View getInfoWindow(Marker arg0) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.marker_info_window, null);

        //   LatLng latLng = arg0.getPosition();

        String name = arg0.getTitle();
        String snip = arg0.getSnippet();


        TextView Mname = (TextView) v.findViewById(R.id.name);
        TextView Mphone = (TextView) v.findViewById(R.id.phone);
        //TextView MProblamDetail = (TextView) v.findViewById(R.id.ProblamDetail);


        Mname.setText(name);
        Mphone.setText(snip);

        // MProblamDetail.setText("Problam Detail:"+ProblamDetail);



        return v;



    }

    @Override
    public View getInfoContents(Marker arg0)
    { return null;

    }

}