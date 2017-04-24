package com.example.kyle.joulieapp.Utils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by Kyle on 2017-01-20.
 */
public class JoulieSocketIOAPI {

    private ResponseListener mListener;
    private Socket mSocket;

    private static JoulieSocketIOAPI ourInstance = new JoulieSocketIOAPI();

    public static JoulieSocketIOAPI getInstance() {
        return ourInstance;
    }

    private JoulieSocketIOAPI() {
    }

    public void registerListener(ResponseListener listener){
        mListener = listener;
    }

    public void connect(){
        try{
            mSocket = IO.socket("https://joulie-cylon.herokuapp.com/api/robots/kyle");
            mSocket.on("status", onStatusUpdate);
            mSocket.on("connect", onConnected);
            mSocket.on("disconnect", onConnected);
            mSocket.connect();

        } catch (URISyntaxException e) {
            Log.i("test", "connect: ");
        }
    }

    public void status() {
        mSocket.emit("test");
    }

    public void disconnect(){
        mSocket.emit("disconnect");
    }

    public interface ResponseListener {
        void onResSuccess(String response);
        void onResError(String errorMessage);
    }

    private Emitter.Listener onStatusUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(args[0] != null){
            JSONObject nestStatus = (JSONObject)args[0];
            try {
                String current_temp = nestStatus.getString("ambient_temperature_c");
                mListener.onResSuccess("Nest Current Temp: "+ current_temp);
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.onResSuccess("JSON parsing error");
            }
            } else {
                mListener.onResSuccess("test");
            }

        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mListener.onResSuccess("Connected");
        }
    };

    private Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mListener.onResSuccess("Disconnected");
        }
    };

}
