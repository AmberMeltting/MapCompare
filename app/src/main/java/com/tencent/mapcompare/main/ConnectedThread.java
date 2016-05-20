package com.tencent.mapcompare.main;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.tencent.mapcompare.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wangxiaokun on 16/4/21.
 */
public class ConnectedThread extends Thread {

    private final BluetoothSocket mSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private Handler mHandler;

    public ConnectedThread(Handler handler,BluetoothSocket socket) {
        mSocket = socket;
        InputStream tmpIs = null;
        OutputStream tmpOs = null;

        try {
            tmpIs = socket.getInputStream();
            tmpOs = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInputStream = tmpIs;
        mOutputStream = tmpOs;
        mHandler = handler;
    }

    @Override
    public void run() {
        Log.d("connected thread start.");
        int count;

        while (true) {
            try {
                byte[] buffer = new byte[1024];
                count = mInputStream.read(buffer);
                mHandler.obtainMessage(
                        BluetoothConnectionService.MSG_READ_DATA_FROM_BT, 0, count, buffer).
                        sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mInputStream.close();
                    mOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    mHandler.obtainMessage(
                            BluetoothConnectionService.MSG_DISCONNECTED_WITH_DEVICE).
                            sendToTarget();
                    break;
                }
            }
        }
        Log.d("connected thread finish");
    }

    synchronized public void write(byte[] buffer) {
        try {
            mOutputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            mSocket.close();
            mInputStream.close();
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
