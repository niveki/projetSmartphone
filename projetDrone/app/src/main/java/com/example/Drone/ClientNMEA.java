package com.example.Drone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.os.Handler;
import android.util.Log;

public class ClientNMEA {
    private int PORT=55555;
    private String IP="10.0.2.2";
    private int REFRESH_TIME=1;
    private Fragment_one frag_one;
    private boolean run;
    private Handler handler;
    private String tagError="error";

    ClientNMEA(Fragment_one frag_one){
        this.frag_one=frag_one;
        this.run=false;
    }

    private void analyseTrame(String trame){
        if(getBoolRun()) {
            handler = new Handler();
            String row[] = trame.split("\n");
            String[] ro = row[0].split("G");
            String fina[] = ro[1].split(",");

            ArrayList a = new ArrayList();
            a.add(convertCoordonnees(fina[4], fina[3]));
            a.add(convertCoordonnees(fina[6], fina[5]));
            a.add(fina[7]);
            a.add(getCap(row[1]));
            frag_one.data(a);
            /*for(int i = 0 ; i < a.size(); i++)
                Log.e(tagError,a.get(i).toString());
            Log.e(tagError,"-------------");*/
            handler.postDelayed(newConnection, REFRESH_TIME * 1000);
        }
        //Log.e(tagError,String.valueOf(getBoolRun()));
    }

    public double convertCoordonnees(String p, String val){
        double reel = Double.valueOf(val)-Double.valueOf(val.substring(val.indexOf(".")));
        int nb = String.valueOf((int) reel).length()-2;
        String start = String.valueOf(reel).substring(0, nb);
        Double end = Double.valueOf(val.substring(val.indexOf(".")-2))/60;
        Double res = Double.parseDouble(start)+end;
        if(p.equals("S") || p.equals("W")){
            res=-1*res;
        }
        return res;
    }

    public double getCap(String val){
        String[] ro=val.split(",");
        int i = (int) Double.parseDouble(ro[1]);
        return i;
    }

    private void initSocket(){
        try {
            String a = new SocketTCP().execute(IP, String.valueOf(PORT)).get();
            analyseTrame(a);
        }catch (ExecutionException e) {
            Log.e(tagError, "ExecutionException ClientNMEA: "+e.toString());
        }catch (InterruptedException e) {
            Log.e(tagError, "InterruptedException ClientNMEA: "+e.toString());
        }
    }

    public void runDefaut(){
       initSocket();
    }

    private Runnable newConnection=new Runnable() {
        @Override
        public void run() {
            initSocket();
        }
    };

    //set
    public void setPORT(int p){PORT=p;}
    public void setIP(String ip){IP=ip;}
    public void setREFRESH(int t){REFRESH_TIME=t;}
    public void setRun(){run=!getBoolRun();}

    //get
    public String getIP(){return IP;}
    public int getPORT(){return PORT;}
    public int getREFRESH(){return REFRESH_TIME;}
    public boolean getBoolRun(){return run;}
}
