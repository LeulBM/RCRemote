package example.com.rcremote;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.bluetooth.*;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
    public static int REQUEST_BLUETOOTH = 1;
    ListView devFound;
    BluetoothAdapter BTAdapter;

    CustomBluetoothAdapter btArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        System.out.println("starting service within Activity");
        startService(new Intent(this, BluetoothService.class));

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
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

        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btArrayAdapter.clear();
                BTAdapter.startDiscovery();
                Toast.makeText(ConnectActivity.this, "Searching", Toast.LENGTH_LONG).show();
            }
        });

        devFound = (ListView) findViewById(R.id.devicesfound);
        btArrayAdapter = new CustomBluetoothAdapter();
        devFound.setAdapter(btArrayAdapter);

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public class CustomBluetoothAdapter extends BaseAdapter {

        private ArrayList<BTConnection> views = new ArrayList<>();

        public void add(BTConnection item) {
            boolean check = true;
            for(BTConnection here : views){
                if(here.getAddress().equals(item.getAddress()))
                    check = false;
            }
            if(check) {
                views.add(item);
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView textView = new TextView(ConnectActivity.this);
            String resource = views.get(position).getName() + "\n" + views.get(position).getAddress();
            textView.setText(resource);
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    BTAdapter.cancelDiscovery();
                    String s = textView.getText().toString();
                    String []lines = s.split("\\s*\\r?\\n\\s*");
                    String mAddress = lines[1];
                    Intent myIntent = new Intent();
                    myIntent.setAction("Connect");
                    myIntent.putExtra("BTAddress", mAddress);
                    LocalBroadcastManager.getInstance(ConnectActivity.this).sendBroadcast(myIntent);

                    IntentFilter respond = new IntentFilter();
                    respond.addAction("Connection Results");
                    LocalBroadcastManager.getInstance(ConnectActivity.this).registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            int resultcheck = intent.getIntExtra("Result", 2);

                            if (resultcheck == 0)
                                Toast.makeText(ConnectActivity.this, "Already Connected to this", Toast.LENGTH_LONG).show();
                            else if (resultcheck == 1) {
                                Toast.makeText(ConnectActivity.this, "Connection Established", Toast.LENGTH_LONG).show();
                                textView.setBackgroundColor(Color.GREEN);
                            } else {
                                Toast.makeText(ConnectActivity.this, "Could not Connect", Toast.LENGTH_LONG).show();
                                textView.setBackgroundColor(Color.RED);
                            }
                        }
                    }, respond);

                }
            });
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

        protected final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BTConnection connect = new BTConnection(device.getName(), device.getAddress(), false);
                    btArrayAdapter.add(connect);
                    btArrayAdapter.notifyDataSetChanged();
                }
            }
        };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }
}
