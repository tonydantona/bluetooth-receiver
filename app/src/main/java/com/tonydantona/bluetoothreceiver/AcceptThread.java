package com.tonydantona.bluetoothreceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import static com.tonydantona.bluetoothreceiver.Immutables.*;

/**
 * Created by rti1ajd on 12/5/2016.
 */

public class AcceptThread extends Thread {
    private final String TAG = "AcceptThread";

    private final BluetoothServerSocket mServerSocket;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothServices mBluetoothServices;

    public AcceptThread(BluetoothServices bluetoothServices, BluetoothAdapter bluetoothAdapter) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            mBluetoothServices = bluetoothServices;
            mBluetoothAdapter = bluetoothAdapter;
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (mBluetoothServices.getState() != STATE_CONNECTED) {
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                synchronized (mBluetoothServices) {
                    switch (mBluetoothServices.getState()) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            mBluetoothServices.connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                    }
                }
            }
        }
    }

    public void close() {
        try {
            mServerSocket.close();
            Log.d(TAG, "close: " + mServerSocket.toString() + " closed");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "BluetoothSocket close error" + "/n" + e.toString());
        }
    }

    // Will cancel the listening socket, and cause the thread to finish
    public void cancel() {
        try {
            mServerSocket.close();
            Log.d(TAG, "cancel: " + mServerSocket.toString() + " cancelled");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "BluetoothSocket cancel error" + "/n" + e.toString());
        }
    }
}
