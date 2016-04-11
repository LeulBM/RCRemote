package example.com.rcremote;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.bluetooth.*;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.UUID;


public class BluetoothService extends Service {
    private String macAddress;
    private BluetoothSocket btSocket;
    private boolean connected;
    private BluetoothAdapter mAdapt;

    private static BluetoothService starthere;

    public static final UUID BLUETOOTH_SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public int onStartCommand(Intent intent, int flag, int startId){
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction("Connect");
        System.out.println("Starting onStartCommand");
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Starting onStartCommand");
                Intent results = new Intent();
                results.setAction("Connection Results");

                if (connected && macAddress.equals(intent.getStringExtra("BTAddress"))) {
                    results.putExtra("Result", 0);
                } else {
                    macAddress = intent.getStringExtra("BTAddress");
                    mAdapt = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice toConn = mAdapt.getRemoteDevice(macAddress);
                    try {
                        if (!(btSocket == null)) {
                            if (btSocket.isConnected())
                                btSocket.close();
                        }

                        btSocket = toConn.createRfcommSocketToServiceRecord(BLUETOOTH_SERIAL_UUID);
                        btSocket.connect();
                        results.putExtra("Result", 1);

                    } catch (IOException e) {
                        try {
                            results.putExtra("Result", 2);
                            btSocket.close();
                            e.printStackTrace();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    LocalBroadcastManager.getInstance(BluetoothService.this).sendBroadcast(results);
                }
            }
        }, myFilter);

        IntentFilter mySendFilter = new IntentFilter();
        mySendFilter.addAction("Send");
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    btSocket.getOutputStream().write(intent.getByteArrayExtra("ToSend"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, mySendFilter);



        return Service.START_STICKY;
    }

    public void onCreate(){
        starthere = this;
    }

    public static BluetoothService getInstance(){
        return starthere;
    }

    public String getMacAddress(){
        return macAddress;
    }

    public boolean getConnected(){
        return connected;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
