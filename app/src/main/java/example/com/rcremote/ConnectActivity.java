package example.com.rcremote;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.bluetooth.*;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
    public static int REQUEST_BLUETOOTH =1;
    private ListView devFound;
    BluetoothAdapter BTAdapter;

    CustomBluetoothAdapter btBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if(BTAdapter == null){
            new AlertDialog.Builder(this)
                .setTitle("Incompatible")
                .setMessage("Your device does not support bluetooth")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
        }

        if(!BTAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btBaseAdapter.clear();
                BTAdapter.startDiscovery();
            }
        });

        devFound = (ListView)findViewById(R.id.devicesfound);
        //btArrayAdapter = new ArrayAdapter<String>(ConnectActivity.this, android.R.layout.simple_list_item_1);
        btBaseAdapter = new CustomBluetoothAdapter();
        devFound.setAdapter(btBaseAdapter);

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public class CustomBluetoothAdapter extends BaseAdapter {

        private List<String> views = new ArrayList<>();

        public void add(String item) {
            if(!views.contains(item)) {
                views.add(item);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(ConnectActivity.this);
            textView.setText(views.get(position));
            return textView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return views.get(position);
        }

        public void clear(){
            views.clear();
        }
    }

    protected final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
       public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btBaseAdapter.add(device.getName() +"\n" + device.getAddress());
                btBaseAdapter.notifyDataSetChanged();
            }
        }
    };
}
