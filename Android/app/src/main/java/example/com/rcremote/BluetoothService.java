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

                if (macAddress != null && macAddress.equals(intent.getStringExtra("BTAddress"))) {
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
                    int [] toConv = intent.getIntArrayExtra("ToSend");
                    String s ="";

                    if(toConv[0]<10)
                        s = s.concat("00"+toConv[0]+" ");
                    else if(toConv[0] < 100)
                        s = s.concat("0"+toConv[0]+" ");
                    else
                        s = s.concat(toConv[0]+" ");

                    if(toConv[1]<10)
                        s = s.concat(toConv[1]+"\n");

                    btSocket.getOutputStream().write(s.getBytes());
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
