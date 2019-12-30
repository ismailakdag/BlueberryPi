package com.example.blueberrypi;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    BluetoothAdapter myblue;
    TextView bluestatus;
    private Set<BluetoothDevice> paired_devices;
    Button togglebutton, pair_button;
    ListView pairedlist;
    public static String EXTRA_ADRESS="device_adress";
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myblue=BluetoothAdapter.getDefaultAdapter(); //Cihazın default bluetooth adaptörünü kontrol eder.
        togglebutton=(Button) findViewById(R.id.bluetooth_toggle); //Burada oluşturduğumuz buttonu findViewById ile çağırdık.
        pair_button=(Button) findViewById(R.id.button_pair);
        pairedlist= (ListView) findViewById(R.id.paired_listview);
        bluestatus=(TextView) findViewById(R.id.bluetooth_status);

        if(myblue.isEnabled()){
            bluestatus.setText("Status: Bluetooth is ON!");
        }
        else{
            bluestatus.setText("Status: Bluetooth is OFF!");
        }


        togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBluetooth(); //togglebluetooth classı oluşturup bu classın içerisinde bluetooth aç kapa yapılmasını sağladık.
            }
        });

        pair_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpaired();
            }
        });
    }

    private void showpaired() {
    paired_devices=myblue.getBondedDevices();
    ArrayList list = new ArrayList();
    if(paired_devices.size()>0)
    {
        for(BluetoothDevice bt:paired_devices)
        {
            list.add(bt.getName()+"\n"+bt.getAddress());

        }

    }
    else{
        Toast.makeText(getApplicationContext(),"No paired device found!",Toast.LENGTH_SHORT).show();
    }

    final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
    pairedlist.setAdapter(adapter);
    pairedlist.setOnItemClickListener(selectdevice);


    }

    public AdapterView.OnItemClickListener selectdevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String info=((TextView) view ).getText().toString();
            String adress= info.substring(info.length()-17);

            Intent comintent = new Intent (MainActivity.this,Communication.class);
            comintent.putExtra(EXTRA_ADRESS,adress);
            startActivity(comintent);

        }
    };

    private void toggleBluetooth() {

        if(myblue==null){
            Toast.makeText(getApplicationContext(), "Bluetooth device error!",Toast.LENGTH_SHORT).show(); //Eğer bluetooth yoksa bluetooth cihazı yok diyoruz.
        }

        if(!myblue.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Burada bluetooth'u açıyoruz. Eğer disable ise.
            startActivity(enableBTIntent);
            bluestatus.setText("Status: Bluetooth is ON!");

        }
        if(myblue.isEnabled()){ //Burada bluetooth enable ise kapatıyoruz.
            myblue.disable();
            bluestatus.setText("Status: Bluetooth is OFF!");
        }
    }
}
