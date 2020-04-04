package com.example.Drone;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SocketTCP extends AsyncTask<String, Void, String> {
    private Socket socket;
    private BufferedReader br;
    private String IP;
    private String port;
    private String tagError="error";

    @Override
    protected String doInBackground(String... args) {
        String trame = "";
        IP = args[0];
        port = args[1];
        try {
            socket = new Socket(IP, Integer.valueOf(port));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (int i = 0; i < 18; i++) {
                trame += " " + br.readLine() + "\n";
            }
            //Log.d("niveki",trame);
            socket.close();
        } catch (Exception e) {
            Log.e(tagError, "Exception SocketTCP: " + e.toString());
            return "error";
        }
        return trame;
    }
}
