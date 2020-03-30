package com.example.Drone;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketTCP extends AsyncTask<String, Void, String> {
    Socket socket;
    private BufferedReader br;

    @Override
    protected String doInBackground(String... args) {
        String trame="";
        try{
            socket = new Socket(args[0], Integer.valueOf(args[1]));
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for(int i=0;i<18;i++){
                trame +=" "+br.readLine()+"\n";
            }
            //Log.d("niveki",trame);
            socket.close();
        } catch (Exception e) {
            Log.e("niveki",e.toString());
        }
        return trame;
    }

    @Override
    protected void onPostExecute (String s){
        s.toString();
    }
}
