package com.tonydantona.bluetoothreceiver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * This thread runs while attempting to make an outgoing connection
 * with a device. It runs straight through; the connection either
 * succeeds or fails.
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;


    private Handler mHandler;
    private BluetoothServices mBluetoothServices;

    public ConnectThread(BluetoothDevice device, Handler handler, BluetoothServices bluetoothServices) {
        mDevice = device;
        mHandler = handler;
        mBluetoothServices = bluetoothServices;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(Immutables.MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Create socket failed", e);
        }
        mSocket = tmp;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectThread SocketType");
        setName("ConnectThread");

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a successful connection or an exception
            mSocket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                mSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
            connectionFailed();
            return;
        }

        // Reset the ConnectThread because we're done
//        synchronized (mBluetoothServices) {
//            mConnectThread = null;
//        }

        // Start the connected thread
        mBluetoothServices.connected(mSocket, mDevice);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    public void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Immutables.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Immutables.TOAST, "Unable to connect device");
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