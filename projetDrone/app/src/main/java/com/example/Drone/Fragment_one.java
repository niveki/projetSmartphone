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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_one extends Fragment implements OnMapReadyCallback {

    private TextView tvMes, tvIPFrag1, tvPortFrag1, tvRefreshFrag1, tvIPSetting, tvPortSetting, tvRefreshSetting;
    private Spinner spiRefresh;
    private Button buttonStart, buttonSetting;
    private GoogleMap mMap;
    private String currentLang, buttonTextSetting, buttonTextStart, textSettingTitle, textSettingIpAdress, textSettingrefresh, textButtonConf, textButtonCanceled, textErrorTitle, textErrorMessage, textErrorMessSame, textOkMessage, textRefresh, textStateCoSuc, textStateCoNotSuc, textSnippetSpeed, textStartClient, textStopClient, textErrorMessageSocket;
    private ClientNMEA clientTCP;
    private PolylineOptions cap;
    private MarkerOptions boat;

    public Fragment_one() {
        clientTCP=new ClientNMEA(this);
        cap=new PolylineOptions().geodesic(true).color(Color.RED).width((float) 8);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentLang = Locale.getDefault().getLanguage();
        setLang(currentLang);
        // Init de la vue
        final View view =  inflater.inflate(R.layout.fragment_one, container, false);
        //getters
        tvMes = view.findViewById(R.id.frag1_mes);
        buttonSetting = view.findViewById(R.id.frag1_boutton_setting);
        buttonStart = view.findViewById(R.id.frag1_boutton_start);
        tvIPFrag1 = view.findViewById(R.id.frag1_ip);
        tvPortFrag1 = view.findViewById(R.id.frag1_port);
        tvRefreshFrag1 = view.findViewById(R.id.frag1_refresh);

        buttonSetting.setText(buttonTextSetting);
        buttonStart.setText(buttonTextStart);

        // gestion bouton start/stop
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonStart.getText().equals("DÉBUT") || buttonStart.getText().equals("START")){
                    Log.d("error", "init du serveur");
                    start();
                }else{
                    stop();
                }
            }
        });
        // gestion bouton setting
        buttonSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View viewSetting = inflater.inflate(R.layout.activity_setting, null);
                //set text textView
                tvIPSetting=viewSetting.findViewById(R.id.setting_text_IP);
                tvIPSetting.setText(textSettingIpAdress);
                tvPortSetting=viewSetting.findViewById(R.id.setting_text_port);
                tvPortSetting.setText("Port: ");
                tvRefreshSetting=viewSetting.findViewById(R.id.setting_text_refresh);
                tvRefreshSetting.setText(textSettingrefresh);
                //get des id pour les setters
                tvIPSetting = viewSetting.findViewById(R.id.setting_set_IP);
                tvPortSetting = viewSetting.findViewById(R.id.setting_set_port);
                spiRefresh=viewSetting.findViewById(R.id.setting_spin);
                //set des valeurs
                tvIPSetting.setText(clientTCP.getIP());
                tvPortSetting.setText(String.valueOf(clientTCP.getPORT()));
                final ArrayList choice = new ArrayList();
                choice.add(1);
                choice.add(5);
                choice.add(10);
                choice.add(15);
                spiRefresh.setSelection(choice.indexOf(clientTCP.getREFRESH()));
                //gestion grisé IP et port si client run
                EditText editTextIP = viewSetting.findViewById(R.id.setting_set_IP);
                EditText editTextPort = viewSetting.findViewById(R.id.setting_set_port);
                if(clientTCP.getBoolRun()) {
                    editTextIP.setEnabled(false);
                    editTextPort.setEnabled(false);
                }else{
                    editTextIP.setEnabled(true);
                    editTextPort.setEnabled(true);
                }
                //popup
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(textSettingTitle);
                builder.setView(viewSetting);
                //bouton Confirm
                builder.setPositiveButton(textButtonConf, new DialogInterface.OnClickListener(){
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
                            builder.setNegativeButton(textButtonCanceled, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            //if(currentLang.equals("fr")){textSettingMess="Paramètres identiques";}else{textSettingMess="Same parameters";}
                            Toast.makeText(getContext(),textErrorMessSame,Toast.LENGTH_LONG).show();
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
                            error.setTitle(textErrorTitle);
                            error.setMessage(textErrorMessage);
                            error.setNegativeButton(textButtonCanceled, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            error.show();
                        }
                    }
                });
                //boutton cancel
                builder.setNegativeButton(textButtonCanceled, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                //Afficher popup
                builder.show();
            }
        });
        //Set les para sur la vue principal
        setIPFrag1(clientTCP.getIP());
        setPortFrag1(clientTCP.getPORT());
        setRafraichissementFrag1(clientTCP.getREFRESH());
        //set etat de la connection
        setMessage(clientTCP.getBoolRun());
        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag1_map)).getMapAsync(this);
        //création de la vue
        return view;
    }
    //Méthode start
    public void start(){
        buttonStart.setText("STOP");
        mMap.clear();
        clientTCP.setRun(true);
        clientTCP.runDefaut();
        if(clientTCP.getBoolRun()) {
            setMessage(clientTCP.getBoolRun());
            Toast.makeText(getContext(), textStartClient, Toast.LENGTH_LONG).show();
        }else{
            buttonStart.setText(buttonTextStart);
            Toast.makeText(getContext(), textErrorMessageSocket, Toast.LENGTH_LONG).show();
        }
    }

    //Méthode stop
    public void stop(){
        buttonStart.setText(buttonTextStart);
        clientTCP.setRun(false);
        setMessage(clientTCP.getBoolRun());
        //setLog(String.valueOf(clientTCP.getBoolRun()));
        Toast.makeText(getContext(),textStopClient,Toast.LENGTH_LONG).show();
    }

    //methode trame et maps
    public void data(ArrayList a){
        //get des valeurs
        double lat = Double.parseDouble(a.get(0).toString());
        double lon = Double.parseDouble(a.get(1).toString());
        double speed = Double.parseDouble(a.get(2).toString());
        int angle = (int) Double.parseDouble(a.get(3).toString());
        //permet d'avoir 4 chiffres après la virgurle
        DecimalFormat df = new DecimalFormat(".######");
        //forme un objet de type LatLong
        LatLng curentLoc = new LatLng(lat, lon);
        //setLatitude(String.valueOf(lat));
        //setLongitude(String.valueOf(lon));
        //setSpeed(a.get(2).toString());
        //setLog("lat: "+lat);
        //setLog("lon: "+lon);
        //setLog("vitesse: "+a.get(2).toString());
        //setLog("angle: "+a.get(3).toString());
        //Log.e("niveki", String.valueOf(i));
        //setLog("-------------");
        //annima la camera au point du bateau centrage et applique un zoom a la carte
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(curentLoc).zoom(15).build()));
        // create marker
        boat = new MarkerOptions();
        boat.position(new LatLng(lat, lon));
        boat.title("Infos: ");
        boat.snippet(getSnippet(df.format(lat), df.format(lon), String.valueOf(speed), a.get(3).toString()));
        boat.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boat32));
        boat.rotation(angle);
        //on ajoute la derniere coordonée
        cap.add(curentLoc);
        //on nettoie la carte pour eviter davoir plusieur bateau
        mMap.clear();
        //on place le bateau
        mMap.addMarker(boat);
        //on forme le tracé
        mMap.addPolyline(cap);
        //permet de créer la bulle au clique (permet le retours à la ligne du snippet via un layout
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

    public void setIPFrag1(String val){tvIPFrag1.setText("IP: "+val);}
    public void setPortFrag1(int val){tvPortFrag1.setText("Port: "+val);}
    public void setRafraichissementFrag1(int val){tvRefreshFrag1.setText(textRefresh+val+"s");}
    public String getSnippet(String lat, String lon, String speed, String angle ){String a ="- Latitude: "+lat+"\n- Longitude: "+lon+"\n"+textSnippetSpeed+speed+" Km/h\n- Angle: "+angle+" °";return a;}
    public void setMessage(boolean boolCo){if(boolCo)tvMes.setText(textStateCoSuc);else tvMes.setText(textStateCoNotSuc);}
    public void setLog(String v){Log.d("niveki", v);}
    public void modifParam(String ip,String port,String time){
        clientTCP.setIP(ip);
        if(!port.equals("")){
            clientTCP.setPORT(Integer.parseInt(port));
        }
        clientTCP.setREFRESH(Integer.parseInt(time));
        setIPFrag1(clientTCP.getIP());
        setPortFrag1(clientTCP.getPORT());
        setRafraichissementFrag1(clientTCP.getREFRESH());
        Toast.makeText(getContext(),textOkMessage,Toast.LENGTH_LONG).show();
    }
    public void setLang(String l){
        if(l.equals("fr")){
            buttonTextSetting="RÉGLAGE";
            buttonTextStart="DÉBUT";
            textSettingTitle="Réglage";
            textSettingIpAdress="Addresse IP: ";
            textSettingrefresh="Temps de rafraichissement: ";
            textButtonConf="Confirmer";
            textButtonCanceled="Annuler";
            textErrorTitle="Erreur paramètre";
            textErrorMessage="Format IP non valide xxx.xxx.xxx.xxx !";
            textErrorMessSame="Paramètres identiques";
            textOkMessage="Paramètres modifiés";
            textRefresh="Rafraichissement: ";
            textStateCoSuc="Connecté.";
            textStateCoNotSuc="Non connecté.";
            textSnippetSpeed="- Vitesse: ";
            textStartClient="Début";
            textStopClient="Stop";
            textErrorMessageSocket="Serveur non initialisé";
        }else{
            buttonTextSetting="SETTING";
            buttonTextStart="START";
            textSettingTitle="Setting";
            textSettingIpAdress="Adress IP: ";
            textSettingrefresh="Refresh time: ";
            textButtonConf="Confirm";
            textButtonCanceled="Canceled";
            textErrorTitle="Parameter error";
            textErrorMessage="Wrong ip format xxx.xxx.xxx.xxx !";
            textErrorMessSame="Same parameters";
            textOkMessage="Changed parameters";
            textRefresh="Refresh: ";
            textStateCoSuc="Connected.";
            textStateCoNotSuc="Not connect.";
            textSnippetSpeed="- Speed: ";
            textStartClient="Start";
            textStopClient="Stop";
            textErrorMessageSocket="Server not initialize";
        }
    }
    // Création de la carte sur le port des Minimes
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if(cap!=null){
            mMap.addPolyline(cap);
        }
    }

}