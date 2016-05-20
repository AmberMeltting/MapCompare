package com.tencent.mapcompare.main;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.mapcompare.util.ByteArrayUtil;
import com.tencent.mapcompare.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * This service used for bluetooth data transfer.
 */
public class BluetoothConnectionService extends Service{

    protected static final UUID MY_UUID = UUID.fromString("bf170ca2-594c-411f-8eb3-18329b591180");

    //The first byte of the header.
    /**
     * Set this byte to define the first time communication of the devices, they
     * transfer necessary informations to eachother. Including map status.
     */
    protected static final byte TRANS_CONNECT_SUCCESS = 1;

    //Teh second byte of the header.
    /**
     * Set this byte to define the first data is about camera change.
     */
    protected static final byte TRANS_MAP_CAMERA_CHANGE = 1;

    /**
     * Connect to a new device witch the user selected from the list.
     */
    protected static final int MSG_CONNECT_TO_DEVICE = 1;
    /**
     * Connect with device success.
     */
    protected static final int MSG_CONNECTED_WITH_DEVICE = 2;
    /**
     * Used to specify the connection succeeded as a server.
     */
    protected static final int MSG_ARG_SERVER = 1;
    /**
     * Disconnect with device.
     */
    protected static final int MSG_DISCONNECTED_WITH_DEVICE = 3;

    protected static final int MSG_READ_DATA_FROM_BT = 10;
//    protected static final int MSG_WRITE_DATA_TO_BT = 11;

    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DISCONNECTED = 3;

    private static boolean isServer = false;

    private final Handler handler = new ServiceHandler();
    private IServiceCallback mCallback;

    private BluetoothAdapter ba;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private ConnectThread mConnectThread;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_ON);
                if (ba.getState() == BluetoothAdapter.STATE_ON &&
                        previousState != BluetoothAdapter.STATE_ON) {
                    start();
                }
                if (ba.getState() == BluetoothAdapter.STATE_OFF &&
                        previousState != BluetoothAdapter.STATE_OFF) {
                    stop();
                }
            }
        }
    };

    private final IMCAidlInterface.Stub mBinder = new IMCAidlInterface.Stub() {

        @Override
        public void getCameraChangeObejct(CameraChangeObject cameraChangeObject) throws RemoteException {
            Gson gson = new Gson();
            String strJson = gson.toJson(cameraChangeObject);
            byte[] dataHeader = new byte[10];
            dataHeader[1] = BluetoothConnectionService.TRANS_MAP_CAMERA_CHANGE;
            byte[] bytes = ByteArrayUtil.byteMerge(dataHeader, strJson.getBytes());
            if (mConnectedThread != null) {
                byte[] bytesWrite = bytes;
                mConnectedThread.write(bytesWrite);
            }
        }

        @Override
        public void connectToDevice(BluetoothDevice device) throws RemoteException {
            if (mConnectThread != null) {
                mConnectThread.cancle();
                mConnectThread = null;
            }
            mConnectThread = new ConnectThread(handler, device);
            mConnectThread.start();
        }

        @Override
        public void setServiceCallback(IServiceCallback callback) throws RemoteException {
            mCallback = callback;
        }
    };

    public BluetoothConnectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(MainActivity.LOG_TAG, "on start conmmand");
        ba = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(MainActivity.LOG_TAG, "on bind");
        start();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public synchronized void start() {
        if (!ba.isEnabled()) {
            return;
        }
        if (mConnectThread != null) {
            mConnectThread.cancle();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(handler, ba);
            mAcceptThread.start();
        }
    }

    public synchronized void stop() {
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        if (mConnectThread != null) {
            mConnectThread.cancle();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    private class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_TO_DEVICE:
                    if (mConnectThread != null) {
                        mConnectThread.cancle();
                        mConnectedThread = null;
                    }
                    mConnectThread = new ConnectThread(handler, (BluetoothDevice) (msg.obj));
                    mConnectThread.start();
                    break;
                case MSG_CONNECTED_WITH_DEVICE:
                    mConnectedThread = (ConnectedThread) msg.obj;
                    isServer = (msg.arg1 == BluetoothConnectionService.MSG_ARG_SERVER);
                    if (mCallback != null) {
                        try {
                            CameraChangeObject object = new CameraChangeObject();
                            object.isChanging = false;
                            object.cameraPosition = mCallback.getMapCenter();
                            Gson gson = new Gson();
                            String strJson = gson.toJson(object);
                            byte[] dataHeader = new byte[10];
                            dataHeader[0] = BluetoothConnectionService.TRANS_CONNECT_SUCCESS;
                            dataHeader[1] = BluetoothConnectionService.TRANS_MAP_CAMERA_CHANGE;
                            byte[] bytes = ByteArrayUtil.byteMerge(dataHeader, strJson.getBytes());
                            if (mConnectedThread != null) {
                                byte[] bytesWrite = bytes;
                                mConnectedThread.write(bytesWrite);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_DISCONNECTED_WITH_DEVICE:
                    Log.d("disconnected");
                    isServer = false;
                    stop();
                    start();
                    break;
                case MSG_READ_DATA_FROM_BT:
                    parseData((byte[]) msg.obj, msg.arg2);
                    break;
//                case MSG_WRITE_DATA_TO_BT:
//                    if (mConnectedThread != null) {
//                        byte[] bytesWrite = (byte[]) msg.obj;
//                        mConnectedThread.write(bytesWrite);
//                    }
//                    break;
                default:
            }
            super.handleMessage(msg);
        }

        /**
         * Parse data from blutooth.
         * @param recieve
         * @param count
         */
        private void parseData(byte[] recieve, int count) {
            ByteArrayInputStream bais = new ByteArrayInputStream(recieve);
            try {
                byte[] header = new byte[10];
                bais.read(header, 0, header.length);
                /**
                 *The data contains map camera position infomation.
                 */
                if ((header[1] & TRANS_MAP_CAMERA_CHANGE) == 1) {
                    byte[] info = new byte[count - 10];
                    bais.read(info, 0, info.length);
                    String str = new String(info);
                    Gson gson = new Gson();
                    CameraChangeObject obj = gson.fromJson(str, CameraChangeObject.class);
                    if ((header[0] & TRANS_CONNECT_SUCCESS) == 1 && isServer) {
                        Log.d("link as server");
                        if (mCallback != null) {
                            mCallback.getMapCenter();
                        }
                        return;
                    }
                    if (mCallback != null) {
                        mCallback.onGetCameraChangeObject(obj);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
