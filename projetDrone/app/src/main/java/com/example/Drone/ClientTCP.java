package com.example.Drone;

public class ClientTCP {
    private int PORT=55555;
    private String IP="127.0.0.2";
    private int REFRESH=1;
    private Fragment_one frag_one;
    private boolean run;
    private boolean connected;

    ClientTCP(Fragment_one frag_one){
        this.frag_one=frag_one;
        this.run=false;
        this.connected=false;
    }

    //set
    public void setPORT(int p){PORT=p;}
    public void setIP(String ip){IP=ip;}
    public void setREFRESH(int t){REFRESH=t;}
    public void setRun(){run=!getBoolRun();}
    public void setConnected(){connected=!getBoolConnected();}

    //get
    public String getIP(){return IP;}
    public int getPORT(){return PORT;}
    public int getREFRESH(){return REFRESH;}
    public boolean getBoolRun(){return run;}
    public boolean getBoolConnected(){return connected;}
}
