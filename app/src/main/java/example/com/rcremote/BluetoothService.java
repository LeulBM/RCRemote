package example.com.rcremote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.*;

import java.util.UUID;


public class BluetoothService extends Service {
    private String deviceName;
    private String macAddress;
    private BluetoothSocket btSocket;


    public static final UUID BLUETOOTH_SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    public BluetoothService(BTConnection conn){
        deviceName = conn.getName();
        macAddress = conn.getAddress();
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getMacAddress(){
        return macAddress;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
