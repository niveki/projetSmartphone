package com.example.Drone;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.os.Handler;
import android.util.Log;

public class ClientNMEA {
    private int PORT=55555;
    private String IP="10.0.2.2";
    private int REFRESH_TIME=1;
    private Fragment_one frag_one;
    private boolean run=false;
    private boolean connected=false;
    private String currentLang = Locale.getDefault().getLanguage();

    ClientNMEA(Fragment_one frag_one){
        this.frag_one=frag_one;
        //this.run=false;
        //this.connected=false;
    }

/*
    public void run() throws ExecutionException, InterruptedException {
        //while(getBoolConnected()) {
            String a = new SocketTCP().execute("10.0.2.2", "55555", String.valueOf(getBoolConnected())).get();
            if(!a.equals("error"))
                traitement(a);
            else {
                setConnected();
                frag_one.stop();
            }

            //refresh(2000);
        //}
    }
*/

    public void traitement(String trame){
        try{
            String row[]=trame.split("\n");
            String[] ro=row[0].split("G");
            analyseTrame(trame);
           // TimeUnit.SECONDS.sleep(REFRESH_TIME-1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyseTrame(String trame){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String row[]=trame.split("\n");
        String[] ro=row[0].split("G");
        String fina[]=ro[1].split(",");

        ArrayList a = new ArrayList();
        a.add(convertCoordonnees(fina[4], fina[3]));
        a.add(convertCoordonnees(fina[6], fina[5]));
        a.add(fina[7]);
        a.add(getCap(row[1]));
        //Log.e("niveki", a.get(1).toString());
        frag_one.data(a);
    }

    public double convertCoordonnees(String p, String val){
        double reel = Double.valueOf(val)-Double.valueOf(val.substring(val.indexOf(".")));
        int nb = String.valueOf((int) reel).length()-2;
        String start = String.valueOf(reel).substring(0, nb);
        Double end = Double.valueOf(val.substring(val.indexOf(".")-2))/60;
        Double res = Double.parseDouble(start)+end;
        //DecimalFormat df = new DecimalFormat("0.###");
        if(p.equals("S") || p.equals("W")){
            res=-1*res;
        }
        //res = Double.valueOf(df.format(res));
        return res;
    }

    public double getCap(String val){
        //System.out.println(val);
        String[] ro=val.split(",");
        int i = (int) Double.parseDouble(ro[1]);
        return i;
    }

    public void run(){
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String a = new SocketTCP().execute(IP, String.valueOf(PORT)).get();
                    if(!a.equals("error"))
                        traitement(a);
                    else {
                        setConnected();
                        frag_one.stop();
                        //final AlertDialog.Builder error = new AlertDialog.Builder(rrr());
                        //frag_one.setError(currentLang);
                    }
                } catch (ExecutionException e) {
                    Log.e("niveki", "1: "+e.toString());
                } catch (InterruptedException e) {
                    Log.e("niveki", "2: "+e.toString());
                }

                //run();
            }
        };
        handler.postDelayed(runnable, REFRESH_TIME);
    }

    //set
    public void setPORT(int p){PORT=p;}
    public void setIP(String ip){IP=ip;}
    public void setREFRESH(int t){REFRESH_TIME=t;}
    public void setRun(){run=!getBoolRun();}
    public void setConnected(){connected=!getBoolConnected();}

    //get
    public String getIP(){return IP;}
    public int getPORT(){return PORT;}
    public int getREFRESH(){return REFRESH_TIME;}
    public boolean getBoolRun(){return run;}
    public boolean getBoolConnected(){return connected;}
}
