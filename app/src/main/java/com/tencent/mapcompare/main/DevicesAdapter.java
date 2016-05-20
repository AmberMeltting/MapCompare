package com.tencent.mapcompare.main;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.mapcompare.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wangxiaokun on 16/3/31.
 */
public class DevicesAdapter extends BaseAdapter {

    private static final String PAIRED = "paired";
    private static final String UNPAIRED = "unpaired";


    private Context mContext;
    private Set<BluetoothDevice> pairedDevices;
    private Set<BluetoothDevice> devices;

    public DevicesAdapter(Context context) {
        mContext = context;
        pairedDevices = new HashSet<BluetoothDevice>();
        devices = new HashSet<BluetoothDevice>();
    }

    public void addDevices(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            pairedDevices.add(bluetoothDevice);
        } else {
            devices.add(bluetoothDevice);
        }
    }

    public void clearDevices() {
        pairedDevices.clear();
        devices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (pairedDevices.size() != 0) {
            count += pairedDevices.size() + 1;
        }
        if (devices.size() != 0) {
            count += devices.size() + 1;
        }

        return count;
    }

    @Override
    public Object getItem(int position) {
        Object obj = null;
        if (pairedDevices.size() == 0) {
            if (devices.size() == 0) {
                return null;
            } else {
                if (position == 0) {
                    return UNPAIRED;
                } else {
                    Iterator iterator = devices.iterator();
                    int i = 1;
                    while (iterator.hasNext()) {
                        obj = iterator.next();
                        if (i++ == position) {
                            return obj;
                        }
                    }
                }
            }
        } else {
            if (position == 0) {
                return PAIRED;
            }
            if (position <= pairedDevices.size()) {
                Iterator iterator = pairedDevices.iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    obj = iterator.next();
                    if (i++ == position) {
                        return obj;
                    }
                }
            }
            if (devices.size() != 0) {
                if (position == pairedDevices.size() + 1) {
                    return UNPAIRED;
                } else {
                    Iterator iterator = devices.iterator();
                    int i = pairedDevices.size() + 2;
                    while (iterator.hasNext()) {
                        obj = iterator.next();
                        if (i++ == position) {
                            return obj;
                        }
                    }
                }
            }
        }
        return obj;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (pairedDevices.size() == 0 && devices.size() == 0) {
            return null;
        }
        Object obj = getItem(position);
        if (obj instanceof String) {
            String str;
            if (((String)obj).equals(PAIRED)) {
                str = mContext.getString(R.string.paired_bluetooth_devices);
            } else {
                str = mContext.getString(R.string.usable_bluetooth_devices);
            }
            convertView = View.inflate(mContext, R.layout.device_group, null);
            TextView tvGroup = (TextView) convertView.findViewById(R.id.tv_group);
            tvGroup.setText(str);
        } else {
            convertView = View.inflate(mContext, R.layout.device_item, null);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            tvName.setText(((BluetoothDevice) obj).getName());
            tvAddress.setText(((BluetoothDevice) obj).getAddress());
        }
        return convertView;
    }
}
