package com.example.Drone;

import java.net.*;
import java.io.*;

public class ClientNMEA {
    private int PORT=55555;
    private String IP="127.0.0.2";
    private int REFRESH=1;
    private Fragment_one frag_one;
    private boolean run;
    private boolean connected;

    ClientNMEA(Fragment_one frag_one){
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

    //Extrait le morceau de trame qui nous interresse
    public String extraireTrame(String trame){

        String data = trame;
        //split trame reçus pour garder uniquement la trame NMEA183
        System.out.println("TEST"+trame);
        data = data.substring(data.lastIndexOf("$"));
        System.out.println("TEST"+data);
        //test recuperation trame NMEA183
        if(data.charAt(0)=='$'){
            //test trame type GPRMC
            if(data.substring(1, 6).equals("GPRMC")){
                return data;

            }else
                System.out.println("Erreur le type de trame n'est pas GPRMC");
            return "Error";
        }else
            System.out.println("Erreur Trame vide ou incorrecte");
        return "Error";
    }

    public void log(String data){
        //recuperarion profondeur

        //recuperation temps

        //affichage boite noire (log)
    }

    //Recuperation de l'index de latitude et de la latitude
    public String addLattitude(String data){

        String indexLat;
        String lat;
        //test indicateur de latitude N=nord, S=sud
        if(data.substring(data.lastIndexOf("S"),data.lastIndexOf("S")+1).equals("S")){
            indexLat = "S";
            lat = data.substring(data.lastIndexOf("A")+2,data.lastIndexOf("S")-1);
            return indexLat.concat(" = "+lat);
        }else
            indexLat = "N";
        lat = data.substring(data.lastIndexOf("A")+2,data.lastIndexOf("N")-1);
        return indexLat.concat(" = "+lat);

    }

    //Recuperation de l'index de longitude et de la longitude
    public String addLongitude(String data){

        String indexLat;
        String indexLon;
        String lon;
        //test indicateur de latitude N=nord, S=sud
        if(data.substring(data.lastIndexOf("S"),data.lastIndexOf("S")+1).equals("S"))
            indexLat = "S";

        else
            indexLat = "N";

        if(data.substring(data.lastIndexOf("E"),data.lastIndexOf("E")+1).equals("E")){
            indexLon = "E";
            lon = data.substring(data.lastIndexOf(indexLat)+2,data.lastIndexOf("E")-1);
            return indexLon.concat(" = "+lon);
        }else{
            indexLon = "O";
            lon = data.substring(data.lastIndexOf(indexLat)+2,data.lastIndexOf("O")-1);
            return indexLon.concat(" = "+lon);
        }

    }

    public double addVitesse(String data){
        //Exemple de trame
        //$GPRMC,155105.924,A,3510.64,S,13828.19,E,11.0,51.8,280320,,,*32"
        String vitesse;
        String indexLon;
        if(data.substring(data.lastIndexOf("E"),data.lastIndexOf("E")+1).equals("E"))
            indexLon = "E";
        else
            indexLon = "O";
        vitesse = data.substring(data.lastIndexOf(indexLon)+2,data.lastIndexOf(indexLon)+6);
        return Double.parseDouble(vitesse);

    }

    public String getTrame(){

        Socket socket;
        DataInputStream userInput;
        PrintStream theOutputStream;
        String trame ="";


        try {
            socket = new Socket(this.getIP(),this.getPORT());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            /*
            System.out.println(in.readLine());
            System.out.println("------------------------------------------------");
            String data = nmea.extraireTrame(in.readLine());



            String lat = nmea.addLattitude(data);
            String lon = nmea.addLongitude(data);
            double vitesse = nmea.addVitesse(data);

            System.out.println("Latitude : "+lat +" Longitude : "+lon+" Vitesse : "+vitesse+" kn");
            */


        } catch (Exception e) {
            e.printStackTrace();
        }

        return trame;
    }


}
