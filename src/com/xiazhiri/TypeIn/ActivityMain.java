package com.xiazhiri.TypeIn;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class ActivityMain extends Activity implements View.OnClickListener, MenuItem.OnMenuItemClickListener{
    /**
     * Called when the activity is first created.
     */

    public Location pubLocation;
    SlidingUpPanelLayout upPanelLayout;
    FrameLayout measurePanel;
    TextView coord;

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
        processList.setAdapter(adapter);


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPoint:
                FragPoint fragPoint = new FragPoint();
                fragPoint.activity = this;
                getFragmentManager().beginTransaction().replace(R.id.measurePanel,fragPoint).commit();
                upPanelLayout.expandPane();
                break;
            case R.id.btnLine:
                FragLine fragLine = new FragLine();
                fragLine.activity = this;
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
    public boolean onCreateOptionsMenu(Menu menu) {
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



        getMenuInflater().inflate(R.menu.actions,menu);
        return super.onCreateOptionsMenu(menu);
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

    class processListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
            return null;
        }
    }

}
