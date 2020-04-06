package com.example.Drone;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Drone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_three extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private String currentLang = Locale.getDefault().getLanguage(),textDraw, textClear,  textSend, textPlay, textMarkerTitle, textMarkerSnippet, textPopTitle, textPopSnippet, textPopButton, textHome, textMessageClickMarker, textMessageVitesse, JSON, textSimuStart, textSimuEnd, textSnippetSpeed;
    private GoogleMap mMap;
    private Button draw, clear, send;
    private TextView textHello;
    private ArrayList<Marker> markers;
    private ArrayList<Double> wayPoint;
    private int index=1;
    private boolean trace;
    private View mPopup;
    private MarkerOptions boat;
    private PolylineOptions cap;
    private Handler handler;
    private int REFRESH_TIME=5;
    private boolean simu;
    private DecimalFormat df = new DecimalFormat(".######");

    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    public Fragment_three() {
        markers=new ArrayList<Marker>();
        trace=false;
        simu=false;
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

        //Listener button draw
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw();
            }
        });
        //Listener button clear
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        //Listener button send
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(send.getText().equals("Envoyer") || send.getText().equals("Send"))
                    send();
                else
                    play();
            }
        });

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
        if(simu==false) {
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
            Marker marker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
            marker.setSnippet(textMarkerSnippet);
            int cptWaypoint = markers.size() + 1;
            marker.setTitle(textMarkerTitle + cptWaypoint);
            marker.setTag(0);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_boat));
            markers.add(marker);
    /*
            for(int i =0; i< markers.size();i++){
                Log.d("niveki",markers.get(i).getPosition().toString());
            }
            Log.d("niveki","----------------");
    */
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getContext(),textMessageClickMarker,Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Drag marker
        index=markers.indexOf(marker);
        markers.remove(marker);
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
        vit.setText(marker.getSnippet().split(":")[1].trim());

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
                Toast.makeText(getContext(),textMessageVitesse+getSpeed+" km/h",Toast.LENGTH_SHORT).show();
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
        send.setText(textSend);
    }

    protected void send(){
        send.setText(textPlay);
        JSON="\n"+"{\"parcours\": {\n\t\"debug\": \"false\",\n\t\"wayPoint\":[";
        for(int i =0; i< markers.size();i++){
            JSON=JSON+"\n\t\t{\n\t\t\t\"Latitude\": \""+markers.get(i).getPosition().latitude+"\",\n\t\t\t\"Longitude\": \""+markers.get(i).getPosition().longitude+"\",\n\t\t\t\"Speed\": \""+markers.get(i).getSnippet().split(":")[1].trim()+"\"\n\t\t},";
        }
        JSON=JSON+"\n\t]\n}}";
        markers.clear();
    }

    protected void play(){
        wayPoint=new ArrayList<Double>();
        cap=new PolylineOptions().geodesic(true).color(Color.RED).width((float) 8);
        mMap.clear();

        try {
            JSONObject reader = new JSONObject(JSON);
            JSONObject nameRow  = reader.getJSONObject("parcours");
            JSONArray row = nameRow.getJSONArray("wayPoint");
            for (int i = 0; i < row.length(); i++) {
                JSONObject elt = row.getJSONObject(i);
                double lat = Double.parseDouble(elt.getString("Latitude"));
                double lon = Double.parseDouble(elt.getString("Longitude"));
                double speed = Double.parseDouble(elt.getString("Speed"));
                wayPoint.add(lat);
                wayPoint.add(lon);
                wayPoint.add(speed);
            }
        }catch (JSONException e){
            Log.e("error", "JSONException frag 3: "+e.getMessage());
        }
        annim();
    }

    public void annim(){
        if(index==1) {
            Toast.makeText(getContext(), textSimuStart, Toast.LENGTH_LONG).show();
            simu=true;
        }
        handler = new Handler();

        LatLng curentLoc = new LatLng(wayPoint.get(index-1),wayPoint.get(index));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(curentLoc).zoom(15).build()));

        boat = new MarkerOptions();
        boat.position(curentLoc);
        boat.title("Infos: ");
        //int speed= wayPoint.get(index+1);
        boat.snippet(getSnippet(df.format(wayPoint.get(index-1)), df.format(wayPoint.get(index)), wayPoint.get(index+1)));
        boat.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boat32));
        boat.rotation(60);
        cap.add(curentLoc);

        mMap.clear();

        mMap.addMarker(boat);
        mMap.addPolyline(cap);
        index=index+3;
        if(index<wayPoint.size())
            handler.postDelayed(runnableCode, REFRESH_TIME * 1000);
        else{
            Toast.makeText(getContext(), textSimuEnd, Toast.LENGTH_LONG).show();
            simu=false;
            index=1;
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View viewInfosMarker = getActivity().getLayoutInflater().inflate(R.layout.infos_marker, null);
                TextView tvTitle=viewInfosMarker.findViewById(R.id.tv_title);
                TextView tvSubTitle=viewInfosMarker.findViewById(R.id.tv_subtitle);
                tvTitle.setText(boat.getTitle());
                tvSubTitle.setText(boat.getSnippet());
                return viewInfosMarker;
            }
        });
    }
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            annim();
        }
    };
    //function translation
    public void setLang(String l){
        if(l.equals("fr")){
            textDraw= "Dessiner";
            textClear= "Effacer";
            textSend= "Envoyer";
            textPlay="Jouer";
            textMarkerTitle= "Waypoint n°";
            textMarkerSnippet= "Vitesse: 1";
            textPopTitle= "Modifier Vitesse du Waypoint";
            textPopSnippet= "Vitesse: ";
            textPopButton="SAUVEGARGER";
            textHome= "Touchez l'écran pour placer un marqueur.";
            textMessageClickMarker="Marqueur cliqué";
            textMessageVitesse="Vitesse modifiée pour: ";
            textSimuStart="Simulation démarée";
            textSimuEnd="Simulation terminée";
            textSnippetSpeed="- Vitesse: ";
        }else{
            textDraw= "draw";
            textClear= "Clear";
            textSend= "Send";
            textPlay="Play";
            textMarkerTitle= "Waypoint n°";
            textMarkerSnippet= "Speed: 1";
            textPopTitle= "Modified Waypoint Speed";
            textPopSnippet= "Speed: ";
            textPopButton="SAVE";
            textHome= "Tap the screen to place a marker.";
            textMessageClickMarker="Marker cliked";
            textMessageVitesse="Modified speed for: ";
            textSimuStart="Start simulation";
            textSimuEnd="End simulation";
            textSnippetSpeed="- Speed: ";
        }
    }
    public String getSnippet(String lat, String lon, double speed){String a ="- Latitude: "+lat+"\n- Longitude: "+lon+"\n"+textSnippetSpeed+speed+" Km/h";return a;}
}
