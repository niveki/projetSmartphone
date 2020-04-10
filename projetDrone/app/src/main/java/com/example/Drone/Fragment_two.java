package com.example.Drone;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.example.Drone.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_two extends Fragment implements SensorEventListener, OnMapReadyCallback {

    private String currentLang = Locale.getDefault().getLanguage(),textstop, textmarche;
    private SensorManager capteur;
    private Sensor accelerometer; // gyroscope du smartphone
    private GoogleMap mMap; //map de la vue
    private Button stop, sos;
    private MarkerOptions drone;
    private PolylineOptions cap;
    private double latitude, longitude ,vitesse ,x, y, z ,angle = 0;
    private DecimalFormat df = new DecimalFormat(".######");


    public Fragment_two() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentLang = Locale.getDefault().getLanguage();
        setLang(currentLang);
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_two, container, false);


        //initialisation de sensorManager et de sensor
        capteur = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = capteur.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        capteur.registerListener(Fragment_two.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        //création marker drone
        if (drone == null){
            drone = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boat32));
            latitude = 46.145907;
            longitude = -1.165674;
        }
        //création trajet
        cap = new PolylineOptions().geodesic(true).color(Color.RED).width((float) 8);

        //bouton mode SOS
        sos = view.findViewById(R.id.frag2_boutton_sos);
        sos.setText("SOS");
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos();
            }
        });

        //bouton stop
        stop = view.findViewById(R.id.frag2_boutton_stop);
        stop.setText(textstop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("niveki", "stop");
                stop();
            }
        });

        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag2_map)).getMapAsync(this);
        //création de la vue
        return view;
    }

    //methode appelé a chaque utilisation du capteur
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
            //Log.e("Anthony",String.valueOf(x));
            // Log.e("Anthony",String.valueOf(y));
            // Log.e("Anthony",String.valueOf(z));

            //calcul trajectoire drone
            if(z >= 0 && x > 0) {
                vitesse = (z*(60))/10;
            } else if(x < 0) {
                vitesse = 60;
            }
            if(y < -2.5) {
                angle = angle - y/100;

            } else if(y > 2.5) {
                angle = angle - y/100;
            }

            latitude = latitude +( Math.sin(angle) * (vitesse / 0.5))/1000000;
            longitude = longitude + (Math.cos(angle) * (vitesse / 0.5))/1000000;
        }
        //On garde 6 chiffres après la virgule
        String stringLatitude = df.format(latitude);
        String stringLongitude = df.format(longitude);

        //Nouvelles valeurs de latitude et longitude
        double newLatitude = Double.parseDouble(stringLatitude);
        double newLongitude = Double.parseDouble(stringLongitude);

        //Compare avec les anciennes valeurs afin de reduire le changement de position du marqueur et éviter les crashs
        if(newLatitude != this.getLatitude() && newLongitude != this.getLongitude()){
            //Log.e("Anthony","Passage dans le if pour updateMap");
            updateMap(newLatitude,newLongitude);
        }
    }

    //Mettre à jour la map avec les nouvelles données
    public void updateMap(double newLat , double newLon){
        LatLng curentLoc = new LatLng(newLat, newLon);
        mMap.clear();
        drone.position(curentLoc);
        //drone.rotation(angle); //orienté le marqueur
        //placer la camera sur le marqueur
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(curentLoc).zoom(15).build()));
        cap.add(curentLoc);
        //place le bateau
        mMap.addMarker(drone);
        //forme le tracé
        mMap.addPolyline(cap);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //getteur et setteur utiles
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //methode qui réactive l'utilisation du capteur lorsque la vue est au premier plan
    @Override
    public void onResume(){
        super.onResume();
        capteur.registerListener(Fragment_two.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    //methode qui desactive l'utilisation du capteur lorque la vue est en arriere plan
    @Override
    public void onPause(){
        super.onPause();
        capteur.unregisterListener(Fragment_two.this,accelerometer);
    }

    public void stop(){
        if(stop.getText().equals(textstop)){
            stop.setText(textmarche);
            stop.setBackgroundColor(Color.GREEN);
            onPause();
        }else{
            stop.setText(textstop);
            stop.setBackgroundColor(Color.RED);
            onResume();
        }

    }

    public void sos(){
        LatLng curentLoc = new LatLng(46.145907, -1.165674);
        mMap.clear();
        drone.position(curentLoc);
        //placer la camera sur le marqueur
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(curentLoc).zoom(15).build()));
        cap.add(curentLoc);
        //place le bateau
        mMap.addMarker(drone);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initialisation de Map
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng LR = new LatLng(46.145907, -1.165674);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LR));
        mMap.setMinZoomPreference(15.0f);

        //marker
        drone.position(new LatLng(getLatitude(), getLongitude()));
        mMap.addMarker(drone);

        //cap
        if(cap!=null){
            mMap.addPolyline(cap);
        }
    }
    //function translation
    public void setLang(String l){
        if(l.equals("fr")){
            textstop= "Arrêter";
            textmarche= "Redemarrer";

        }else{
            textstop= "Stop";
            textmarche= "Restart";

        }
    }
}