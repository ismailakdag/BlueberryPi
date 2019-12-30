package com.example.blueberrypi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Communication extends AppCompatActivity {
    SeekBar changevolts;
    TextView voltvalue;
    TextView conndev;
    String address=null;
    private ProgressDialog progress;
    BluetoothAdapter myblue=null;
    BluetoothSocket btSocket =null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mmServer;
    int barvalue;



    private boolean isBtConnected=false;
    static final UUID myUUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        new BTbaglan().execute();
        Intent newint = getIntent();
        address=newint.getStringExtra(MainActivity.EXTRA_ADRESS);
        changevolts=(SeekBar) findViewById(R.id.change_voltage);
        changevolts.setMax(100);
        voltvalue=(TextView) findViewById(R.id.voltagevaluetext);
        conndev = (TextView) findViewById(R.id.connecteddevice);

        if(myblue == null){
            myblue = BluetoothAdapter.getDefaultAdapter();
        }
        String name = myblue.getName();
        if(name == null){
            System.out.println("Name is null!");
            name = myblue.getAddress();
        }
        conndev.setText(name);

        changevolts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                barvalue=progress;
                changevoltage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changevoltage() {


        if(btSocket!=null)
        {
            try{
                btSocket.getOutputStream().write(String.valueOf(barvalue).getBytes());
                voltvalue.setText(String.valueOf(barvalue));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void Disconnect(){
        if(btSocket !=null){
            try {
                btSocket.close();

            }catch (IOException e){
//msg("Error");

            }

        }
        finish();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }


    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Communication.this, "Connecting...", "Please Wait");
        }

        // https://gelecegiyazanlar.turkcell.com.tr/konu/android/egitim/android-301/asynctask
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myblue = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myblue.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();





                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                // msg("Baglantı Hatası, Lütfen Tekrar Deneyin");
                Toast.makeText(getApplicationContext(), "Connection Error! Try Again...", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //   msg("Baglantı Basarılı");
                Toast.makeText(getApplicationContext(), "Connection Succesful!", Toast.LENGTH_SHORT).show();

                isBtConnected = true;
            }
            progress.dismiss();
        }

    }
}


