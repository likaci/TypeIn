package com.xiazhiri.TypeIn;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Administrator on 5.14 014.
 */
public class FragLine extends Fragment implements View.OnClickListener {
    ArrayList<String> pointList;
    ListView linePointListView;
    ArrayAdapter<String> adapter;
    LinePointListAdapter listAdapter;
    ActivityMain activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_line,container,false);
        linePointListView = (ListView)view.findViewById(R.id.linePointList);
        pointList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,pointList);
        listAdapter = new LinePointListAdapter(view.getContext(),R.layout.item_point,new ArrayList<Location>());
        linePointListView.setAdapter(listAdapter);
        view.findViewById(R.id.addPoint).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPoint :
                if (activity.pubLocation == null) {
                    Toast.makeText(getView().getContext(),"请先打开定位",Toast.LENGTH_SHORT).show();
                    break;
                }
                listAdapter.add(activity.pubLocation);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.cancel:
                activity.upPanelLayout.collapsePane();
                break;
        }
    }

    class LinePointListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        //Context context;
        int resID;
        ArrayList<Location> locationList;

        LinePointListAdapter(Context context, int resID, ArrayList<Location> locationList) {
            this.inflater = LayoutInflater.from(context);
            this.resID = resID;
            this.locationList = locationList;
        }

        @Override
        public int getCount() {
            return locationList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_point,null);
            }
            TextView tv = ((TextView) convertView.findViewById(R.id.pointNum));
            tv.setText(position + "");
            ((TextView) convertView.findViewById(R.id.pointCoord)).setText(locationList.get(position).getLatitude() + "," + locationList.get(position).getLongitude());
            return convertView;
        }

        public void add(Location location) {
            locationList.add(location);
        }
    }
}