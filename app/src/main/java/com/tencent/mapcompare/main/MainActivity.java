package com.tencent.mapcompare.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mapcompare.R;
import com.tencent.mapcompare.bluetoothobject.CameraChangeObject;
import com.tencent.mapcompare.mapfragment.CameraPosition;
import com.tencent.mapcompare.mapfragment.MapCameraChangedListener;
import com.tencent.mapcompare.mapfragment.MapFragment;
import com.tencent.mapcompare.mapfragment.TencentRasterMapFragment;
import com.tencent.mapcompare.mapfragment.TencentVectorMapFragment;
import com.tencent.mapcompare.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MapCameraChangedListener{

    protected static final String LOG_TAG = "BLUETOOTH";

    private boolean mBound = false;
    /**
     * This field set the device is the map camera changing sender device.
     * If true, it will send it's map camera state, otherwise it won't send
     * any data about the map camera state.
     */
    private IMCAidlInterface mService;

    private Toolbar tb_main;
    private MenuItem mi_bluetooth;
    private MenuItem mi_descoverable;

    private TextView tv_name;
    private TextView tv_address;

    private DrawerLayout dlRoot;
    private MyViewPager viewPager;
    private TabAdapter tabAdapter;
    private NavigationView nvDrawer;
    private MapFragment currentMapFragment;

    private DevicesAdapter devicesAdapter;

    private BluetoothAdapter ba;


    private final BroadcastReceiver btsReceiver = new BroadcastReceiver() {
        BluetoothDevice connectedDevice = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                MainActivity.this.invalidateOptionsMenu();
            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.i(LOG_TAG, "start discovery");
                MainActivity.this.invalidateOptionsMenu();
            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.i(LOG_TAG, "stop discovery");
                MainActivity.this.invalidateOptionsMenu();
            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                Log.i(LOG_TAG, "scan mode changed");
                MainActivity.this.invalidateOptionsMenu();
            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                Log.d("bluetooth_change", "bluetooth adapter connection state changed");
                /**
                 * Connected to a device.
                 */
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED);
                if (state == BluetoothAdapter.STATE_CONNECTED) {
                    connectedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("bluetooth_change", "connect with: " + connectedDevice.getName() + ":" + connectedDevice.getAddress());
                }
                /**
                 * Lose connection with the device.
                 */
                if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("bluetooth_change", "lost connection");
                    if (connectedDevice != null &&
                            device.getAddress().equals(connectedDevice.getAddress())) {
                        Log.d("bluetooth_change", "lost connection: " + connectedDevice.getName() + ":" + connectedDevice.getAddress());
//                        try {
//                            mService.initConnectionThread();
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }

            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                Log.i(LOG_TAG, "bluetooth device found");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesAdapter.addDevices(device);
                devicesAdapter.notifyDataSetChanged();
            }

            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

            }
        }
    };


    private final ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMCAidlInterface.Stub.asInterface(service);
            try {
                mService.setServiceCallback(new IServiceCallback.Stub() {

                    @Override
                    public void onGetCameraChangeObject(CameraChangeObject cameraChangeObject) throws RemoteException {
                        if (!currentMapFragment.isTouching()) {
                            currentMapFragment.setCameraPosition(cameraChangeObject);
                        }
                    }

                    @Override
                    public CameraPosition getMapCenter() throws RemoteException {
                        return currentMapFragment.getCurrentPosition();
                    }

                    @Override
                    public void onConnectSuccess(CameraChangeObject cameraChangeObject) throws RemoteException {
                        currentMapFragment.setCameraPosition(cameraChangeObject);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ba = BluetoothAdapter.getDefaultAdapter();

        initView();
        setListener();

        //Start bluetooth connection service to listen client
        Intent bcs = new Intent(this, BluetoothConnectionService.class);
        startService(bcs);
        bindService(bcs, sc, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcasts();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(btsReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(sc);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mi_bluetooth = menu.findItem(R.id.action_bluetooth);
        mi_descoverable = menu.findItem(R.id.action_discoverable);

        setMenuState();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setMenuState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_bluetooth:
                if (ba.getState() == BluetoothAdapter.STATE_OFF) {
                    ba.enable();
                }
                if (ba.getState() == BluetoothAdapter.STATE_ON) {
                    ba.disable();
                }
                return true;
            case R.id.action_discoverable:
                if (!ba.isEnabled()) {
                    Snackbar.make(dlRoot, "请打开蓝牙", Snackbar.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                Intent descoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                if (ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
                    descoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(descoverableIntent);
                }
                if (ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//                    descoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                }
                return true;
            case R.id.action_connect:
                if (!ba.isEnabled()) {
                    Snackbar.make(dlRoot, "请打开蓝牙", Snackbar.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                showDeviceDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void initView() {
        //set toolbar
        tb_main = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(tb_main);

        //set viewpager
        viewPager = (MyViewPager) findViewById(R.id.vp);
        List<MapFragment> fragments = new ArrayList<MapFragment>();
        fragments.add(new TencentRasterMapFragment());
        fragments.add(new TencentVectorMapFragment());
        tabAdapter = new TabAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(tabAdapter);

        dlRoot = (DrawerLayout) findViewById(R.id.dl_root);
        nvDrawer = (NavigationView) findViewById(R.id.nv_drawer);
        tv_name = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.tv_title);
        tv_address = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.tv_snippet);

        Menu menu = nvDrawer.getMenu();
        if (menu != null) {
            menu.findItem(R.id.navigation_tencent_raster).setChecked(true);
        }
        //Initialize the currentMapFragment
        currentMapFragment = tabAdapter.getItem(0);
    }

    protected void setListener() {

        tb_main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlRoot.isDrawerOpen(nvDrawer)) {
                    dlRoot.closeDrawer(nvDrawer);
                } else {
                    dlRoot.openDrawer(nvDrawer);
                }
            }
        });

        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_tencent_raster:
                        viewPager.setCurrentItem(0);
                        item.setChecked(true);
                        return true;
                    case R.id.navigation_tencent_vector:
                        viewPager.setCurrentItem(1);
                        item.setChecked(true);
                        return true;
                    default:
                        return false;
                }
            }
        });

        tabAdapter.getItem(0).setOnCameraChangedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastposition;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabAdapter.getItem(lastposition).setOnCameraChangedListener(null);
                currentMapFragment = tabAdapter.getItem(position);
                currentMapFragment.setOnCameraChangedListener(MainActivity.this);
                lastposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        isTouching = true;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        isTouching = false;
//                        break;
//                }
//                return false;
//            }
//        });
    }

    protected boolean isBluetoothAvailable() {
        if (ba == null) {
            Toast.makeText(this,
                    R.string.device_unsupported_bluetooth,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else {
        }
        return true;
    }

    /**
     * According with the state of the bluetooth state set the menu icon.
     */
    protected void setMenuState() {
        //Check bluetooth is available. If not return.
        if (!isBluetoothAvailable()) {
            mi_bluetooth.setIcon(ContextCompat.getDrawable(
                    this, R.mipmap.ic_bluetooth_disabled_white_24dp));
            mi_bluetooth.setEnabled(false);
            return ;
        }
        mi_bluetooth.setEnabled(true);
        //status of bluetooth menu item
        if (ba.getState() == BluetoothAdapter.STATE_OFF) {
            Log.i(LOG_TAG, "STATE_OFF");
            mi_bluetooth.setIcon(ContextCompat.getDrawable(
                    this, R.mipmap.ic_bluetooth_disabled_white_24dp));
        }
        if (ba.getState() == BluetoothAdapter.STATE_ON) {
            Log.i(LOG_TAG, "STATE_ON");
            mi_bluetooth.setIcon(ContextCompat.getDrawable(
                    this, R.mipmap.ic_bluetooth_white_24dp));

            if (ba.isDiscovering()) {
                Log.i(LOG_TAG, "STATE_DISCOVERING");
                mi_bluetooth.setIcon(ContextCompat.getDrawable(
                        MainActivity.this, R.mipmap.ic_bluetooth_searching_white_24dp));
            } else {
                mi_bluetooth.setIcon(ContextCompat.getDrawable(
                        MainActivity.this, R.mipmap.ic_bluetooth_white_24dp));
            }
        }
        if (ba.getState() == BluetoothAdapter.STATE_TURNING_ON) {
            Log.i(LOG_TAG, "STATE_TURNING_ON");
            mi_bluetooth.setEnabled(false);
        }
        if (ba.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
            Log.i(LOG_TAG, "STATE_TURNING_OFF");
            mi_bluetooth.setEnabled(false);
        }

        //status of bluetooth detectable
        if (ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            mi_descoverable.setTitle(R.string.action_descoverable_undescoverable);
        }
        if (ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            mi_descoverable.setTitle(R.string.action_descoverable_descoverable);
        }
    }

    /**
     * Show device list in dialog.
     */
    protected void showDeviceDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View vDialog = View.inflate(this, R.layout.dialog_device_picker, null);
        builder.setView(vDialog);
        final AlertDialog dialog = builder.create();

        ListView lvDevices = (ListView) vDialog.findViewById(R.id.lv_devices);
        if (devicesAdapter == null) {
            devicesAdapter = new DevicesAdapter(this);
        } else {
            devicesAdapter.clearDevices();
        }
        lvDevices.setAdapter(devicesAdapter);
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = devicesAdapter.getItem(position);
                if (obj instanceof BluetoothDevice){
                    try {
                        mService.connectToDevice((BluetoothDevice)obj);
                        dialog.dismiss();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ba.cancelDiscovery();
            }
        });
        dialog.show();
        ba.startDiscovery();
    }

    protected void registerBroadcasts() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(btsReceiver, filter);
    }

    protected void sendCamerChangMessage(boolean isChangeing, CameraPosition cameraPosition) {
        if (mBound) {
            CameraChangeObject obj = new CameraChangeObject();
            obj.isChanging = isChangeing;
            obj.cameraPosition=cameraPosition;
            try {
                mService.getCameraChangeObejct(obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapCameraChanging(CameraPosition cameraPosition) {
        if (currentMapFragment.isTouching()) {
            sendCamerChangMessage(true, cameraPosition);
        }
    }

    @Override
    public void mapCameraChanged(CameraPosition cameraPosition) {
        if (currentMapFragment.isTouching()) {
            sendCamerChangMessage(false, cameraPosition);
        }
    }
}
