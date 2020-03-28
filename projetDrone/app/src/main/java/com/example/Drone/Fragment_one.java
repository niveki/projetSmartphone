package com.example.Drone;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private TextView textSpeed, textLat, textLon, textMes, textIPFrag1, textPortFrag1, textRefreshFrag1, textIPSetting, textPortSetting;
    private Spinner spiRefresh;
    private Button buttonStart, buttonSetting;
    private GoogleMap mMap;
    private String currentLang = Locale.getDefault().getLanguage(), conf, quit, setting, textSettingMess, errorTitle, errorMessage, refreh, adress;
    private ClientTCP clientTCP;
    public Fragment_one() {
        clientTCP=new ClientTCP(this);
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
                //get
                textIPSetting = viewSetting.findViewById(R.id.setting_set_IP);
                textPortSetting = viewSetting.findViewById(R.id.setting_set_port);
                spiRefresh=viewSetting.findViewById(R.id.setting_spin);
                //set
                textIPSetting.setText(clientTCP.getIP());
                textPortSetting.setText(String.valueOf(clientTCP.getPORT()));
                final ArrayList choice = new ArrayList();
                choice.add(1);
                choice.add(5);
                choice.add(10);
                choice.add(15);
                spiRefresh.setSelection(choice.indexOf(clientTCP.getREFRESH()));

                //popup
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(setting);
                builder.setView(viewSetting);

                //bouton Confirm
                builder.setPositiveButton(conf, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText textIP = ((AlertDialog) dialogInterface).findViewById(R.id.setting_set_IP);
                        EditText textPort = ((AlertDialog) dialogInterface).findViewById(R.id.setting_set_port);
                        Spinner spinTps = ((AlertDialog) dialogInterface).findViewById(R.id.setting_spin);
                        /*modifParam(textIP.getText().toString(), textPort.getText().toString(),
                                spinTps.getSelectedItem().toString().split(" ")[0]);*/
                        if (textIP.getText().toString().equals(clientTCP.getIP()) &&
                                textPort.getText().toString().equals(String.valueOf((clientTCP.getPORT()))) &&
                                spinTps.getSelectedItem().toString().split(" ")[0].equals(String.valueOf(clientTCP.getREFRESH()))){
                            builder.setNegativeButton(quit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            if(currentLang.equals("fr")){textSettingMess="Paramètres identiques";}else{textSettingMess="Same parameters";}
                            Toast.makeText(getContext(),textSettingMess,Toast.LENGTH_LONG).show();
                        }else if(textIP.getText().toString().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
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
        //textIPFrag1.setText(clientTCP.getIP());
        setIPFrag1(clientTCP.getIP());
        setPortFrag1(clientTCP.getPORT());
        setRafraichissementFrag1(clientTCP.getREFRESH(), currentLang);
        setMessage(currentLang);
        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag1_map)).getMapAsync(this);
        //création de la vue
        return view;
    }

    // Méthodes de textes
    public void setSpeed(String s, String l){if(l.equals("fr")){textSpeed.setText("Vitesse: "+s+" Km/h");}else{textSpeed.setText("speed: "+s+" Km/h");}}
    public void setLatitude(String s){textLat.setText("Latitude: "+s);}
    public void setLongitude(String s){textLon.setText("Longitude: "+s);}
    public void setIPFrag1(String val){textIPFrag1.setText("IP: "+val);}
    public void setPortFrag1(int val){textPortFrag1.setText("Port: "+val);}
    public void setRafraichissementFrag1(int val, String l){if(l.equals("fr")){textRefreshFrag1.setText("Rafraichissement: "+val+"s");}else {textRefreshFrag1.setText("refresh: "+val+"s");}}
    public void setMessage(String l){if(l.equals("fr")){textMes.setText("Non connecté.");}else {textMes.setText("Not connect.");}}
    public void modifParam(String ip,String port,String time){
        clientTCP.setIP(ip);
        if(!port.equals("")){
            clientTCP.setPORT(Integer.parseInt(port));
        }
        clientTCP.setREFRESH(Integer.parseInt(time));
        setIPFrag1(clientTCP.getIP());
        setPortFrag1(clientTCP.getPORT());
        setRafraichissementFrag1(clientTCP.getREFRESH(), currentLang);
        if(currentLang.equals("fr")){textSettingMess="Paramètres modifiés";}else{textSettingMess="Modified parameters";}
        Toast.makeText(getContext(),textSettingMess,Toast.LENGTH_LONG).show();
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

// LOG Log.d("niveki", String.valueOf(clientTCP.getREFRESH()));