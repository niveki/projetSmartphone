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
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_one extends Fragment implements OnMapReadyCallback {

    private TextView textSpeed, textLat, textLon, textMes, textIPFrag1, textPortFrag1, textRefreshFrag1, textIPSetting, textPortSetting;
    private Spinner spiRefresh;
    private Button buttonStart, buttonSetting;
    private GoogleMap mMap;
    private String currentLang = Locale.getDefault().getLanguage(), conf, quit, setting, textSettingMess, errorTitle, errorMessage, refreh, adress, stateCo, tSpeed;
    private ClientNMEA clientTCP;
    private PolylineOptions cap;
    private MarkerOptions boat;
    private int cpt;

    public Fragment_one() {
        clientTCP=new ClientNMEA(this);
        cap=new PolylineOptions().geodesic(true).color(Color.RED).width((float) 8);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Init de la vue
        final View view =  inflater.inflate(R.layout.fragment_one, container, false);
        //getters
        textSpeed = null;//view.findViewById(R.id.frag1_speed);
        textLat = null;//view.findViewById(R.id.frag1_lat);
        textLon = null;//view.findViewById(R.id.frag1_lon);
        textMes = view.findViewById(R.id.frag1_mes);
        buttonSetting = view.findViewById(R.id.frag1_boutton_setting);
        buttonStart = view.findViewById(R.id.frag1_boutton_start);
        textIPFrag1 = view.findViewById(R.id.frag1_ip);
        textPortFrag1 = view.findViewById(R.id.frag1_port);
        textRefreshFrag1 = view.findViewById(R.id.frag1_refresh);
        //init des textes
        //setSpeed("NA", currentLang);
        //setLatitude("NA");
        //setLongitude("NA");
        if(currentLang.equals("fr")){
            buttonSetting.setText("RÉGLAGE");
            buttonStart.setText("DÉBUT");
        }else{
            buttonSetting.setText("SETTING");
            buttonStart.setText("START");
        }
        // gestion bouton start/stop
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonStart.getText().equals("DÉBUT") || buttonStart.getText().equals("START")){
                    try {
                        start();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    stop();
                }
            }
        });
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
        setMessage(clientTCP.getBoolConnected(), currentLang);
        // création de la carte
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag1_map)).getMapAsync(this);
        //création de la vue
        return view;
    }
    //Méthode start
    public void start() throws ExecutionException, InterruptedException {
        buttonStart.setText("STOP");

        mMap.clear();
        //clientTCP.doInBackground();
        //setLog(String.valueOf(clientTCP.doInBackground()));
        clientTCP.setConnected();
        //while (clientTCP.getBoolConnected()) {
            clientTCP.run();
        //}
        //setLog("start");
        //clientTCP.setRun();
        //setLog(String.valueOf(clientTCP.getBoolRun()));
        //clientTCP.setConnected();
        //setMessage(clientTCP.getBoolConnected(), currentLang);

    }
    //Méthode stop
    public void stop(){
        if(currentLang.equals("fr")){buttonStart.setText("DÉBUT");}else {buttonStart.setText("START");}
        clientTCP.setConnected();
        //setMessage(clientTCP.getBoolConnected(), currentLang);
        //setLog("stop");
        //setSpeed("NA", currentLang);
        //setLatitude("NA");
        //setLongitude("NA");
        //clientTCP.setConnected();
        //setMessage(clientTCP.getBoolConnected(), currentLang);
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
        //setSpeed(a.get(2).toString(), currentLang);
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
        boat.snippet(getSnippet(currentLang, df.format(lat), df.format(lon), String.valueOf(speed), a.get(3).toString()));
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

    // Méthodes de textes
    public void setSpeed(String s, String l){if(l.equals("fr")){textSpeed.setText("Vitesse: "+s+" Km/h");}else{textSpeed.setText("speed: "+s+" Km/h");}}
    public void setLatitude(String s){textLat.setText("Latitude: "+s);}
    public void setLongitude(String s){textLon.setText("Longitude: "+s);}
    public void setIPFrag1(String val){textIPFrag1.setText("IP: "+val);}
    public void setPortFrag1(int val){textPortFrag1.setText("Port: "+val);}
    public void setRafraichissementFrag1(int val, String l){if(l.equals("fr")){textRefreshFrag1.setText("Rafraichissement: "+val+"s");}else {textRefreshFrag1.setText("refresh: "+val+"s");}}
    public String getSnippet(String l, String lat, String lon, String speed, String angle ){if (l.equals("fr"))tSpeed="- Vitesse: ";else tSpeed="- Speed: ";String a ="- Latitude: "+lat+"\n- Longitude: "+lon+"\n"+tSpeed+speed+" Km/h\n- Angle: "+angle+" °";return a;}
    public void setMessage(boolean boolCo, String l){if(l.equals("fr")){if(boolCo){stateCo="Connecté.";}else{stateCo="Non connecté.";}}else{if(boolCo){stateCo="Connected.";}else{stateCo="Not connect.";}}textMes.setText(stateCo);}
    public void setError(String l){if (l.equals("fr"))textMes.setText("Pas de serveur !");else textMes.setText("No server !");}
    public void setLog(String v){Log.d("niveki", v);}
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

        if(cap!=null){
            mMap.addPolyline(cap);
            //mMap.addMarker(boat);
        }
    }

}