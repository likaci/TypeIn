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
import android.widget.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class ActivityMain extends Activity implements MenuItem.OnMenuItemClickListener {
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
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }

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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gpsSwithcer:
                toggleGps(item);
                break;
            case R.id.point:
                FragPoint fragPoint = new FragPoint(this);
                getFragmentManager().beginTransaction().replace(R.id.measurePanel,fragPoint).commit();
                upPanelLayout.expandPane();
                break;
            case R.id.line:
                FragLine fragLine = new FragLine(this);
                getFragmentManager().beginTransaction().replace(R.id.measurePanel,fragLine).commit();
                upPanelLayout.expandPane();
                break;
            case R.id.area:
                upPanelLayout.expandPane();
                break;
            default:
                break;
        }
        return false;
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
        ArrayList<View> views;
        public ProcessListAdapter() {
            views = new ArrayList<View>();
            items = new ArrayList<ProcessItem>();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (position == 0)
            //    views.clear();
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_process, null);
                views.add(position, convertView);
                senData(convertView,items.get(position));
            }
            ((TextView) convertView.findViewById(R.id.processNum)).setText(String.valueOf(position));
            ((TextView) convertView.findViewById(R.id.processType)).setText(items.get(position).type);
            ((TextView) convertView.findViewById(R.id.processStatus)).setText(items.get(position).status);
            ((Button) convertView.findViewById(R.id.processDel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    views.remove(position);
                    notifyDataSetChanged();
                }
            });
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

    String result;
    void senData(final View view,ProcessItem item) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    ((TextView)view.findViewById(R.id.processStatus)).setText(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                SoapObject soapObject = new SoapObject("http://WebXml.com.cn/", "getWeatherbyCityName");
                soapObject.addProperty("theCityName", "北京");

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                //SoapEnvelope envelope = new SoapEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = soapObject;
                //envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);

                HttpTransportSE httpTransport = new HttpTransportSE("http://www.webxml.com.cn/webservices/weatherwebservice.asmx");
                httpTransport.debug = true;

                try {
                    httpTransport.call("http://WebXml.com.cn/getWeatherbyCityName", envelope);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                try {
                    SoapObject detail1 = (SoapObject) envelope.getResponse();
                    result = detail1.toString();
                    handler.sendEmptyMessage(1);
                } catch (SoapFault soapFault) {
                    soapFault.printStackTrace();
                }

                SoapObject detail2 = (SoapObject) envelope.bodyIn;
                SoapObject detail2Prop = (SoapObject) detail2.getProperty("getWeatherbyCityNameResult");
            }
        }).start();
    }
}
