package com.example.Drone;

public class ClientTCP {
    private int PORT=55555;
    private String IP="127.0.0.2";
    private int REFRESH=1;
    private Fragment_one frag_one;

    ClientTCP(Fragment_one frag_one){
        this.frag_one=frag_one;
    }

    public void setPORT(int p){PORT=p;}
    public void setIP(String ip){IP=ip;}
    public void setREFRESH(int t){REFRESH=t;}
    public String getIP(){return IP;}
    public int getPORT(){return PORT;}
    public int getREFRESH(){return REFRESH;}
}
