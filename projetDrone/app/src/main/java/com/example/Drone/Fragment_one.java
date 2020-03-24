package com.example.Drone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_one extends Fragment implements OnMapReadyCallback {

    private TextView textSpeed, textLat, textLon, textMes;
    private Button buttonStart, buttonSetting;
    private GoogleMap mMap;
    private String currentLang = Locale.getDefault().getLanguage();
    public Fragment_one() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view =  inflater.inflate(R.layout.fragment_one, container, false);
        //getters
        textSpeed = view.findViewById(R.id.frag1_speed);
        textLat = view.findViewById(R.id.frag1_lat);
        textLon = view.findViewById(R.id.frag1_lon);
        textMes = view.findViewById(R.id.frag1_mes);
        buttonSetting = view.findViewById(R.id.frag1_boutton_setting);
        buttonStart = view.findViewById(R.id.frag1_boutton_start);
        //init des textes
        setSpeed("NA", currentLang);
        setLatitude("NA");
        setLongitude("NA");
        if(currentLang.equals("fr")){
            buttonSetting.setText("RÉGLAGE");
            buttonStart.setText("DÉBUT");
        }else{
            buttonSetting.setText("SETTING");
            buttonStart.setText("START");
        }
        setMessage(currentLang);
        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag1_map)).getMapAsync(this);
        //création de la vue
        return view;
    }

    // Méthodes de textes
    public void setSpeed(String s, String l){if(l.equals("fr")){textSpeed.setText("Vitesse: "+s+" Km/h");}else {textSpeed.setText("speed: "+s+" Km/h");}}
    public void setLatitude(String s){textLat.setText("Latitude: "+s);}
    public void setLongitude(String s){textLon.setText("Longitude: "+s);}
    public void setMessage(String l){if(l.equals("fr")){textMes.setText("Non connecté.");}else {textMes.setText("Not connect.");}}

    // Création de la carte sur le port des Minimes
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(46.145907, -1.165674);
        // Permet de placer un marqueur POUR PLUS TARD /!\
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Port La Rochelle"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(15.0f);
    }

}
