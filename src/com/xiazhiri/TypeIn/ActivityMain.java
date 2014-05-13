package com.xiazhiri.TypeIn;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ActivityMain extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView coord = ((TextView) findViewById(R.id.coord));
        final LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setBearingRequired(true);
        final String provider = locationManager.getBestProvider(criteria,true);
        //final String provider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER).getName();
        Log.i("GPS", "provider:" + provider);

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                coord.setText(location.getLatitude() + "," + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("GPS",provider + " Status: " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("GPS",provider + " Start");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("GPS",provider + " Stop");
            }
        };

        ((ToggleButton) findViewById(R.id.locateSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    locationManager.requestLocationUpdates(provider,200,0,locationListener);
                else
                    locationManager.removeUpdates(locationListener);
            }
        });
    }
}
