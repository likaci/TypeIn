package com.xiazhiri.TypeIn;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class ActivityMain extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */

    public Location pubLocation;
    SlidingUpPanelLayout upPanelLayout;
    FrameLayout measurePanel;
    TextView coord;
    ProcessListAdapter processListAdapter;

    Thread syncLocation;
    Boolean syncLocationFlag;
    Handler syncLocatitonHandler;

    //GPS
    Boolean gpsEnable = true;
    LocationManager locationManager;
    LocationListener locationListener;

    ActionMode actionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        upPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        upPanelLayout.setEnableDragViewTouchEvents(true);
        upPanelLayout.setSlidingEnabled(false);
        measurePanel = (FrameLayout)findViewById(R.id.measurePanel);

        findViewById(R.id.btnPoint).setOnClickListener(this);
        findViewById(R.id.btnLine).setOnClickListener(this);
        findViewById(R.id.btnArea).setOnClickListener(this);

        //任务列表
        ListView processList = (ListView)findViewById(R.id.processList);
        processListAdapter = new ProcessListAdapter();
        processList.setAdapter(processListAdapter);

        //GPS
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0,locationListener);

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.actionbar);

        //region ActionMode Title Thread
        syncLocatitonHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if ( msg.what == 1) {
                    actionMode.setTitle(pubLocation.getLongitude() + "," + pubLocation.getLatitude());
                    int timePasted = (int)(System.currentTimeMillis() - pubLocation.getTime())/1000;
                    actionMode.setSubtitle(pubLocation.getAccuracy() + "米 - " + timePasted + "秒");
                }
                super.handleMessage(msg);
            }
        };
        //endregion
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        menu.findItem(R.id.gpsSwithcer).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toggleGps(item);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        actionMode = mode;
        syncLocationFlag = true;
        syncLocation = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (syncLocationFlag) {
                            SystemClock.sleep(1000);
                            if (actionMode == null | pubLocation == null)
                                return;
                            Message msg = syncLocatitonHandler.obtainMessage();
                            msg.what = 1;
                            msg.sendToTarget();
                        }
                    }
                });
        syncLocation.start();
        super.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        syncLocationFlag = false;
        super.onActionModeFinished(mode);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPoint:
                FragPoint fragPoint = new FragPoint(this);
                getFragmentManager().beginTransaction().replace(R.id.measurePanel,fragPoint).commit();
                upPanelLayout.expandPane();
                break;
            case R.id.btnLine:
                FragLine fragLine = new FragLine(this);
                getFragmentManager().beginTransaction().replace(R.id.measurePanel,fragLine).commit();
                upPanelLayout.expandPane();
                break;
            case R.id.btnArea:
                upPanelLayout.expandPane();
                break;
            default:
                break;
        }
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            pubLocation = location;
            ((TextView)getActionBar().getCustomView().findViewById(R.id.title)).setText(pubLocation.getLongitude() + "," + pubLocation.getLatitude());
            int timePasted = (int)(System.currentTimeMillis() - pubLocation.getTime())/1000;
            ((TextView)getActionBar().getCustomView().findViewById(R.id.subTitle)).setText(pubLocation.getAccuracy() + "米 - " + timePasted + "秒");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    class ProcessListAdapter extends BaseAdapter {
        ArrayList<ProcessItem> items;
        public ProcessListAdapter() {
            items = new ArrayList<ProcessItem>();
        }


        @Override

        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_process, null);
            }
            ((TextView)convertView.findViewById(R.id.processNum)).setText(String.valueOf(position));
            ((TextView)convertView.findViewById(R.id.processType)).setText(items.get(position).type);
            ((TextView)convertView.findViewById(R.id.precessStatus)).setText(items.get(position).status);
            return convertView;
        }

        public void add(ProcessItem item) {
            items.add(item);
            notifyDataSetChanged();
        }

    }

    void toggleGps(MenuItem item) {
        if (gpsEnable) {
            locationManager.removeUpdates(locationListener);
            item.setIcon(R.drawable.ic_action_device_access_location_searching);
            gpsEnable = false;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0,locationListener);
            item.setIcon(R.drawable.ic_action_device_access_location_found);
            gpsEnable = true;
        }
    }

}
