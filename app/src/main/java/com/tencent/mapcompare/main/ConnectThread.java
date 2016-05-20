package com.tencent.mapcompare.main;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.tencent.mapcompare.util.Log;

import java.io.IOException;

/**
 * Created by wangxiaokun on 16/4/21.
 */
public class ConnectThread extends Thread {

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private Handler mHandler;

    public ConnectThread(Handler handler, BluetoothDevice device) {
        mDevice = device;
        BluetoothSocket tmpSocket = null;
        try {
            tmpSocket = mDevice.createRfcommSocketToServiceRecord(BluetoothConnectionService.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmpSocket;
        mHandler = handler;
    }

    @Override
    public void run() {
        Log.d("connect thread start.");
        try {
            mSocket.connect();

            ConnectedThread connectedThread =
                    new ConnectedThread(mHandler, mSocket);
            connectedThread.start();
            mHandler.obtainMessage(
                    BluetoothConnectionService.MSG_CONNECTED_WITH_DEVICE, 0, 0, connectedThread).
                    sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        Log.d("connect thread finish.");
    }

    public void cancle() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
