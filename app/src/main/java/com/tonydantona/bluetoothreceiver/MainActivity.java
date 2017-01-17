package com.tonydantona.bluetoothreceiver;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothServiceFragment fragment = new BluetoothServiceFragment();
            transaction.replace(R.id.activity_main, fragment);
            transaction.commit();
        }
    }
}
