package com.example.Drone;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.Drone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_three extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private String currentLang = Locale.getDefault().getLanguage(),textDraw, textClear,  textSend, textMarkerTitle, textMarkerSnippet, textPopTitle, textPopSnippet, textPopButton, textHome;
    private GoogleMap mMap;
    private Button draw, clear, send;
    private TextView textHello;
    private ArrayList<Marker> markers;
    private boolean trace;
    private View mPopup;
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    public Fragment_three() {
        markers=new ArrayList<Marker>();
        trace=false;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Init de la vue
        final View view =  inflater.inflate(R.layout.fragment_three, container, false);
        //traduction
        setLang(currentLang);
        //get view elet
        draw=view.findViewById(R.id.frag3_boutton_tracer);
        clear=view.findViewById(R.id.frag3_boutton_clear);
        send=view.findViewById(R.id.frag3_boutton_send);
        textHello=view.findViewById(R.id.frag3_hello);
        mPopup=inflater.inflate(R.layout.marker,null);

        //setTextBoutton
        draw.setText(textDraw);
        clear.setText((textClear));
        send.setText(textSend);

        //setTextHome
        textHello.setText(textHome);

        //Listener bouton
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        // Inflate the layout for this fragment

        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag3_map)).getMapAsync(this);
        //création de la vue
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng LR = new LatLng(46.145907, -1.165674);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LR));
        mMap.setMinZoomPreference(15.0f);

        mMap.setOnMapClickListener(this );
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

    }
    @Override
    public void onMapClick(LatLng latLng) {
        clear.setAlpha(1);
        draw.setAlpha(1);
        send.setAlpha(1);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        p.weight = 1;
        clear.setLayoutParams(p);
        draw.setLayoutParams(p);
        send.setLayoutParams(p);
        textHello.setText("");
        //Init marker
        Marker marker= mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
        marker.setSnippet(textMarkerSnippet);
        int cptWaypoint =markers.size()+1;
        marker.setTitle(textMarkerTitle+cptWaypoint);
        marker.setTag(0);
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_boat));
        markers.add(marker);

        for(int i =0; i< markers.size();i++){
            Log.d("niveki",markers.get(i).getPosition().toString());
        }
        Log.d("niveki","----------------");

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        //Popup vitesse
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(textPopTitle);
        builder.setView(mPopup);
        TextView vt=mPopup.findViewById(R.id.marker_text);
        vt.setText(textPopSnippet);

        TextView vit=mPopup.findViewById(R.id.marker_edit);
        vit.setText(marker.getSnippet().split(":")[1]);

        builder.setPositiveButton(textPopButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Objet
                EditText textspeed = ((AlertDialog) dialogInterface).findViewById(R.id.marker_edit);
                String getSpeed=textspeed.getText().toString();
                //Log.d("niveki", getSpeed);
                marker.setSnippet(textPopSnippet+getSpeed);
                marker.hideInfoWindow();
                ((ViewGroup) mPopup.getParent()).removeView(mPopup);
            }
        });
        AlertDialog alert= builder.create();
        alert.setCanceledOnTouchOutside(false);
        //Afficher popup
        alert.show();
    }

    //permet de dessiner le chemin
    public void draw(){
        PolylineOptions po=new PolylineOptions();
        ArrayList<Marker> newMarkers=new ArrayList<Marker>();
        for(Marker l: markers){
            po.add(l.getPosition());
            po.color(Color.parseColor("#FF0000"));
        }
        mMap.addPolyline(po);
        trace=true;
    }

    //function clear
    private void clear() {
        clear.setAlpha(0);
        draw.setAlpha(0);
        send.setAlpha(0);
        textHello.setText(textHome);
        mMap.clear();
        markers.clear();
        trace=false;
    }

    //function translation
    public void setLang(String l){
        if(l.equals("fr")){
            textDraw= "Dessiner";
            textClear= "Effacer";
            textSend= "Envoyer";
            textMarkerTitle= "Waypoint n°";
            textMarkerSnippet= "Vitesse: 1";
            textPopTitle= "Modifier Vitesse du Waypoint";
            textPopSnippet= "Vitesse: ";
            textPopButton="SAUVEGARGER";
            textHome= "Touchez l'écran pour placer un marqueur.";
        }else{
            textDraw= "draw";
            textClear= "Clear";
            textSend= "Send";
            textMarkerTitle= "Waypoint n°";
            textMarkerSnippet= "Speed: 1";
            textPopTitle= "Modified Waypoint Speed";
            textPopSnippet= "Speed: ";
            textPopButton="SAVE";
            textHome= "Tap the screen to place a marker.";
        }
    }
}
