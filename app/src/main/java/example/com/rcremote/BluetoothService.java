package example.com.rcremote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.*;


public class BluetoothService extends Service {
    private String deviceName;
    private String macAddress;
    private BluetoothSocket btSocket;

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
