package com.tonydantona.bluetoothreceiver;

/**
 * Created by rti1ajd on 12/14/2016.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.tonydantona.bluetoothreceiver.Immutables.*;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mSocket;
    private final InputStream mmInStream;
    private Handler mHandler;

    private BluetoothServices mBluetoothServices;

    private static final String TAG = "ConnectedThread";

    public ConnectedThread(BluetoothSocket socket, BluetoothServices bluetoothServices, Handler handler) {
        Log.d(TAG, "create ConnectedThread");
        mBluetoothServices = bluetoothServices;
        mSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmInStream = tmpIn;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (mBluetoothServices.getState() == STATE_CONNECTED) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                // Send the obtained bytes to the UI Activity
                Message msg = mHandler.obtainMessage(Immutables.MESSAGE_READ, bytes, -1, buffer);
                mHandler.sendMessage(msg);
//                mHandler.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                // Connection lost, start the service over (AcceptThread) to restart listening mode
                mBluetoothServices.start();
                break;
            }
        }
    }

    // Indicate that the connection was lost and notify the UI Activity.
    public void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Immutables.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Immutables.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        start();
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
