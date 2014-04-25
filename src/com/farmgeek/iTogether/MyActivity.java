package com.farmgeek.iTogether;

import android.widget.*;
import com.farmgeek.iTogether.models.User;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.farmgeek.iTogether.models.UserManager;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class MyActivity extends Activity {
    private static final String TAG = "iHerd";

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private static final int REQUEST_ENABLE_BT = 1234;
    private Hashtable<String, User>   _users   = new Hashtable<String, User>();
    private Hashtable<String, String> _groups  = new Hashtable<String, String>();

    private BeaconManager beaconManager;

    private void setBackground() {
        double furthestDistance = 0.00;
        SeekBar seekBar = (SeekBar) findViewById(R.id.maxDIstance);
        int maximum_distance = seekBar.getProgress();
        Enumeration<User> e = UserManager.getUsers().elements();

        while(e.hasMoreElements()) {
            double dist = e.nextElement().get_distance();
            if (dist > furthestDistance) {
                furthestDistance = dist;
            }
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.state);
        if (furthestDistance >= maximum_distance) {
            layout.setBackground(getResources().getDrawable(R.drawable.orange_background));
        } else if (furthestDistance < maximum_distance) {
            layout.setBackground(getResources().getDrawable(R.drawable.green_background));
        } else {
            layout.setBackground(getResources().getDrawable(R.drawable.red_background));
        }

        Toast.makeText(this, "Max Distance: " + furthestDistance, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Loop through beacons and attach a user to it
                        for (Beacon beacon : beacons) {
                            if (!UserManager.has(beacon.getMacAddress())) {
                                UserManager.push(new User(beacon, region));
                            }
                        }
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: Create an infinite loop & add a wait (probably using AsyncTask)
                        setBackground();
                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }


    @Override
    protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();
    }

    private void connectToService() {
        Log.d(TAG, "Scanning...");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(MyActivity.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
