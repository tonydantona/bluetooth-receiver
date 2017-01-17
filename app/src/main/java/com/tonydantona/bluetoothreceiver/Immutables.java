package com.tonydantona.bluetoothreceiver;

import java.util.UUID;

/**
 * Created by rti1ajd on 12/5/2016.
 */

public class Immutables {
    public static final String NAME = "Nav_BT_Receiver";
    public static final UUID MY_UUID = UUID.fromString("F0C7B3B3-5027-461B-A0C3-A30C427D6020");

    // Constants that indicate the current connection state
    public static final  int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

}
