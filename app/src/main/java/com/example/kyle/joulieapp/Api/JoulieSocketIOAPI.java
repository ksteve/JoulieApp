package com.example.kyle.joulieapp.Api;


        import android.util.Log;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.net.URISyntaxException;

        import com.example.kyle.joulieapp.Models.Device;
        import com.example.kyle.joulieapp.Utils.Tools;
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
            mSocket = IO.socket("https://joulie-core.herokuapp.com/api");
            mSocket.on("state", onStatusUpdate);
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
        void onStateUpdate(Device device, Boolean onOrOff);
        void onConnected(String message);
        void onDisconnected(String message);
       // void onResError(String errorMessage);
    }

    private Emitter.Listener onStatusUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(args[0] != null){
                String json = (String) args[0];


             //   new JSONObject()
                try {
                    JSONObject Status = new JSONObject(json);
                    String socketState = Status.getString("state");
                    boolean mState = true;
                    if(socketState.equals("1")){
                        mState = true;
                    } else if (socketState.equals("0")){
                        mState = false;
                    }

                    String device_id = Status.getString("uuid");

                    Device device = Tools.findDeviceByID(device_id);
                    if(device != null){
                        device.setPowerState(mState);
                        mListener.onStateUpdate(device,mState );
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                   // mListener.onResSuccess("JSON parsing error");
                }
            } else {
                //mListener.onResSuccess("test");
            }

        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mListener.onConnected("Connected");
        }
    };

    private Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mListener.onDisconnected("Disconnected");
        }
    };

//    private Emitter.Listener getOnStatusUpdate

}
