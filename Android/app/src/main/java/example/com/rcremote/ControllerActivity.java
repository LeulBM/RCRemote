package example.com.rcremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class ControllerActivity extends AppCompatActivity {
    int [] command = {90,0};
    TextView echo;
    Intent sendIntent;
    int loc;
    VerticalSeekBar power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        sendIntent = new Intent();
        sendIntent.setAction("Send");
        sendIntent.putExtra("ToSend", command);

        Button lTurn = (Button) findViewById(R.id.button);
        assert lTurn != null;
        lTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command[1] = 1;
                LocalBroadcastManager.getInstance(ControllerActivity.this).sendBroadcast(sendIntent);
            }
        });

        Button rTurn = (Button) findViewById(R.id.button2);
        assert rTurn != null;
        rTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command[1] = 2;
                LocalBroadcastManager.getInstance(ControllerActivity.this).sendBroadcast(sendIntent);
            }

        });

        Button nTurn = (Button) findViewById(R.id.button3);
        assert nTurn != null;
        nTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                command[1] = 0;
                LocalBroadcastManager.getInstance(ControllerActivity.this).sendBroadcast(sendIntent);
            }
        });

        echo = (TextView) findViewById(R.id.tv_value);
        power = (VerticalSeekBar) findViewById(R.id.seekbar);
        assert power != null;
        power.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //echo.setText(String.valueOf(((double)power.getProgress()-90)/90));
                loc = progress;
                echo.setText(String.valueOf(loc));
                command[0] = (loc);
                LocalBroadcastManager.getInstance(ControllerActivity.this).sendBroadcast(sendIntent);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ToggleButton eStop = (ToggleButton) findViewById(R.id.toggleButton);
        assert eStop != null;
        eStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    command[0] = 90;
                    command[1] = 0;
                    power.setProgress(90);
                    echo.setText("90");
                    LocalBroadcastManager.getInstance(ControllerActivity.this).sendBroadcast(sendIntent);
                }
            }
        });


    }


}
