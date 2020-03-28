package com.example.Drone;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_one extends Fragment implements OnMapReadyCallback {

    private TextView textSpeed, textLat, textLon, textMes, textIPFrag1, textPortFrag1, textRefreshFrag1, textIPSetting, textPortSetting, textRefreshSetting;
    private Button buttonStart, buttonSetting;
    private GoogleMap mMap;
    private String currentLang = Locale.getDefault().getLanguage(), conf, quit, setting, refreh, errorTitle, errorMessage, adress;
    public Fragment_one() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
        textIPFrag1 = view.findViewById(R.id.frag1_ip);
        textPortFrag1 = view.findViewById(R.id.frag1_port);
        textRefreshFrag1 = view.findViewById(R.id.frag1_refresh);
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


        // gestion bouton setting
        buttonSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(currentLang.equals("fr")){
                    adress="Addresse IP: ";
                    conf="Confirmer";
                    quit="Annuler";
                    setting="Réglage";
                    refreh="Temps de rafraichissement: ";
                    errorTitle="Erreur paramètre";
                    errorMessage="Format IP non valide xxx.xxx.xxx.xxx !";
                }else{
                    adress="Adress IP: ";
                    conf="Confirm";
                    quit="Canceled";
                    setting="Setting";
                    refreh="Refresh time: ";
                    errorTitle="Parameter error";
                    errorMessage="Wrong ip format xxx.xxx.xxx.xxx !";
                };
                View viewSetting = inflater.inflate(R.layout.activity_setting, null);
                /*
                textIPSetting = viewSetting.findViewById(R.id.setting_set_IP);
                textIPSetting.setText(getIP());

                textPortSetting = viewSetting.findViewById(R.id.setting_set_port);
                textPortSetting.setText(getPort());

                //textRefreshSetting=viewSetting.findViewById(R.id.setting_spin);
                //textRefreshSetting.setText(getRefreshTime());


                Spinner listTemps = viewSetting.findViewById(R.id.setting_spin);
                TextView textTitleIP=viewSetting.findViewById(R.id.setting_text_IP);
                textTitleIP.setText(adress);
                TextView textRefresh=viewSetting.findViewById(R.id.setting_text_refresh);
                textRefresh.setText(refreh);
                ArrayList a = new ArrayList();
                a.add(1);
                a.add(5);
                a.add(10);
                a.add(15);*/

                //popup
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(setting);
                builder.setView(viewSetting);

                //bouton Confirm
                builder.setPositiveButton(conf, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText textIP = ((AlertDialog) dialogInterface).findViewById(R.id.setting_set_IP);
                        EditText textPort=((AlertDialog) dialogInterface).findViewById(R.id.setting_set_port);
                        Spinner spinTps=((AlertDialog) dialogInterface).findViewById(R.id.setting_spin);
                        /*modifParam(textIP.getText().toString(), textPort.getText().toString(),
                                spinTps.getSelectedItem().toString().split(" ")[0]);*/
                        if(textIP.getText().toString().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")){
                            //modification des paramètre
                            modifParam(textIP.getText().toString(), textPort.getText().toString(),
                                    spinTps.getSelectedItem().toString().split(" ")[0]);
                        }else{
                            //erreur de ip saisie
                            final AlertDialog.Builder error = new AlertDialog.Builder(getContext());
                            error.setTitle(errorTitle);
                            error.setMessage(errorMessage);
                            error.setNegativeButton(quit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            error.show();
                        }
                    }
                });
                //boutton cancel
                builder.setNegativeButton(quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                //Afficher popup
                builder.show();
            }
        });
        setIPFrag1("127.0.0.1");
        setPortFrag1("55555");
        setRafraichissementFrag1("5", currentLang);
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
    public void setIPFrag1(String val){textIPFrag1.setText("IP: "+val);}
    public void setPortFrag1(String val){textPortFrag1.setText("Port: "+val);}
    public void setRafraichissementFrag1(String val, String l){if(l.equals("fr")){textRefreshFrag1.setText("Rafraichissement: "+val+"s");}else {textRefreshFrag1.setText("refresh: "+val+"s");}}
    public void setIPSetting(String val){textIPSetting.setText(val);}
    public void setPortSetting(String val){textPortSetting.setText(val);}
    public void setMessage(String l){if(l.equals("fr")){textMes.setText("Non connecté.");}else {textMes.setText("Not connect.");}}
    public void modifParam(String ip,String port,String time){
        setIPFrag1(ip);
        setPortFrag1(port);
        setRafraichissementFrag1(time,currentLang);
        setIPSetting(ip);
        Toast.makeText(getContext(),"Paramètre IP modifié",Toast.LENGTH_LONG).show();
    }

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
