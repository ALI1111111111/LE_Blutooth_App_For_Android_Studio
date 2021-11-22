package com.droiduino.bluetoothconn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Initialization
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);
        final TextView textViewInfo2 = findViewById(R.id.textViewInfo2);
        final TextView textViewInfo3= findViewById(R.id.textViewInfo3);
        final TextView textViewInfo4= findViewById(R.id.textViewInfo4);

        final Button buttonToggle = findViewById(R.id.buttonToggle);
        buttonToggle.setEnabled(false);
        final Button buttonToggle2 = findViewById(R.id.buttonToggle2);
        buttonToggle2.setEnabled(false);
        final Button buttonToggle3 = findViewById(R.id.buttonToggle3);
        buttonToggle3.setEnabled(false);
        final Button buttonToggle4 = findViewById(R.id.buttonToggle4);
        buttonToggle4.setEnabled(false);
        final ImageView imageView = findViewById(R.id.imageView);
        imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));
        final ImageView imageView2 = findViewById(R.id.imageView2);
        imageView2.setBackgroundColor(getResources().getColor(R.color.colorOff));
        final ImageView imageView3 = findViewById(R.id.imageView3);
        imageView3.setBackgroundColor(getResources().getColor(R.color.colorOff));
        final ImageView imageView4 = findViewById(R.id.imageView4);
        imageView4.setBackgroundColor(getResources().getColor(R.color.colorOff));
//        WebView myWebView = (WebView) findViewById(R.id.webview);
//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        myWebView.loadUrl("http://192.168.10.11/");
        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progree and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                buttonToggle.setEnabled(true);
                                buttonToggle2.setEnabled(true);
                                buttonToggle3.setEnabled(true);
                                buttonToggle4.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        switch (arduinoMsg.toLowerCase()){
                            case "pin1 is turned off":
                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOn));
                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin1 is turned on":
                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));
                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin2 is turned off":
                                imageView2.setBackgroundColor(getResources().getColor(R.color.colorOn));
                                textViewInfo2.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin2 is turned on":
                                imageView2.setBackgroundColor(getResources().getColor(R.color.colorOff));
                                textViewInfo2.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin3 is turned off":
                                imageView3.setBackgroundColor(getResources().getColor(R.color.colorOn));
                                textViewInfo3.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin3 is turned on":
                                imageView3.setBackgroundColor(getResources().getColor(R.color.colorOff));
                                textViewInfo3.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin4 is turned off":
                                imageView4.setBackgroundColor(getResources().getColor(R.color.colorOn));
                                textViewInfo4.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "pin4 is turned on":
                                imageView4.setBackgroundColor(getResources().getColor(R.color.colorOff));
                                textViewInfo4.setText("Arduino Message : " + arduinoMsg);
                                break;
                        }
                        break;
                }
            }
        };

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to adapter list
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        // Button1 to ON/OFF LED on Arduino Board
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText1 = null;
                String btnState = buttonToggle.getText().toString().toLowerCase();
                switch (btnState){
                    case "pin1on":
                        buttonToggle.setText("pin1Off");
                        // Command to turn on LED on Arduino. Must match with the command in Arduino code
                        cmdText1 = "<pin1on>";
                        break;
                    case "pin1off":
                        buttonToggle.setText("pin1On");
                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
                        cmdText1 = "<pin1off>";
                        break;
                }
                // Send command to Arduino board
                connectedThread.write(cmdText1);
            }
        });
        // Button2 to ON/OFF LED on Arduino Board
        buttonToggle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText2 = null;
                String btnState2 = buttonToggle2.getText().toString().toLowerCase();
                switch (btnState2){
                    case "pin2on":
                        buttonToggle2.setText("pin2Off");
                        // Command to turn on LED on Arduino. Must match with the command in Arduino code
                        cmdText2 = "<pin2on>";
                        break;
                    case "pin2off":
                        buttonToggle2.setText("pin2On");
                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
                        cmdText2 = "<pin2off>";
                        break;
                }
                // Send command to Arduino board
                connectedThread.write(cmdText2);
            }
        });
        // Button3 to ON/OFF LED on Arduino Board
        buttonToggle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText3 = null;
                String btnState3 = buttonToggle3.getText().toString().toLowerCase();
                switch (btnState3){
                    case "pin3on":
                        buttonToggle3.setText("pin3Off");
                        // Command to turn on LED on Arduino. Must match with the command in Arduino code
                        cmdText3 = "<pin3on>";
                        break;
                    case "pin3off":
                        buttonToggle3.setText("pin3On");
                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
                        cmdText3 = "<pin3off>";
                        break;
                }
                // Send command to Arduino board
                connectedThread.write(cmdText3);
            }
        });

        // Button4 to ON/OFF LED on Arduino Board
        buttonToggle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText4 = null;
                String btnState4 = buttonToggle4.getText().toString().toLowerCase();
                switch (btnState4){
                    case "pin4on":
                        buttonToggle4.setText("pin4Off");
                        // Command to turn on LED on Arduino. Must match with the command in Arduino code
                        cmdText4 = "<pin4on>";
                        break;
                    case "pin4off":
                        buttonToggle4.setText("pin4On");
                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
                        cmdText4 = "<pin4off>";
                        break;
                }
                // Send command to Arduino board
                connectedThread.write(cmdText4);
            }
        });

    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
