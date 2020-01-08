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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Communication extends AppCompatActivity {
    SeekBar changevolts;
    TextView dutycycyle;
    TextView conndev;
    TextView s21text;
    TextView errortext;
    TextView predstatus;
    TextView s31text;
    EditText tbfreq;
    EditText tbvolt;
    Button btsendvalues;
    Button closeconn;
    String address=null;
    private ProgressDialog progress;
    BluetoothAdapter myblue=null;
    BluetoothSocket btSocket =null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mmServer;
    TextView statusconn;
    int barvalue;
    public String freqandvolt;




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
        dutycycyle=(TextView) findViewById(R.id.voltagevaluetext);
        conndev = (TextView) findViewById(R.id.connecteddevice);
        s21text=(TextView) findViewById(R.id.tv_s21);
        s31text=(TextView) findViewById(R.id.tv_s31);
        btsendvalues=(Button) findViewById(R.id.button_sendvalues);
        tbfreq=(EditText) findViewById(R.id.tb_freq);
        tbvolt=(EditText) findViewById(R.id.tb_volt);
        closeconn=(Button) findViewById(R.id.bt_closeconn);
        errortext=(TextView) findViewById(R.id.tv_error);
        statusconn=(TextView) findViewById(R.id.status_tv);
        dutycycyle.setText(String.valueOf(barvalue));





        if(myblue == null){
            myblue = BluetoothAdapter.getDefaultAdapter();
        }
        String name = myblue.getName();
        if(name == null){
            System.out.println("Name is null!");
            name = myblue.getAddress();
        }
        conndev.setText(name);

   btsendvalues.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           sendvalues();
       }
   });
   closeconn.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           closeconnection();
       }
   });


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

    private void closeconnection() {
        if(btSocket!=null)
        {
            try{
                String close="exit";
                btSocket.getOutputStream().write(close.getBytes());
                byte[] buffer= new byte[256];
                int bytes;
                bytes=btSocket.getInputStream().read(buffer);
                String gelenveri2= new String(buffer,0,bytes);
                statusconn.setText(gelenveri2);
                Toast.makeText(getApplicationContext(), "Connection Closed!", Toast.LENGTH_SHORT).show();
                Intent homeintent = new Intent (Communication.this,MainActivity.class);
                startActivity(homeintent);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendvalues() {

        freqandvolt=tbfreq.getText().toString()+","+tbvolt.getText().toString()+","+String.valueOf(barvalue);

        if(btSocket!=null)
        {
            try{
                btSocket.getOutputStream().write(freqandvolt.getBytes());
                byte[] buffer= new byte[256];
                int bytes;
                bytes=btSocket.getInputStream().read(buffer);
                String gelenveri= new String(buffer,0,bytes);
                String [] seperated= gelenveri.split("\\,");
                s21text.setText(seperated[0]);
                s31text.setText(seperated[1]);
                errortext.setText(seperated[2]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changevoltage() {


        if(btSocket!=null)
        {
            //try{
                //btSocket.getOutputStream().write(String.valueOf(barvalue).getBytes());
                dutycycyle.setText(String.valueOf(barvalue));
                //s21text.setText(freqandvolt);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
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


