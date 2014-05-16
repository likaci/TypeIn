package com.xiazhiri.TypeIn;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Date;

public class ActivityMain extends Activity implements View.OnClickListener, MenuItem.OnMenuItemClickListener{
    /**
     * Called when the activity is first created.
     */

    public Location pubLocation;
    SlidingUpPanelLayout upPanelLayout;
    FrameLayout measurePanel;
    TextView coord;
    ProcessListAdapter processListAdapter;

    Runnable syncLocaltion;
    Handler syncLocaltitonHandler;

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

        coord = ((TextView) findViewById(R.id.coord));
        final LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setBearingRequired(true);
        for (String providerName : locationManager.getProviders(true)) {
            Log.i("GPS 可用的Provider: ", providerName);
        }
        final String provider = locationManager.getBestProvider(criteria,true);
        //final String provider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        Log.i("Provider: ", provider + "");


        ArrayList<String> list = new ArrayList<String>();
        list.add("点   xxxx,yyyy    上传完成");
        list.add("面   xxxx,yyyy    上传中");
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,list);
        ListView processList = (ListView)findViewById(R.id.processList);
        processListAdapter = new ProcessListAdapter();
        processList.setAdapter(processListAdapter);


        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                pubLocation = location;
                coord.setText("坐标: " + location.getLatitude() + "," + location.getLongitude() + " 方式: " +  location.getProvider());
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
        };

        ((ToggleButton) findViewById(R.id.locateSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    locationManager.requestLocationUpdates(provider,200,0,locationListener);
                    coord.setText("坐标: 正在定位");
                }
                else
                    locationManager.removeUpdates(locationListener);
            }
        });
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        actionMode = mode;


        syncLocaltitonHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if ( msg.what == 1) {
                    Date date = new Date(pubLocation.getTime());
                    actionMode.setTitle(date.toString());
                    actionMode.setSubtitle(pubLocation.getAccuracy() + "");
                }
                super.handleMessage(msg);
            }
        };

        syncLocaltion = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(1000);
                    if (actionMode == null | pubLocation == null)
                        return;
                    Message msg = syncLocaltitonHandler.obtainMessage();
                    msg.what = 1;
                    msg.sendToTarget();
                }
            }
        };
        new Thread(syncLocaltion).start();
        super.onActionModeStarted(mode);
    }

    @Override
    protected void onResume() {
        TextView tv = new TextView(getBaseContext());
        tv.setText("坐标: xxx,yyy");
        getActionBar().setCustomView(tv);
        super.onResume();
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuItem item = menu.add("定位");
        item.setIcon(R.drawable.ic_menu_gpsoff_dark);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
                        MenuItem item = menu.add("取消");
                        item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                mode.finish();
                                return false;
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }
                };
                startActionMode(callback);
                return false;

            }
        });
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                return false;
            }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (upPanelLayout.isExpanded()) {
                upPanelLayout.collapsePane();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        }
        return false;
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

}
