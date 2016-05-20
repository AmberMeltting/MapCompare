package com.tencent.mapcompare.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.tencent.mapcompare.util.Log;

import java.io.IOException;

/**
 * Created by wangxiaokun on 16/4/21.
 */
public class AcceptThread extends Thread {

    private final BluetoothServerSocket mServerSocket;
    private Handler mHandler;

    public AcceptThread(Handler handler, BluetoothAdapter bluetoothAdapter) {
        BluetoothServerSocket tmpBSS = null;
        try {
            tmpBSS = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    bluetoothAdapter.getName(), BluetoothConnectionService.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = tmpBSS;
        mHandler = handler;
    }

    @Override
    public void run() {
        Log.d("accept thread start.");
        BluetoothSocket bluetoothSocket;
        while (true) {
            try {
                bluetoothSocket = mServerSocket.accept();
                if (bluetoothSocket != null) {
                    ConnectedThread connectedThread =
                            new ConnectedThread(mHandler, bluetoothSocket);
                    connectedThread.start();
                    mHandler.obtainMessage(
                            BluetoothConnectionService.MSG_CONNECTED_WITH_DEVICE,
                            BluetoothConnectionService.MSG_ARG_SERVER, 0 , connectedThread).
                            sendToTarget();
                    try {
                        mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        Log.d("accept thread finish.");
    }

    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
